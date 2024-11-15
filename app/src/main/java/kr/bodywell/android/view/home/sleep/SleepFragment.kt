package kr.bodywell.android.view.home.sleep

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSleepBinding
import kr.bodywell.android.model.GoalInit
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import java.util.TimeZone

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var dailyGoal = GoalInit()
   private var getSleep = Sleep()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_sleep)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

      val etHour = dialog.findViewById<EditText>(R.id.etHour)
      val etMinute = dialog.findViewById<EditText>(R.id.etMinute)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      btnSave.setOnClickListener {
         val hour = if(etHour.text.toString().trim() == "") 7 else etHour.text.toString().toInt()
         val minute = if(etMinute.text.toString().trim() == "") 0 else etMinute.text.toString().toInt()
         val total = hour * 60 + minute

         if(dailyGoal.createdAt == "") {
            dataManager.insertGoal(GoalInit(sleep = total, createdAt = selectedDate.toString()))
            dailyGoal = dataManager.getGoal(selectedDate.toString())
         }else {
            dataManager.updateInt(GOAL, SLEEP, total, selectedDate.toString())
            dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
         }

         dailyView()

         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), SleepRecordFragment())
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      var total = 0
      binding.pbSleep.setProgressStartColor(Color.TRANSPARENT)
      binding.pbSleep.setProgressEndColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0h 0m"
      binding.tvRemain.text = "0h 0m"

//      dailyGoal = dataManager.getGoal(selectedDate.toString())
//      getSleep = dataManager.getSleep(selectedDate.toString())

      lifecycleScope.launch {
         getSleep = powerSync.getSleep(selectedDate.toString())
      }

      if(getSleep.starts != "" && getSleep.ends!= "") {
//         val bedTime = LocalDateTime.parse(getSleep.starts)
//         val wakeTime = LocalDateTime.parse(getSleep.ends)
//
//         val diff = Duration.between(bedTime, wakeTime)
//         total =diff.toMinutes().toInt()

         // ISO8601문자열을 Date로 변환
         val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
         dateFormat.timeZone = TimeZone.getTimeZone("UTC")
         val date1: Date = dateFormat.parse("2024-12-14T23:20:17.419+09:00")
         val bedTime = dateFormat.parse(getSleep.starts)
         val wakeTime = dateFormat.parse(getSleep.ends)
         val diff = Duration.between(bedTime.toInstant(), wakeTime.toInstant())
         total =diff.toMinutes().toInt()

         binding.pbSleep.setProgressStartColor(resources.getColor(R.color.sleep))
         binding.pbSleep.setProgressEndColor(resources.getColor(R.color.sleep))
         binding.pbSleep.max = dailyGoal.sleep
         binding.pbSleep.progress = total
         binding.tvBedtime.text = "${bedTime.hours}h ${bedTime.minutes}m"
         binding.tvWakeTime.text = "${wakeTime.hours}h ${wakeTime.minutes}m"

         val remain = (dailyGoal.sleep - total)
         if(remain > 0) binding.tvRemain.text = "${remain / 60}h ${remain % 60}m"
      }else {
         binding.tvBedtime.text = "0h 0m"
         binding.tvWakeTime.text = "0h 0m"
      }

      binding.tvGoal.text = "${dailyGoal.sleep / 60}h ${dailyGoal.sleep % 60}m"
      binding.tvSleep.text = "${total / 60}h ${total % 60}m"
   }
}