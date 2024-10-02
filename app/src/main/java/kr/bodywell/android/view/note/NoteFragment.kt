package kr.bodywell.android.view.note

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.android.R
import kr.bodywell.android.adapter.CalendarAdapter2
import kr.bodywell.android.adapter.PhotoSlideAdapter
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.NOTE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentNoteBinding
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import kr.bodywell.android.view.home.MainFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
   private var days = ArrayList<LocalDate?>()
   private var type = "NOTE"

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

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){

      }

      dataManager = DataManager(activity)
      dataManager.open()

      // 날짜 초기화
      if(arguments?.getString("data").toString() != NOTE) selectedDate = LocalDate.now()

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

      binding.clGallery.setOnClickListener {
         if(checkCameraPermission(requireActivity())) {
            replaceFragment1(requireActivity(), GalleryFragment())
         }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_2)
            }else {
               pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_1)
            }
         }
      }

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
      binding.tvKcal1.text = "${foodKcal.int5}kcal"

      // 소비 칼로리 계산
      var total = 0
      val getDailyExercise = dataManager.getDailyExercise(CREATED_AT, selectedDate.toString())

      for(i in 0 until getDailyExercise.size) total += getDailyExercise[i].kcal

      binding.tvKcal2.text = "${total}kcal"

      setImageView()
   }

   private fun setImageView() {
      val dataList = dataManager.getImage(type, selectedDate.toString())
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

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }

   companion object {
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}