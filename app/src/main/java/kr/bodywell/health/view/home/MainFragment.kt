package kr.bodywell.health.view.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.bodywell.health.adapter.CalendarAdapter1
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.databinding.FragmentMainBinding
import kr.bodywell.health.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.health.util.CalendarUtil.Companion.weekArray
import kr.bodywell.health.util.CustomUtil.Companion.dataType
import kr.bodywell.health.util.CustomUtil.Companion.getExerciseCalories
import kr.bodywell.health.util.CustomUtil.Companion.getFoodCalories
import kr.bodywell.health.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.health.util.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class MainFragment : Fragment() {
   private var _binding: FragmentMainBinding? = null
   val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private val viewModel: MainViewModel by activityViewModels()
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
      viewModel.setDate()

      val getUser = dataManager.getUser()

      if(getUser.name != "") binding.tvName.text = getUser.name + " 님"

      if(getUser.image != "") binding.ivUser.setImageURI(Uri.parse(getUser.image))

      binding.clFood.setOnClickListener {
         dataType = 1
         replaceFragment1(requireActivity(), DetailFragment())
      }

      binding.clWater.setOnClickListener {
         dataType = 2
         replaceFragment1(requireActivity(), DetailFragment())
      }

      binding.clExercise.setOnClickListener {
         dataType = 3
         replaceFragment1(requireActivity(), DetailFragment())
      }

      binding.clBody.setOnClickListener {
         dataType = 4
         replaceFragment1(requireActivity(), DetailFragment())
      }

      binding.clSleep.setOnClickListener {
         dataType = 5
         replaceFragment1(requireActivity(), DetailFragment())
      }

      binding.clDrug.setOnClickListener {
         dataType = 6
         replaceFragment1(requireActivity(), DetailFragment())
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
      ): Boolean {
         var result = false
         try {
            val diffY = e2.y - e1!!.y
            val diffX = e2.x - e1.x
            if (abs(diffX) > abs(diffY)) {
               if (abs(diffX) > 100 && abs(velocityX) > 100) {
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

      // 데이터 설정
      val getDailyGoal = dataManager.getGoal(selectedDate.toString())
      val foodSum = getFoodCalories(requireActivity(), selectedDate.toString()).int5
      val getWater = dataManager.getWater(selectedDate.toString())
      val exerciseSum = getExerciseCalories(requireActivity(), selectedDate.toString())
      val getBody = dataManager.getBody(selectedDate.toString())
      val getSleep = dataManager.getSleep(selectedDate.toString())
      val getDrugCheckCount = dataManager.getDrugCheckCount(selectedDate.toString())

      if(foodSum > 0) {
         binding.pbFood.setProgressStartColor(Color.parseColor("#BFE24F5C"))
         binding.pbFood.setProgressEndColor(Color.parseColor("#BFE24F5C"))
         binding.pbFood.max = getDailyGoal.food
         binding.pbFood.progress = foodSum
      }

      if(getWater.count > 0) {
         binding.pbWater.setProgressStartColor(Color.parseColor("#CC4AC0F2"))
         binding.pbWater.setProgressEndColor(Color.parseColor("#CC4AC0F2"))
         if(getDailyGoal.water > 0) {
            binding.pbWater.max = getDailyGoal.water
            binding.pbWater.progress = getWater.count
         }else if(getDailyGoal.water == 0) {
            binding.pbWater.max = getWater.count
            binding.pbWater.progress = getWater.count
         }
      }

      if(exerciseSum > 0) {
         binding.pbExercise.setProgressStartColor(Color.parseColor("#CCF6B44B"))
         binding.pbExercise.setProgressEndColor(Color.parseColor("#CCF6B44B"))
         if(getDailyGoal.exercise > 0) {
            binding.pbExercise.max = getDailyGoal.exercise
            binding.pbExercise.progress = exerciseSum
         }else if(getDailyGoal.exercise == 0) {
            binding.pbExercise.max = exerciseSum
            binding.pbExercise.progress = exerciseSum
         }
      }

      val weightGoalSplit = getDailyGoal.body.toString().split(".")
      val weightGoal = if(weightGoalSplit[1] == "0") weightGoalSplit[0] else getDailyGoal.body

      val weightSplit = getBody.weight.toString().split(".")
      val weight = if(weightSplit[1] == "0") weightSplit[0] else getBody.weight

      if(getBody.weight > 0) {
         binding.pbBody.setProgressStartColor(Color.parseColor("#B8E189"))
         binding.pbBody.setProgressEndColor(Color.parseColor("#B8E189"))
         if(getDailyGoal.body > 0) {
            binding.pbBody.max = getDailyGoal.body.roundToInt()
            binding.pbBody.progress = getBody.weight.toInt()
         }else if(getDailyGoal.body == 0.0) {
            binding.pbBody.max = getBody.weight.toInt()
            binding.pbBody.progress = getBody.weight.toInt()
         }
      }

      val sleep = if(getDailyGoal.sleep % 60 == 0) "${getDailyGoal.sleep / 60}h" else "${getDailyGoal.sleep / 60}h${getDailyGoal.sleep % 60}m"

      if(getSleep.total > 0) {
         binding.pbSleep.setProgressStartColor(Color.parseColor("#667D99"))
         binding.pbSleep.setProgressEndColor(Color.parseColor("#667D99"))
         if(getDailyGoal.sleep > 0) {
            binding.pbSleep.max = getDailyGoal.sleep
            binding.pbSleep.progress = getSleep.total
         }else if(getDailyGoal.sleep == 0) {
            binding.pbSleep.max = getSleep.total
            binding.pbSleep.progress = getSleep.total
         }
      }

      if(getDrugCheckCount > 0) {
         binding.pbDrug.setProgressStartColor(Color.parseColor("#9F76DF"))
         binding.pbDrug.setProgressEndColor(Color.parseColor("#9F76DF"))
         if(getDailyGoal.drug > 0) {
            binding.pbDrug.max = getDailyGoal.drug
            binding.pbDrug.progress = getDrugCheckCount
         }else if(getDailyGoal.drug == 0) {
            binding.pbDrug.max = getDrugCheckCount
            binding.pbDrug.progress = getDrugCheckCount
         }
      }

      binding.tvFood.text = "$foodSum/${getDailyGoal.food} kcal"
      binding.tvWater.text = "${getWater.count}/${getDailyGoal.water}잔"
      binding.tvExercise.text = "$exerciseSum/${getDailyGoal.exercise} kcal"
      binding.tvBody.text = "$weight/$weightGoal kg"
      binding.tvSleep.text = "${getSleep.total / 60}h${getSleep.total % 60}m/$sleep"
      binding.tvDrug.text = "$getDrugCheckCount/${getDailyGoal.drug}회"
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}