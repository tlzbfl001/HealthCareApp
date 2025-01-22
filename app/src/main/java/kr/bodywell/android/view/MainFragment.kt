package kr.bodywell.android.view

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import kr.bodywell.android.adapter.CalendarAdapter1
import kr.bodywell.android.databinding.FragmentMainBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CalendarUtil.weekArray
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.getExerciseCalories
import kr.bodywell.android.util.CustomUtil.getFoodCalories
import kr.bodywell.android.util.CustomUtil.isoToDateTime
import kr.bodywell.android.util.CustomUtil.layoutType
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.util.PermissionUtil
import kr.bodywell.android.view.home.CalendarDialog
import kr.bodywell.android.view.home.DetailFragment
import kr.bodywell.android.view.home.food.GalleryFragment
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
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
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

      setStatusBar(requireActivity(), binding.mainLayout)

      selectedDate = LocalDate.now()
      viewModel.setDate()

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){}

      // 프로필 설정
      lifecycleScope.launch {
         val getProfile = powerSync.getProfile()
         val getFile = powerSync.getFile(getProfile.id)

         if(getFile.name != "") {
            val imgPath = requireActivity().filesDir.toString() + "/" + getFile.name
            val file = File(imgPath)
            if(file.exists()){
               val bm = BitmapFactory.decodeFile(imgPath)
               binding.ivUser.setImageBitmap(bm)
            }
         }

         if(getProfile.name != "") binding.tvName.text = getProfile.name + " 님"
      }

      binding.clFood.setOnClickListener {
         if(PermissionUtil.checkMediaPermission(requireActivity())) {
            layoutType = 1
            replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
         }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pLauncher.launch(PermissionUtil.MEDIA_PERMISSION_2)
            }else {
               pLauncher.launch(PermissionUtil.MEDIA_PERMISSION_1)
            }
         }
      }

      binding.clWater.setOnClickListener {
         layoutType = 2
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.clExercise.setOnClickListener {
         layoutType = 3
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.clBody.setOnClickListener {
         layoutType = 4
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.clSleep.setOnClickListener {
         layoutType = 5
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.clMedicine.setOnClickListener {
         layoutType = 6
         replaceFragment1(requireActivity().supportFragmentManager, DetailFragment())
      }

      binding.btnCalendar.setOnClickListener {
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
      // 데이터 초기화
      days = weekArray(selectedDate)
      adapter = CalendarAdapter1(days)
      val layoutManager = GridLayoutManager(activity, 7)
      binding.recyclerView.layoutManager = layoutManager
      binding.recyclerView.adapter = adapter
      binding.tvYear.text = selectedDate.format(DateTimeFormatter.ofPattern("yyyy"))
      binding.tvMonth.text = selectedDate.format(DateTimeFormatter.ofPattern("M"))
      binding.btnFood.text = "0% 완료"
      binding.btnWater.text = "0% 완료"
      binding.btnExercise.text = "0% 완료"
      binding.btnBody.text = "0% 완료"
      binding.btnSleep.text = "0% 완료"
      binding.btnMedicine.text = "0% 완료"

      // 목표 달성 데이터 설정
      lifecycleScope.launch {
         val getGoal = powerSync.getGoal(selectedDate.toString())
         val foodSum = getFoodCalories(selectedDate.toString()).int5
         val getWater = powerSync.getWater(selectedDate.toString())
         val exerciseSum = getExerciseCalories(selectedDate.toString())
         val getBody = powerSync.getBody(selectedDate.toString())
         val getSleep = powerSync.getSleep(selectedDate.toString())
         val getIntake = powerSync.getRecentlyIntakes(selectedDate.toString())

         if(foodSum > 0) {
            binding.btnFood.text = if(getGoal.kcalOfDiet > 0) "${((foodSum * 100) / getGoal.kcalOfDiet)}% 완료" else "100% 완료"
         }

         if(getWater.count > 0) {
            binding.btnWater.text = if(getGoal.waterIntake > 0) "${(getWater.count * 100) / getGoal.waterIntake}% 완료" else "100% 완료"
         }

         if(exerciseSum > 0) {
            binding.btnExercise.text = if(getGoal.kcalOfWorkout > 0) "${(exerciseSum * 100) / getGoal.kcalOfWorkout}% 완료" else "100% 완료"
         }

         val weightGoalSplit = getGoal.weight.toString().split(".")
         val weightGoal = if(weightGoalSplit[1] == "0") weightGoalSplit[0] else getGoal.weight

         var weight = "0"
         if(getBody.weight != null) {
            val weightSplit = getBody.weight.toString().split(".")
            weight = if(weightSplit[1] == "0") weightSplit[0] else getBody.weight.toString()
         }

         if(getBody.weight != null && getBody.weight!! > 0) {
            binding.btnBody.text =if(getGoal.weight > 0)  "${(getBody.weight!!.toInt() * 100) / getGoal.weight.roundToInt()}% 완료" else "100% 완료"
         }

         val sleep = if(getGoal.sleep % 60 == 0) "${getGoal.sleep / 60}h" else "${getGoal.sleep / 60}h${getGoal.sleep % 60}m"

         if(getSleep.starts != "" && getSleep.ends!= "") {
            if(getSleep.starts.contains("T")) {
               starts = isoToDateTime(getSleep.starts)
               ends = isoToDateTime(getSleep.ends)
            }else {
               val replace1 = getSleep.starts.replace(" ", "T")
               val replace2 = getSleep.ends.replace(" ", "T")
               starts = isoToDateTime(replace1)
               ends = isoToDateTime(replace2)
            }
         }

         val diff1 = Duration.between(starts, ends)
         val total = diff1.toMinutes().toInt()

         if(total > 0) {
            binding.btnSleep.text = if(getGoal.sleep > 0) "${(total * 100) / getGoal.sleep}% 완료" else "100% 완료"
         }

         if(getIntake.isNotEmpty()) {
            binding.btnMedicine.text = if(getGoal.medicineIntake > 0) "${(getIntake.size * 100) / getGoal.medicineIntake}% 완료" else "100% 완료"
         }

         binding.tvFoodGoal.text = "$foodSum/${getGoal.kcalOfDiet}kcal"
         binding.tvWaterGoal.text = "${getWater.count}/${getGoal.waterIntake}잔"
         binding.tvExerciseGoal.text = "$exerciseSum/${getGoal.kcalOfWorkout}kcal"
         binding.tvBodyGoal.text = "${weight}/${weightGoal}kg"
         binding.tvSleepGoal.text = "${total / 60}h${total % 60}m/$sleep"
         binding.tvMedicineGoal.text = "${getIntake.size}/${getGoal.medicineIntake}회"
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}