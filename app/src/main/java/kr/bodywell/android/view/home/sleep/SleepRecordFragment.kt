package kr.bodywell.android.view.home.sleep

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSleepRecordBinding
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import nl.joery.timerangepicker.TimeRangePicker
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bedHour = 0
   private var bedMinute = 0
   private var sleepHour = 0
   private var sleepMinute = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), SleepFragment())
         }
      }

      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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
      dataManager.open()

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
            binding.tvWakeupTime.text = "${binding.time.endTime.hour} : ${binding.time.endTime.minute}"

         }

         override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
            sleepHour = duration.hour
            sleepMinute = duration.minute
            binding.tvTotal.text = "${sleepHour}h ${sleepMinute}m"
         }
      })

      binding.cvSave.setOnClickListener {
         val getSleep = dataManager.getSleep(selectedDate.toString())
         var date = selectedDate
         val bedTime = bedHour * 60 + bedMinute
         val wakeTime: Int
         val sleepTime = sleepHour * 60 + sleepMinute

         if(bedTime + sleepTime > 1440) {
            date = selectedDate.plusDays(1)
            wakeTime = bedTime + sleepTime - 1440
         }else wakeTime = bedTime + sleepTime

         val bed = selectedDate.year.toString().substring(2,4) + String.format("%02d", selectedDate.monthValue) + String.format("%02d", selectedDate.dayOfMonth) +
            String.format("%02d", bedHour) + String.format("%02d", bedMinute)
         val wake = date.year.toString().substring(2,4) + String.format("%02d", date.monthValue) + String.format("%02d", date.dayOfMonth) +
            String.format("%02d", wakeTime / 60) + String.format("%02d", wakeTime % 60)

         val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'")
         val bedFormat = LocalDateTime.parse(bed, DateTimeFormatter.ofPattern("yyMMddHHmm")).format(formatter)
         val wakeFormat = LocalDateTime.parse(wake, DateTimeFormatter.ofPattern("yyMMddHHmm")).format(formatter)

         val startDT = LocalDateTime.of(selectedDate.year, selectedDate.monthValue, selectedDate.dayOfMonth, bedHour, bedMinute)
         val endDT = LocalDateTime.of(date.year, date.monthValue, date.dayOfMonth, wakeTime / 60, wakeTime % 60)
         val diff = Duration.between(startDT, endDT)

         if(getSleep.regDate == "") {
            dataManager.insertSleep(Sleep(uid = "", startTime = bedFormat, endTime = wakeFormat, total = diff.toMinutes().toInt(), regDate = selectedDate.toString()))
            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }else {
            dataManager.updateSleep(Sleep(startTime = bedFormat, endTime = wakeFormat, total = diff.toMinutes().toInt(), regDate = selectedDate.toString()))
            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), SleepFragment())
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}