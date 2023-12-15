package com.makebodywell.bodywell.view.note

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter1
import com.makebodywell.bodywell.adapter.PhotoSlideAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentNoteBinding
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainFragment
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

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      return binding.root
   }

   private fun initView() {
      // 날짜 초기화
      val data = arguments?.getString("data").toString()
      if(data != "noteData") {
         selectedDate = LocalDate.now()
      }

      setWeekView()

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

      // 클릭이벤트 설정
      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
               selectedDate = days[position]!!
               setWeekView()
            }
         })
      )

      binding.clGallery.setOnClickListener {
         bundle.putString("type", "note")
         replaceFragment2(requireActivity(), GalleryFragment(), bundle)
      }
   }

   private fun setWeekView() {
      // 텍스트 초기화
      binding.tvCalTitle.text = calendarTitle(selectedDate)
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
      val gestureListener: SwipeGesture = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)
      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      // 이미지뷰
      val dataList = dataManager!!.getImage("note", selectedDate.toString())
      val itemList = ArrayList<Image>()
      for (i in 0 until dataList.size) {
         itemList.add(Image(id = dataList[i].id, imageUri = dataList[i].imageUri, type = "note", regDate = selectedDate.toString()))
      }

      val calendarPhotoAdapter = PhotoSlideAdapter(requireActivity(), itemList)
      binding.viewPager.adapter = calendarPhotoAdapter
      binding.viewPager.setPadding(180, 0, 180, 0)
   }

   inner class SwipeGesture(v: View) : GestureDetector.OnGestureListener {
      private val swipeThreshold = 100
      private val swipeVelocityThreshold = 100

      override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
         var result = false
         try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
               if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
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

   class RecyclerItemClickListener(context: Context, private val listener: OnItemClickListener?) : RecyclerView.OnItemTouchListener {
      private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
         override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
         }
      })

      interface OnItemClickListener {
         fun onItemClick(view: View, position: Int)
      }

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
}