package com.makebodywell.bodywell.view.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter1
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentMainBinding
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodIntake
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import kotlin.math.abs

class MainFragment : Fragment() {
   private var _binding: FragmentMainBinding? = null
   private val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null

   var days = ArrayList<LocalDate?>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMainBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      // 달력 설정
      setupCalendar()

      // 차트 값 지정
      setupChart(calendarDate.toString())

      return binding.root
   }

   private fun initView() {
      binding.btnCalPrev.setOnClickListener {
         selectedDate = selectedDate!!.minusWeeks(1)
         setWeekView()
      }

      binding.btnCalNext.setOnClickListener {
         selectedDate = selectedDate!!.plusWeeks(1)
         setWeekView()
      }

      binding.clFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.clExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.clBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.clSleep.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.clWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }

      binding.clDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }
   }

   private fun setupCalendar() {
      selectedDate = LocalDate.now()
      setWeekView()

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), binding.recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]
            setWeekView()
         }
      }))

      binding.ivCalendar.setOnClickListener {
         val calendarDialog = CalendarDialog(requireActivity())
         calendarDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         calendarDialog.window?.setGravity(Gravity.TOP)
         calendarDialog.show()

         val window = calendarDialog.window
         window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      }
   }

   @SuppressLint("ClickableViewAccessibility")
   private fun setWeekView() {
      binding.tvCalTitle.text = CalendarUtil.dateTitle(selectedDate!!)
      days = CalendarUtil.weekArray(selectedDate!!)
      val adapter = CalendarAdapter1(days)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter

      val gestureListener: SwipeGesture = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)
      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }
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
                     selectedDate = selectedDate!!.minusWeeks(1)
                     setWeekView()
                  } else {
                     selectedDate = selectedDate!!.plusWeeks(1)
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

   class RecyclerItemClickListener(context: Context, recyclerView: RecyclerView, private val listener: OnItemClickListener?)
      : RecyclerView.OnItemTouchListener {
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

   private fun setupChart(date: String) {
      val getDailyData = dataManager!!.getDailyData(date)
      val sum = getFoodIntake(requireActivity(), date)
      if (getDailyData.foodGoal > 0 && sum > 0) {
         binding.pbFood.max = getDailyData.foodGoal
         binding.pbFood.progress = sum
      }else if(getDailyData.foodGoal == 0 && sum > 0) {
         binding.pbFood.max = sum
         binding.pbFood.progress = sum
      }
      binding.tvFood.text = "$sum/${getDailyData.foodGoal} kcal"
   }
}