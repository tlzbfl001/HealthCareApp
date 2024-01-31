package com.makebodywell.bodywell.view.note

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter1
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodKcal
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_PERMISSION_3
import com.makebodywell.bodywell.view.home.food.GalleryFragment
import java.time.LocalDate
import kotlin.math.abs

class NoteFragment : Fragment() {
   private var _binding: FragmentNoteBinding? = null
   private val binding get() = _binding!!

   private val bundle = Bundle()

   private var dataManager: DataManager? = null

   private var days = ArrayList<LocalDate?>()

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
      val data = arguments?.getString("data").toString()
      if(data != "note") {
         selectedDate = LocalDate.now()
      }

      binding.ivWrite.setOnClickListener {
         replaceFragment1(requireActivity(), NoteWriteFragment())
      }

      binding.clWrite.setOnClickListener {
         replaceFragment1(requireActivity(), NoteWriteFragment())
      }

      binding.ivPrev.setOnClickListener {
         selectedDate = selectedDate.minusWeeks(1)
         setWeekView()
      }

      binding.ivNext.setOnClickListener {
         selectedDate = selectedDate.plusWeeks(1)
         setWeekView()
      }

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
               selectedDate = days[position]!!
               setWeekView()

               // 섭취 칼로리 계산
               val foodKcal = getFoodKcal(requireActivity(), selectedDate.toString())
               binding.tvKcal1.text = "${foodKcal.int5} kcal"

               // 소비 칼로리 계산
               var total = 0
               val getExercise = dataManager!!.getExercise(selectedDate.toString())
               for(i in 0 until getExercise.size) {
                  total += getExercise[i].calories
               }
               binding.tvKcal2.text = "$total kcal"
            }
         })
      )

      binding.clGallery.setOnClickListener {
         if(requestPermission()) {
            bundle.putString("type", "5")
            bundle.putString("calendarDate", selectedDate.toString())
            replaceFragment2(requireActivity(), GalleryFragment(), bundle)
         }
      }

      setWeekView()

      return binding.root
   }

   private fun setWeekView() {
      // 텍스트 초기화
      binding.tvCalTitle.text = calendarTitle()
      binding.tvDate.text = dateFormat(selectedDate)

      val getNote = dataManager!!.getNote(selectedDate.toString())
      if(getNote.string1 != "") {
         binding.tvNoteTitle.text = getNote.string1
      }else {
         binding.tvNoteTitle.text = "제목."
      }

      // 달력 설정
      days = weekArray(selectedDate)
      val adapter = CalendarAdapter1(days)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter

      // 스와이프 설정
      val gestureListener = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)
      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      // 이미지뷰
      val dataList = dataManager!!.getImage(5, selectedDate.toString())
      val photoAdapter = PhotoSlideAdapter(requireActivity(), dataList)
      binding.viewPager.adapter = photoAdapter
      binding.viewPager.setPadding(180, 0, 180, 0)
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
                     setWeekView()
                  } else {
                     selectedDate = selectedDate.plusWeeks(1)
                     setWeekView()
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

   private fun requestPermission(): Boolean {
      var check = true
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         for(permission in CAMERA_PERMISSION_3) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_3), REQUEST_CODE)
               check = false
            }
         }
      }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         for(permission in CAMERA_PERMISSION_2) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_2), REQUEST_CODE)
               check = false
            }
         }
      }else {
         for(permission in CAMERA_PERMISSION_1) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*CAMERA_PERMISSION_1), REQUEST_CODE)
               check = false
            }
         }
      }
      return check
   }

   companion object {
      private const val REQUEST_CODE = 1
      private const val SWIPE_THRESHOLD = 100
      private const val SWIPE_VELOCITY_THRESHOLD = 100
   }
}