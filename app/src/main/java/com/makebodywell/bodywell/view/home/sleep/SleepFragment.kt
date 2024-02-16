package com.makebodywell.bodywell.view.home.sleep

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSleepBinding
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Sleep
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import java.util.Calendar

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var getDaily = DailyData()
   private var getSleep = Sleep()

   private var calendarDate: LocalDate? = null

   @SuppressLint("DiscouragedApi", "InternalInsetResource")
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
      dataManager!!.open()

      calendarDate = LocalDate.now()
      binding.tvDate.text = dateFormat(calendarDate)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.clNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
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
         bundle.putString("calendarDate", calendarDate.toString())
         replaceFragment2(requireActivity(), SleepRecordFragment(), bundle)
      }

      settingGoal()
      dailyView()

      return binding.root
   }

   @SuppressLint("SetTextI18n")
   private fun settingGoal() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_sleep)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val etHour = dialog.findViewById<EditText>(R.id.etHour)
      val etMinute = dialog.findViewById<EditText>(R.id.etMinute)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         val hour = if(etHour.text.toString().trim() == "") {
            7
         } else {
            etHour.text.toString().toInt()
         }

         var minute = if(etMinute.text.toString().trim() == "") {
            0
         } else {
            etMinute.text.toString().toInt()
         }

         val total = hour * 60 + minute

         if(getDaily.regDate == "") {
            dataManager!!.insertDailyData(DailyData(sleepGoal = total, regDate = calendarDate.toString()))
         }else {
            dataManager!!.updateGoal("sleepGoal", total, calendarDate.toString())
         }

         binding.pbSleep.max = total
         binding.tvGoal.text = "${total / 60}h ${total % 60}m"

         val remain = total - getSleep.sleepTime

         if(remain > 0) {
            binding.tvRemain.text = "${remain / 60}h ${remain % 60}m"
         }else {
            binding.tvRemain.text = "0h 0m"
         }

         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }
   }

   @SuppressLint("SetTextI18n")
   private fun dailyView() {
      getSleep = dataManager!!.getSleep(calendarDate.toString())
      getDaily = dataManager!!.getDailyData(calendarDate.toString())

      binding.tvSleep.text = "${getSleep.sleepTime / 60}h ${getSleep.sleepTime % 60}m"
      binding.tvGoal.text = "${getDaily.sleepGoal / 60}h ${getDaily.sleepGoal % 60}m"
      binding.tvBedtime.text = "${getSleep.bedTime / 60}h ${getSleep.bedTime % 60}m"
      binding.tvWakeTime.text = "${getSleep.wakeTime / 60}h ${getSleep.wakeTime % 60}m"

      if(getDaily.sleepGoal > 0) {
         val result = (getDaily.sleepGoal - getSleep.sleepTime)
         if(result > 0) {
            binding.tvRemain.text = "${result / 60}h ${result % 60}m"
         }
      }
   }
}