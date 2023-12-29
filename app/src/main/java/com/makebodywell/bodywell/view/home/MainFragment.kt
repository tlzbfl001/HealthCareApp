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
import com.makebodywell.bodywell.model.Water
import com.makebodywell.bodywell.util.CalendarUtil.Companion.calendarTitle
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.getExerciseCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodKcal
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.CalendarDialog
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

class MainFragment : Fragment() {
   private var _binding: FragmentMainBinding? = null
   private val binding get() = _binding!!

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

      return binding.root
   }

   private fun initView() {
      binding.btnCalPrev.setOnClickListener {
         selectedDate = selectedDate.minusWeeks(1)
         setWeekView()
      }

      binding.btnCalNext.setOnClickListener {
         selectedDate = selectedDate.plusWeeks(1)
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

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : RecyclerItemClickListener.OnItemClickListener {
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!
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
      binding.tvCalTitle.text = calendarTitle()
      days = weekArray(selectedDate)
      val adapter = CalendarAdapter1(days)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter

      val gestureListener: SwipeGesture = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)
      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      // 차트 값 지정
      recordView()
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

   private fun recordView() {
      // 프로그래스바 초기화
      binding.pbFood.progress = 0
      binding.pbWater.progress = 0
      binding.pbExercise.progress = 0
      binding.pbBody.progress = 0

      // 프로그래스바 설정
      val getDailyData = dataManager!!.getDailyData(selectedDate.toString())
      val foodSum = getFoodKcal(requireActivity(), selectedDate.toString()).int5!!
      val getWater = dataManager!!.getWater(selectedDate.toString())
      val exerciseSum = getExerciseCalories(requireActivity(), selectedDate.toString())
      val getBody = dataManager!!.getBody(selectedDate.toString())

      if(getDailyData.foodGoal > 0 && foodSum > 0) {
         binding.pbFood.max = getDailyData.foodGoal
         binding.pbFood.progress = foodSum
      }else if(getDailyData.foodGoal == 0 && foodSum > 0) {
         binding.pbFood.max = foodSum
         binding.pbFood.progress = foodSum
      }

      if(getDailyData.waterGoal > 0 && getWater.water > 0) {
         binding.pbWater.max = getDailyData.waterGoal
         binding.pbWater.progress = getWater.water
      }else if(getDailyData.foodGoal == 0 && getWater.water > 0) {
         binding.pbWater.max = getWater.water
         binding.pbWater.progress = getWater.water
      }

      if(getDailyData.exerciseGoal > 0 && exerciseSum > 0) {
         binding.pbExercise.max = getDailyData.exerciseGoal
         binding.pbExercise.progress = exerciseSum
      }else if(getDailyData.exerciseGoal == 0 && exerciseSum > 0) {
         binding.pbExercise.max = exerciseSum
         binding.pbExercise.progress = exerciseSum
      }

      val weightSplit = getBody.weight.toString().split(".")
      val bodyGoalSplit = getDailyData.bodyGoal.toString().split(".")

      val weight = when(weightSplit[1]) {
         "0" -> weightSplit[0]
         else -> getBody.weight
      }
      val bodyGoal = when(bodyGoalSplit[1]) {
         "0" -> bodyGoalSplit[0]
         else -> getDailyData.bodyGoal
      }

      if(getDailyData.bodyGoal > 0 && getBody.weight > 0) {
         binding.pbBody.max = getDailyData.bodyGoal.roundToInt()
         binding.pbBody.progress = getBody.weight.toInt()
      }else if(getDailyData.foodGoal == 0 && getBody.weight > 0) {
         binding.pbBody.max = getBody.weight.toInt()
         binding.pbBody.progress = getBody.weight.toInt()
      }

      binding.tvFood.text = "$foodSum/${getDailyData.foodGoal} kcal"
      binding.tvWater.text = "${getWater.water}/${getDailyData.waterGoal}잔"
      binding.tvExercise.text = "$exerciseSum/${getDailyData.exerciseGoal} kcal"
      binding.tvBody.text = "$weight/$bodyGoal kg"
   }
}