package kr.bodywell.android.view.note

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.adapter.CalendarAdapter2
import kr.bodywell.android.adapter.PhotoSlideAdapter
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentNoteBinding
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.saveFile
import kr.bodywell.android.view.home.MainFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var days = ArrayList<LocalDate?>()
   private var dialog: Dialog? = null
   private var uri:Uri? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentNoteBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      dataManager = DataManager(activity)
      dataManager.open()

      // 날짜 초기화
      if(arguments?.getString("data").toString() != "note") selectedDate = LocalDate.now()

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
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!
            setDailyView()
         }
      }))

      // 카메라 설정
//      binding.clCamera.setOnClickListener {
//         if(cameraRequest(requireActivity())) {
//            dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
//            val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)
//
//            val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
//            val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)
//
//            clCamera.setOnClickListener {
//               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//               startActivityForResult(intent, CAMERA_REQUEST_CODE)
//            }
//
//            clPhoto.setOnClickListener {
//               val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//               intent.type = "image/*"
//               startActivityForResult(intent, STORAGE_REQUEST_CODE)
//            }
//
//            dialog!!.setContentView(bottomSheetView)
//            dialog!!.show()
//         }
//      }

      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager

      setDailyView()

      return binding.root
   }

   private fun setDailyView() {
      // 텍스트 초기화
      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvYearText.text = selectedDate.month.toString()

      val getNote = dataManager.getNote(selectedDate.toString())

      if(getNote.createdAt != "") {
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

      // 섭취 칼로리 계산
      val foodKcal = getFoodCalories(requireActivity(), selectedDate.toString())
      binding.tvKcal1.text = "${foodKcal.int5} kcal"

      // 소비 칼로리 계산
      var total = 0
      val getDailyExercise = dataManager.getDailyExercise(CREATED_AT, selectedDate.toString())

      for(i in 0 until getDailyExercise.size) total += getDailyExercise[i].kcal

      binding.tvKcal2.text = "$total kcal"

//      setImageView()
   }

   private fun setImageView() {
      val dataList = dataManager.getImage("5", selectedDate.toString())
      val photoAdapter = PhotoSlideAdapter(requireActivity(), dataList)
      binding.viewPager.adapter = photoAdapter
      binding.viewPager.setPadding(140, 0, 140, 0)
   }

   inner class SwipeGesture(v: View) : GestureDetector.OnGestureListener {
      override fun onDown(p0: MotionEvent): Boolean {
         return false
      }
      override fun onShowPress(p0: MotionEvent) {}
      override fun onSingleTapUp(p0: MotionEvent): Boolean {
         return false
      }

      override fun onScroll(
         e1: MotionEvent?,
         e2: MotionEvent,
         distanceX: Float,
         distanceY: Float
      ): Boolean {
         return false
      }

      override fun onLongPress(p0: MotionEvent) {}
      override fun onFling(
         e1: MotionEvent?,
         e2: MotionEvent,
         velocityX: Float,
         velocityY: Float
      ): Boolean {var result = false
         try {
            val diffY = e2.y - e1!!.y
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
                  uri = saveFile(requireActivity(), "image/jpeg", img)

                  dataManager.insertImage(Image(type = "5", imageUri = uri.toString(), createdAt = selectedDate.toString()))
                  setImageView()

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               uri = data!!.data

               val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
               requireActivity().contentResolver.takePersistableUriPermission(uri!!, takeFlags)

               dataManager.insertImage(Image(type = "5", imageUri = uri.toString(), createdAt = selectedDate.toString()))
               setImageView()

               dialog!!.dismiss()
            }
         }
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}