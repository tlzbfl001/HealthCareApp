package kr.bodywell.test.view.home.sleep

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
import kr.bodywell.test.R
import kr.bodywell.test.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentSleepBinding
import kr.bodywell.test.model.Goal
import kr.bodywell.test.model.Sleep
import kr.bodywell.test.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.test.util.CustomUtil.Companion.isoFormatter
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.test.util.MainViewModel
import java.time.LocalDate
import java.time.LocalDateTime

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var dailyGoal = Goal()
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

         if(dailyGoal.created == "") {
            dataManager.insertGoal(Goal(sleep = total, created = selectedDate.toString()))
            dailyGoal = dataManager.getGoal(selectedDate.toString())
         }else {
            dataManager.updateInt(TABLE_GOAL, "sleep", total, selectedDate.toString())
            dataManager.updateInt(TABLE_GOAL, "isUpdated", 1, "id", dailyGoal.id)
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

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> { item ->
         dailyView()
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbSleep.setProgressStartColor(Color.TRANSPARENT)
      binding.pbSleep.setProgressEndColor(Color.TRANSPARENT)
      binding.tvGoal.text = "0h 0m"
      binding.tvRemain.text = "0h 0m"

      dailyGoal = dataManager.getGoal(selectedDate.toString())
      getSleep = dataManager.getSleep(selectedDate.toString())

      if(getSleep.startTime != "" && getSleep.endTime != "") {
         val bedTime = LocalDateTime.parse(getSleep.startTime, isoFormatter)
         val wakeTime = LocalDateTime.parse(getSleep.endTime, isoFormatter)

         binding.pbSleep.setProgressStartColor(Color.parseColor("#667D99"))
         binding.pbSleep.setProgressEndColor(Color.parseColor("#667D99"))
         binding.pbSleep.max = dailyGoal.sleep
         binding.pbSleep.progress = getSleep.total
         binding.tvBedtime.text = "${bedTime.hour}h ${bedTime.minute}m"
         binding.tvWakeTime.text = "${wakeTime.hour}h ${wakeTime.minute}m"

         val remain = (dailyGoal.sleep - getSleep.total)
         if(remain > 0) {
            binding.tvRemain.text = "${remain / 60}h ${remain % 60}m"
         }
      }else {
         binding.tvBedtime.text = "0h 0m"
         binding.tvWakeTime.text = "0h 0m"
      }

      binding.tvGoal.text = "${dailyGoal.sleep / 60}h ${dailyGoal.sleep % 60}m"
      binding.tvSleep.text = "${getSleep.total / 60}h ${getSleep.total % 60}m"
   }
}