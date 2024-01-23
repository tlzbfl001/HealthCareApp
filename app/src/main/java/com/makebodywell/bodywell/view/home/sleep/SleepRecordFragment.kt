package com.makebodywell.bodywell.view.home.sleep

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

import com.makebodywell.bodywell.databinding.FragmentSleepRecordBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepRecordBinding.inflate(layoutInflater)

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.clBedtime.setOnClickListener {
         binding.tvBedtime.text = openTimePicker()
      }

      binding.clWake.setOnClickListener {
         binding.tvWake.text = openTimePicker()
      }

      return binding.root
   }

   private fun openTimePicker(): String {
      var time = ""
      val isSystem24Hour = is24HourFormat(requireActivity())
      val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

      val picker = MaterialTimePicker.Builder()
         .setTimeFormat(clockFormat)
         .setHour(12)
         .setMinute(0)
         .setTitleText("Set Alarm")
         .build()

      picker.show(childFragmentManager, TAG)

      picker.addOnPositiveButtonClickListener {
         val h = picker.hour
         val min = picker.minute
         time = "$h : $min"
      }

      picker.addOnNegativeButtonClickListener {

      }

      return time
   }
}