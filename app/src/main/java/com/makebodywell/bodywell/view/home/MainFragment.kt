package com.makebodywell.bodywell.view.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.CalendarAdapter1
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentMainBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CalendarUtil.Companion.weekArray
import com.makebodywell.bodywell.util.CustomUtil.Companion.getExerciseCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.getFoodCalories
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class MainFragment : Fragment() {
   private var _binding: FragmentMainBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var adapter: CalendarAdapter1? = null
   private var pressedTime: Long = 0
   private var days = ArrayList<LocalDate?>()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            pressedTime = if(pressedTime == 0L) {
               Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
               System.currentTimeMillis()
            }else {
               val seconds = (System.currentTimeMillis() - pressedTime).toInt()
               if(seconds > 2000) {
                  Toast.makeText(requireActivity(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                  0
               }else {
                  requireActivity().finishAffinity()
                  System.runFinalization()
                  exitProcess(0)
               }
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMainBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.cl1.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      selectedDate = LocalDate.now()

      val getUser = dataManager.getUser()

      if(getUser.name != "") {
         binding.tvName.text = getUser.name + " 님"
      }

      if(getUser.profileImage != "") {
         binding.ivUser.setImageURI(Uri.parse(getUser.profileImage))
      }

      binding.clFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.clWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
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

      binding.clDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.ivCalendar.setOnClickListener {
         val calendarDialog = CalendarDialog(requireActivity())
         calendarDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         calendarDialog.window?.setGravity(Gravity.TOP)

         calendarDialog.setOnDismissListener {
            setWeekView()
            recordView()
         }

         calendarDialog.show()

         val window = calendarDialog.window
         window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      }

      val gestureListener: SwipeGesture = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)

      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : RecyclerItemClickListener.OnItemClickListener {
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!
            setWeekView()
            recordView()
         }
      }))

      setWeekView()
      recordView()

      return binding.root
   }

   fun setWeekView() {
      days = weekArray(selectedDate)
      adapter = CalendarAdapter1(days, 1)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter

      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvMonth.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))
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
                     recordView()
                  } else {
                     selectedDate = selectedDate.plusWeeks(1)
                     setWeekView()
                     recordView()
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

   fun recordView() {
      // 프로그래스바 초기화
      binding.pbFood.setProgressStartColor(Color.TRANSPARENT)
      binding.pbFood.setProgressEndColor(Color.TRANSPARENT)
      binding.pbWater.setProgressStartColor(Color.TRANSPARENT)
      binding.pbWater.setProgressEndColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressStartColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressEndColor(Color.TRANSPARENT)
      binding.pbBody.setProgressStartColor(Color.TRANSPARENT)
      binding.pbBody.setProgressEndColor(Color.TRANSPARENT)
      binding.pbSleep.setProgressStartColor(Color.TRANSPARENT)
      binding.pbSleep.setProgressEndColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressStartColor(Color.TRANSPARENT)
      binding.pbDrug.setProgressEndColor(Color.TRANSPARENT)

      // 프로그래스바 설정
      val getDailyGoal = dataManager.getDailyGoal(selectedDate.toString())
      val foodSum = getFoodCalories(requireActivity(), selectedDate.toString()).int5
      val getWater = dataManager.getWater(selectedDate.toString())
      val exerciseSum = getExerciseCalories(requireActivity(), selectedDate.toString())
      val getBody = dataManager.getBody(selectedDate.toString())
      val getSleep = dataManager.getSleep(selectedDate.toString())
      val getDrugCheckCount = dataManager.getDrugCheckCount(selectedDate.toString())

      if(foodSum > 0) {
         binding.pbFood.setProgressStartColor(Color.parseColor("#EE6685"))
         binding.pbFood.setProgressEndColor(Color.parseColor("#EE6685"))
         binding.pbFood.max = getDailyGoal.foodGoal
         binding.pbFood.progress = foodSum
      }

      if(getWater.water > 0) {
         binding.pbWater.setProgressStartColor(Color.parseColor("#4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#4AC0F2"))
         if(getDailyGoal.waterGoal > 0) {
            binding.pbWater.max = getDailyGoal.waterGoal
            binding.pbWater.progress = getWater.water
         }else if(getDailyGoal.waterGoal == 0) {
            binding.pbWater.max = getWater.water
            binding.pbWater.progress = getWater.water
         }
      }

      if(exerciseSum > 0) {
         binding.pbExercise.setProgressStartColor(Color.parseColor("#FA9B01"))
         binding.pbExercise.setProgressEndColor(Color.parseColor("#FA9B01"))
         if(getDailyGoal.exerciseGoal > 0) {
            binding.pbExercise.max = getDailyGoal.exerciseGoal
            binding.pbExercise.progress = exerciseSum
         }else if(getDailyGoal.exerciseGoal == 0) {
            binding.pbExercise.max = exerciseSum
            binding.pbExercise.progress = exerciseSum
         }
      }

      val weightGoalSplit = getDailyGoal.bodyGoal.toString().split(".")
      val weightGoal = if(weightGoalSplit[1] == "0") weightGoalSplit[0] else getDailyGoal.bodyGoal

      val weightSplit = getBody.weight.toString().split(".")
      val weight = if(weightSplit[1] == "0") weightSplit[0] else getBody.weight

      if(getBody.weight > 0) {
         binding.pbBody.setProgressStartColor(Color.parseColor("#88CB38"))
         binding.pbBody.setProgressEndColor(Color.parseColor("#88CB38"))
         if(getDailyGoal.bodyGoal > 0) {
            binding.pbBody.max = getDailyGoal.bodyGoal.roundToInt()
            binding.pbBody.progress = getBody.weight.toInt()
         }else if(getDailyGoal.bodyGoal == 0.0) {
            binding.pbBody.max = getBody.weight.toInt()
            binding.pbBody.progress = getBody.weight.toInt()
         }
      }

      val sleep = if(getDailyGoal.sleepGoal % 60 == 0) {
         "${getDailyGoal.sleepGoal / 60}h"
      }else {
         "${getDailyGoal.sleepGoal / 60}h${getDailyGoal.sleepGoal % 60}m"
      }

      if(getSleep.sleepTime > 0) {
         binding.pbSleep.setProgressStartColor(Color.parseColor("#667D99"))
         binding.pbSleep.setProgressEndColor(Color.parseColor("#667D99"))
         if(getDailyGoal.sleepGoal > 0) {
            binding.pbSleep.max = getDailyGoal.sleepGoal
            binding.pbSleep.progress = getSleep.sleepTime
         }else if(getDailyGoal.sleepGoal == 0) {
            binding.pbSleep.max = getSleep.sleepTime
            binding.pbSleep.progress = getSleep.sleepTime
         }
      }

      if(getDrugCheckCount > 0) {
         binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
         binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
         if(getDailyGoal.drugGoal > 0) {
            binding.pbDrug.max = getDailyGoal.drugGoal
            binding.pbDrug.progress = getDrugCheckCount
         }else if(getDailyGoal.drugGoal == 0) {
            binding.pbDrug.max = getDrugCheckCount
            binding.pbDrug.progress = getDrugCheckCount
         }
      }

      binding.tvFood.text = "$foodSum/${getDailyGoal.foodGoal} kcal"
      binding.tvWater.text = "${getWater.water}/${getDailyGoal.waterGoal}잔"
      binding.tvExercise.text = "$exerciseSum/${getDailyGoal.exerciseGoal} kcal"
      binding.tvBody.text = "$weight/$weightGoal kg"
      binding.tvSleep.text = "${getSleep.sleepTime / 60}h${getSleep.sleepTime % 60}m/$sleep"
      binding.tvDrug.text = "$getDrugCheckCount/${getDailyGoal.drugGoal}회"
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