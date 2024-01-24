package com.makebodywell.bodywell.view.home.sleep

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSleepRecordBinding
import com.makebodywell.bodywell.model.Sleep
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.food.FoodFragment
import java.util.Calendar

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var cal = Calendar.getInstance()
   private var check = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepRecordBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.clBedtime.setOnClickListener {
         openTimePicker(1)
      }

      binding.clWake.setOnClickListener {
         openTimePicker(2)
      }

      binding.cvSave.setOnClickListener {
         val calendarDate = arguments?.getString("calendarDate").toString()

         val getSleep = dataManager!!.getSleep(calendarDate)
         if(getSleep.regDate == "") {
            dataManager!!.insertSleep(Sleep(sleepHour = sleepHour, sleepMinute = sleepMinute, bedHour = bedHour, bedMinute = bedMinute,
               wakeHour = wakeHour, wakeMinute = wakeMinute, regDate = calendarDate))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }else {
            dataManager!!.updateSleep(Sleep(sleepHour = sleepHour, sleepMinute = sleepMinute, bedHour = bedHour, bedMinute = bedMinute,
               wakeHour = wakeHour, wakeMinute = wakeMinute, regDate = calendarDate))

            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }
      }

      return binding.root
   }

   private fun openTimePicker(type: Int) {
      val isSystem24Hour = is24HourFormat(requireActivity())
      val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

      val picker = MaterialTimePicker.Builder()
         .setTimeFormat(clockFormat)
         .setHour(12)
         .setMinute(0)
         .setTitleText("시간 선택")
         .build()

      picker.show(childFragmentManager, TAG)

      picker.addOnPositiveButtonClickListener {
         val h = picker.hour
         val min = picker.minute

         if(type == 1) {
            bedHour = h
            bedMinute = min
            binding.tvBedtime.text = "$h : $min"

            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, min)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            bedMillis = cal.timeInMillis
         }else {
            wakeHour = h
            wakeMinute = min
            binding.tvWakeupTime.text = "$h : $min"

            cal.add(Calendar.DATE, 1)
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, min)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            wakeupMillis = cal.timeInMillis

            check = true
         }

         if(bedMillis != 0L && wakeupMillis != 0L) {
            val result = (wakeupMillis - bedMillis) / 1000
            sleepHour = (result / (60 * 60)).toInt()
            sleepMinute = ((result / 60) % 60).toInt()
            binding.tvTotal.text = "${sleepHour}h ${sleepMinute}m"
         }

         if(check) {
            cal.add(Calendar.DATE, -1)
            check = false
         }
      }

      picker.addOnNegativeButtonClickListener {

      }
   }

   companion object {
      private var bedHour = 0
      private var bedMinute = 0
      private var wakeHour = 0
      private var wakeMinute = 0
      private var bedMillis = 0L
      private var wakeupMillis = 0L
      private var sleepHour = 0
      private var sleepMinute = 0
   }
}