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
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSleepBinding
import kr.bodywell.android.model.GoalInit
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

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

      dailyGoal = dataManager.getGoal(selectedDate.toString())
      getSleep = dataManager.getSleep(selectedDate.toString())

      if(getSleep.startTime != "" && getSleep.endTime != "") {
         val bedTime = LocalDateTime.parse(getSleep.startTime)
         val wakeTime = LocalDateTime.parse(getSleep.endTime)

         val diff = Duration.between(bedTime, wakeTime)
         total =diff.toMinutes().toInt()

         binding.pbSleep.setProgressStartColor(resources.getColor(R.color.sleep))
         binding.pbSleep.setProgressEndColor(resources.getColor(R.color.sleep))
         binding.pbSleep.max = dailyGoal.sleep
         binding.pbSleep.progress = total
         binding.tvBedtime.text = "${bedTime.hour}h ${bedTime.minute}m"
         binding.tvWakeTime.text = "${wakeTime.hour}h ${wakeTime.minute}m"

         val remain = (dailyGoal.sleep - total)
         if(remain > 0) {
            binding.tvRemain.text = "${remain / 60}h ${remain % 60}m"
         }
      }else {
         binding.tvBedtime.text = "0h 0m"
         binding.tvWakeTime.text = "0h 0m"
      }

      binding.tvGoal.text = "${dailyGoal.sleep / 60}h ${dailyGoal.sleep % 60}m"
      binding.tvSleep.text = "${total / 60}h ${total % 60}m"
   }
}