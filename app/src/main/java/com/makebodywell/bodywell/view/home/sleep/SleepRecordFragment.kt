package com.makebodywell.bodywell.view.home.sleep

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.health.connect.client.time.TimeRangeFilter
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSleepRecordBinding
import com.makebodywell.bodywell.model.Sleep
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.food.FoodFragment
import nl.joery.timerangepicker.TimeRangePicker
import java.util.Calendar

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var bedHour = 0
   private var bedMinute = 0
   private var wakeHour = 0
   private var wakeMinute = 0
   private var sleepHour = 0
   private var sleepMinute = 0

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

      binding.time.setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {
         override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
            bedHour = binding.time.startTime.hour
            bedMinute = binding.time.startTime.minute
            binding.tvBedtime.text = "$bedHour : $bedMinute"
         }

         override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
            wakeHour = binding.time.endTime.hour
            wakeMinute = binding.time.endTime.minute
            binding.tvWakeupTime.text = "$wakeHour : $wakeMinute"

         }

         override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
            sleepHour = duration.hour
            sleepMinute = duration.minute
            binding.tvTotal.text = "${duration.hour}h ${duration.minute}m"
         }
      })

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
}