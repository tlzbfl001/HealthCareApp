package kr.bodywell.android.view.home

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kr.bodywell.android.adapter.CalendarAdapter1
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentMainBinding
import kr.bodywell.android.model.Constants.FILES
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.getExerciseCalories
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.getUser
import kr.bodywell.android.util.CustomUtil.layoutType
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.MainViewModel
import java.io.File
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
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
   private var days = ArrayList<LocalDate?>()
   private var pressedTime: Long = 0
   private var starts = LocalDateTime.now()
   private var ends = LocalDateTime.now()

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

   @RequiresApi(Build.VERSION_CODES.R)
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMainBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.cl1)

      dataManager = DataManager(requireActivity())
      dataManager.open()

      selectedDate = LocalDate.now()
      viewModel.setDate()

      // 프로필 설정
      lifecycleScope.launch {
         val getProfile = powerSync.getProfile(getUser.uid)

         val getFile = powerSync.getFile(getProfile.id)
         if(getFile.name != "") {
            powerSync.deleteDuplicate(FILES, "profile_id", getProfile.id, getFile.id)
            val imgPath = requireActivity().filesDir.toString() + "/" + getFile.name
            val file = File(imgPath)
            if(file.exists()){
               val bm = BitmapFactory.decodeFile(imgPath)
               binding.ivUser.setImageBitmap(bm)
            }
         }

         if(getProfile.name != "") binding.tvName.text = getProfile.name + " 님"
      }

      binding.cvFood.setOnClickListener {
         layoutType = 1
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.cvWater.setOnClickListener {
         layoutType = 2
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.cvExercise.setOnClickListener {
         layoutType = 3
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.cvBody.setOnClickListener {
         layoutType = 4
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.cvSleep.setOnClickListener {
         layoutType = 5
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.cvDrug.setOnClickListener {
         layoutType = 6
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.ivCalendar.setOnClickListener {
         val calendarDialog = CalendarDialog(requireActivity())
         calendarDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         calendarDialog.window?.setGravity(Gravity.TOP)

         calendarDialog.setOnDismissListener {
            dailyView()
         }

         calendarDialog.show()

         val window = calendarDialog.window
         window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
      }

      val gestureListener = SwipeGesture(binding.recyclerView)
      val gestureDetector = GestureDetector(requireActivity(), gestureListener)

      binding.recyclerView.setOnTouchListener { _, event ->
         return@setOnTouchListener gestureDetector.onTouchEvent(event)
      }

      binding.recyclerView.addOnItemTouchListener(RecyclerItemClickListener(requireActivity(), object : RecyclerItemClickListener.OnItemClickListener {
         override fun onItemClick(view: View, position: Int) {
            selectedDate = days[position]!!
            dailyView()
         }
      }))

      dailyView()

      return binding.root
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
                     dailyView()
                  } else {
                     selectedDate = selectedDate.plusWeeks(1)
                     dailyView()
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

   fun dailyView() {
      // 달력 설정
      days = weekArray(selectedDate)
      adapter = CalendarAdapter1(days, 1)
      val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter
      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvMonth.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))

      // 데이터 초기화
      binding.pbFood.progress = 0
      binding.pbWater.progress = 0
      binding.pbExercise.progress = 0
      binding.pbBody.progress = 0
      binding.pbSleep.progress = 0
      binding.pbDrug.progress = 0
      binding.tvFoodPt.text = "0%"
      binding.tvWaterPt.text = "0%"
      binding.tvExercisePt.text = "0%"
      binding.tvBodyPt.text = "0%"
      binding.tvSleepPt.text = "0%"
      binding.tvDrugPt.text = "0%"

      lifecycleScope.launch {
         // 목표 달성 데이터 설정
         val getGoal = powerSync.getGoal(selectedDate.toString())
         val foodSum = getFoodCalories(selectedDate.toString()).int5
         val getWater = powerSync.getWater(selectedDate.toString())
         val exerciseSum = getExerciseCalories(selectedDate.toString())
         val getBody = powerSync.getBody(selectedDate.toString())
         val getSleep = powerSync.getSleep(selectedDate.toString())
         val getIntake = powerSync.getRecentlyIntakes(selectedDate.toString())

         if(foodSum > 0) {
            binding.tvFoodPt.text = if(getGoal.kcalOfDiet > 0) "${((foodSum * 100) / getGoal.kcalOfDiet)}%" else "100%"
            binding.pbFood.max = if(getGoal.kcalOfDiet > 0) getGoal.kcalOfDiet else foodSum
            binding.pbFood.progress = foodSum
         }

         if(getWater.count > 0) {
            binding.tvWaterPt.text = if(getGoal.waterIntake > 0) "${(getWater.count * 100) / getGoal.waterIntake}%" else "100%"
            binding.pbWater.max = if(getGoal.waterIntake > 0) getGoal.waterIntake else getWater.count
            binding.pbWater.progress = getWater.count
         }

         if(exerciseSum > 0) {
            binding.tvExercisePt.text = if(getGoal.kcalOfWorkout > 0) "${(exerciseSum * 100) / getGoal.kcalOfWorkout}%" else "100%"
            binding.pbExercise.max = if(getGoal.kcalOfWorkout > 0) getGoal.kcalOfWorkout else exerciseSum
            binding.pbExercise.progress = exerciseSum
         }

         val weightGoalSplit = getGoal.weight.toString().split(".")
         val weightGoal = if(weightGoalSplit[1] == "0") weightGoalSplit[0] else getGoal.weight

         var weight = "0"
         if(getBody.weight != null) {
            val weightSplit = getBody.weight.toString().split(".")
            weight = if(weightSplit[1] == "0") weightSplit[0] else getBody.weight.toString()
         }

         if(getBody.weight != null && getBody.weight!! > 0) {
            binding.tvBodyPt.text =if(getGoal.weight > 0)  "${(getBody.weight!!.toInt() * 100) / getGoal.weight.roundToInt()}%" else "100%"
            binding.pbBody.max = if(getGoal.weight > 0) getGoal.weight.roundToInt() else getBody.weight!!.toInt()
            binding.pbBody.progress = getBody.weight!!.toInt()
         }

         val sleep = if(getGoal.sleep % 60 == 0) "${getGoal.sleep / 60}h" else "${getGoal.sleep / 60}h${getGoal.sleep % 60}m"

         if(getSleep.starts != "" && getSleep.ends!= "") {
            if(getSleep.starts.contains("+09:00")) {
               starts = CustomUtil.isoToDateTime(getSleep.starts)
               ends = CustomUtil.isoToDateTime(getSleep.ends)
            }else {
               val split1 = getSleep.starts.split(".", limit=2)
               val replace1 = split1[0].replace("T", " ")
               val split2 = getSleep.ends.split(".", limit=2)
               val replace2 = split2[0].replace("T", " ")
               starts = LocalDateTime.parse(replace1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(9)
               ends = LocalDateTime.parse(replace2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(9)
            }
         }

         val diff1 = Duration.between(starts, ends)
         val total = diff1.toMinutes().toInt()

         if(total > 0) {
            binding.tvSleepPt.text = if(getGoal.sleep > 0) "${(total * 100) / getGoal.sleep}%" else "100%"
            binding.pbSleep.max = if(getGoal.sleep > 0) getGoal.sleep else total
            binding.pbSleep.progress = total
         }

         if(getIntake.isNotEmpty()) {
            binding.tvDrugPt.text = if(getGoal.medicineIntake > 0) "${(getIntake.size * 100) / getGoal.medicineIntake}%" else "100%"
            binding.pbDrug.max = if(getGoal.medicineIntake > 0) getGoal.medicineIntake else getIntake.size
            binding.pbDrug.progress = getIntake.size
         }

         binding.tvFood.text = "$foodSum/${getGoal.kcalOfDiet}kcal"
         binding.tvWater.text = "${getWater.count}/${getGoal.waterIntake}잔"
         binding.tvExercise.text = "$exerciseSum/${getGoal.kcalOfWorkout}kcal"
         binding.tvBody.text = "${weight}/${weightGoal}kg"
         binding.tvSleep.text = "${total / 60}h${total % 60}m/$sleep"
         binding.tvDrug.text = "${getIntake.size}/${getGoal.medicineIntake}회"
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}