package com.makebodywell.bodywell.view.note

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.CalendarAdapter2
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteBinding
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.randomFileName
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var days = ArrayList<LocalDate?>()
   private var dialog: Dialog? = null
   private var uri:Uri? = null

   @SuppressLint("DiscouragedApi", "InternalInsetResource", "ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      // 날짜 초기화
      if(arguments?.getString("data").toString() != "note") {
         selectedDate = LocalDate.now()
      }

      setDailyView()

      binding.clExpand.setOnClickListener {
         val dialog = NoteCalendarDialog(requireActivity())
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.window?.setGravity(Gravity.TOP)

         dialog.setOnDismissListener {
            setDailyView()
         }

         dialog.show()

         val window = dialog.window
         window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      }

      binding.ivWrite.setOnClickListener {
         replaceFragment1(requireActivity(), NoteWriteFragment())
      }

      binding.clWrite.setOnClickListener {
         replaceFragment1(requireActivity(), NoteWriteFragment())
      }

      val gestureListener = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)

      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : OnItemClickListener {
         @SuppressLint("SetTextI18n")
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!

            // 섭취 칼로리 계산
            val foodKcal = getFoodCalories(requireActivity(), selectedDate.toString())
            binding.tvKcal1.text = "${foodKcal.int5} kcal"
            Log.d(TAG, "foodKcal: ${foodKcal.int5}")

            // 소비 칼로리 계산
            var total = 0
            val getExercise = dataManager!!.getExercise(selectedDate.toString())
            for(i in 0 until getExercise.size) {
               total += getExercise[i].calories
            }
            Log.d(TAG, "getExercise: $getExercise")

            binding.tvKcal2.text = "$total kcal"

            setDailyView()
         }
      }))

      // 카메라 설정
      binding.clCamera.setOnClickListener {
         if(cameraRequest(requireActivity())) {
            dialog = Dialog(requireActivity())
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.dialog_gallery)

            val clCamera = dialog!!.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = dialog!!.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }

            clGallery.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK)
               intent.type = MediaStore.Images.Media.CONTENT_TYPE
               startActivityForResult(intent, STORAGE_REQUEST_CODE)
            }

            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setGravity(Gravity.BOTTOM)
            dialog!!.show()
         }
      }

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager

      return binding.root
   }

   @SuppressLint("SetTextI18n")
   private fun setDailyView() {
      // 텍스트 초기화
      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvYearText.text = selectedDate.month.toString()

      val getNote = dataManager!!.getNote(selectedDate.toString())
      if(getNote.regDate != "") {
         binding.tvTitle.text = getNote.title
         when(getNote.status) {
            1 -> binding.ivFace.setImageResource(R.drawable.face1)
            2 -> binding.ivFace.setImageResource(R.drawable.face2)
            3 -> binding.ivFace.setImageResource(R.drawable.face3)
            4 -> binding.ivFace.setImageResource(R.drawable.face4)
            5 -> binding.ivFace.setImageResource(R.drawable.face5)
         }
      }else {
         binding.tvTitle.text = "제목"
         binding.ivFace.setImageResource(R.drawable.face1)
      }

      // 달력 설정
      days = weekArray(selectedDate)
      val adapter = CalendarAdapter2(days)
      binding.recyclerView.adapter = adapter

      val foodKcal = getFoodCalories(requireActivity(), selectedDate.toString())
      binding.tvKcal1.text = "${foodKcal.int5} kcal"

      var total = 0
      val getExercise = dataManager!!.getExercise(selectedDate.toString())
      for(i in 0 until getExercise.size) {
         total += getExercise[i].calories
      }
      binding.tvKcal2.text = "$total kcal"

      setImageView()
   }

   private fun setImageView() {
      val dataList = dataManager!!.getImage(5, selectedDate.toString())
      val photoAdapter = PhotoSlideAdapter(requireActivity(), dataList)
      binding.viewPager.adapter = photoAdapter
      binding.viewPager.setPadding(140, 0, 140, 0)
   }

   inner class SwipeGesture(v: View) : GestureDetector.OnGestureListener {
      override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
         var result = false
         try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
               if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                  if (diffX > 0) {
                     selectedDate = selectedDate.minusWeeks(1)
                     setDailyView()
                  } else {
                     selectedDate = selectedDate.plusWeeks(1)
                     setDailyView()
                  }
               }
            }
            result = true
         } catch (exception: Exception) {
            exception.printStackTrace()
         }
         return result
      }

      override fun onDown(p0: MotionEvent): Boolean {
         return false
      }
      override fun onShowPress(p0: MotionEvent) {}
      override fun onSingleTapUp(p0: MotionEvent): Boolean {
         return false
      }
      override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
         return false
      }
      override fun onLongPress(p0: MotionEvent) {}
   }

   interface OnItemClickListener {
      fun onItemClick(view: View, position: Int)
   }

   inner class RecyclerItemClickListener(context: Context, private val listener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
      private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
         override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
         }
      })

      override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
         val childView = view.findChildViewUnder(e.x, e.y)
         if (childView != null && listener != null && mGestureDetector.onTouchEvent(e)) {
            try {
               listener.onItemClick(childView, view.getChildAdapterPosition(childView))
            } catch (e: Exception) {
               e.printStackTrace()
            }
            return true
         }
         return false
      }

      override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
      override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            CAMERA_REQUEST_CODE -> {
               if(data?.extras?.get("data") != null){
                  val img = data.extras?.get("data") as Bitmap
                  uri = saveFile(requireActivity(), randomFileName(), "image/jpeg", img)

                  dataManager!!.insertImage(Image(imageUri = uri.toString(), type = 5, regDate = selectedDate.toString()))
                  setImageView()

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               uri = data!!.data
               if(data.data!!.toString().contains("com.google.android.apps.photos.contentprovider")) {
                  val uriParse = getImageUriWithAuthority(requireActivity(), uri)
                  dataManager!!.insertImage(Image(imageUri = uriParse!!, type = 5, regDate = selectedDate.toString()))
                  setImageView()
               }else {
                  dataManager!!.insertImage(Image(imageUri = uri.toString(), type = 5, regDate = selectedDate.toString()))
                  setImageView()
               }

               dialog!!.dismiss()
            }
         }
      }
   }

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}