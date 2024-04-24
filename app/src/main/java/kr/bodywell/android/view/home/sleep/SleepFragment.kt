package kr.bodywell.android.view.home.sleep

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSleepBinding
import kr.bodywell.android.model.DailyGoal
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.util.CalendarUtil.Companion.dateFormat
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.view.home.MainFragment
import kr.bodywell.android.view.home.body.BodyFragment
import kr.bodywell.android.view.home.drug.DrugFragment
import kr.bodywell.android.view.home.exercise.ExerciseFragment
import kr.bodywell.android.view.home.food.FoodFragment
import kr.bodywell.android.view.home.water.WaterFragment

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var getDailyGoal = DailyGoal()
   private var getSleep = Sleep()

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
      _binding = FragmentSleepBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      binding.tvDate.text = dateFormat(selectedDate)

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_sleep)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val etHour = dialog.findViewById<EditText>(R.id.etHour)
      val etMinute = dialog.findViewById<EditText>(R.id.etMinute)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         val hour = if(etHour.text.toString().trim() == "") 7 else { etHour.text.toString().toInt() }
         val minute = if(etMinute.text.toString().trim() == "") 0 else { etMinute.text.toString().toInt() }
         val total = hour * 60 + minute

         if(getDailyGoal.regDate == "") {
            dataManager.insertDailyGoal(DailyGoal(sleepGoal = total, regDate = selectedDate.toString()))
         }else {
            dataManager.updateIntByDate(TABLE_DAILY_GOAL, "sleepGoal", total, selectedDate.toString())
         }

         dailyView()

         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         selectedDate = selectedDate.minusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         dailyView()
      }

      binding.clNext.setOnClickListener {
         selectedDate = selectedDate.plusDays(1)
         binding.tvDate.text = dateFormat(selectedDate)
         dailyView()
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }

      binding.cvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.cvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), SleepRecordFragment())
      }

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbSleep.setProgressStartColor(Color.TRANSPARENT)
      binding.pbSleep.setProgressEndColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0h 0m"
      binding.tvRemain.text = "0h 0m"

      getDailyGoal = dataManager.getDailyGoal(selectedDate.toString())
      getSleep = dataManager.getSleep(selectedDate.toString())

      if(getSleep.sleepTime > 0) {
         binding.pbSleep.setProgressStartColor(Color.parseColor("#667D99"))
         binding.pbSleep.setProgressEndColor(Color.parseColor("#667D99"))
         binding.pbSleep.max = getDailyGoal.sleepGoal
         binding.pbSleep.progress = getSleep.sleepTime

         val result = (getDailyGoal.sleepGoal - getSleep.sleepTime)
         if(result > 0) {
            binding.tvRemain.text = "${result / 60}h ${result % 60}m"
         }
      }

      binding.tvSleep.text = "${getSleep.sleepTime / 60}h ${getSleep.sleepTime % 60}m"
      binding.tvGoal.text = "${getDailyGoal.sleepGoal / 60}h ${getDailyGoal.sleepGoal % 60}m"
      binding.tvBedtime.text = "${getSleep.bedTime / 60}h ${getSleep.bedTime % 60}m"
      binding.tvWakeTime.text = "${getSleep.wakeTime / 60}h ${getSleep.wakeTime % 60}m"
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}