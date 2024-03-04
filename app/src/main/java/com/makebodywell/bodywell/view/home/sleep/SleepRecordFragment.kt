package com.makebodywell.bodywell.view.home.sleep

import android.annotation.SuppressLint
import android.graphics.Color
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
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
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

   @SuppressLint("InternalInsetResource", "DiscouragedApi")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepRecordBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.cl1.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.time.setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {
         @SuppressLint("SetTextI18n")
         override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
            bedHour = binding.time.startTime.hour
            bedMinute = binding.time.startTime.minute
            binding.tvBedtime.text = "$bedHour : $bedMinute"
         }

         @SuppressLint("SetTextI18n")
         override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
            wakeHour = binding.time.endTime.hour
            wakeMinute = binding.time.endTime.minute
            binding.tvWakeupTime.text = "$wakeHour : $wakeMinute"

         }

         @SuppressLint("SetTextI18n")
         override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
            sleepHour = duration.hour
            sleepMinute = duration.minute
            binding.tvTotal.text = "${duration.hour}h ${duration.minute}m"
         }
      })

      binding.cvSave.setOnClickListener {
         val getSleep = dataManager!!.getSleep(selectedDate.toString())

         val bedTime = bedHour * 60 + bedMinute
         val wakeTime = wakeHour * 60 + wakeMinute
         val sleepTime = sleepHour * 60 + sleepMinute

         if(getSleep.regDate == "") {
            dataManager!!.insertSleep(Sleep(bedTime = bedTime, wakeTime = wakeTime, sleepTime = sleepTime, regDate = selectedDate.toString()))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }else {
            dataManager!!.updateSleep(Sleep(bedTime = bedTime, wakeTime = wakeTime, sleepTime = sleepTime, regDate = selectedDate.toString()))

            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }
      }

      return binding.root
   }
}