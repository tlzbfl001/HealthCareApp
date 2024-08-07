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
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.isoFormatter
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.ViewModelUtil
import kr.bodywell.android.view.home.DetailFragment
import nl.joery.timerangepicker.TimeRangePicker
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

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
            replaceFragment3(requireActivity(), DetailFragment())
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
         replaceFragment3(requireActivity(), DetailFragment())
      }

      binding.time.setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {
         override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
            bedHour = binding.time.startTime.hour
            bedMinute = binding.time.startTime.minute
            binding.tvBedtime.text = "$bedHour : $bedMinute"

            if(sleepHour == 0 && sleepMinute == 0) {
               binding.tvBedtime.text = "0 : 0"
            }
         }

         override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
            binding.tvWakeupTime.text = "${binding.time.endTime.hour} : ${binding.time.endTime.minute}"

            if(sleepHour == 0 && sleepMinute == 0) {
               binding.tvWakeupTime.text = "0 : 0"
            }
         }

         override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
            sleepHour = duration.hour
            sleepMinute = duration.minute
            binding.tvTotal.text = "${sleepHour}h ${sleepMinute}m"
         }
      })

      binding.cvSave.setOnClickListener {
         val getSleep = dataManager.getSleep(selectedDate.toString())

         if(sleepHour == 0 && sleepMinute == 0) {
            Toast.makeText(requireActivity(), "시간이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
         }else {
            var date = selectedDate
            val bedTime = bedHour * 60 + bedMinute
            val wakeTime: Int
            val sleepTime = sleepHour * 60 + sleepMinute

            if(bedTime + sleepTime > 1440) {
               date = selectedDate.plusDays(1)
               wakeTime = bedTime + sleepTime - 1440
            }else wakeTime = bedTime + sleepTime

            val bed = selectedDate.year.toString() + String.format("%02d", selectedDate.monthValue) + String.format("%02d", selectedDate.dayOfMonth) +
               String.format("%02d", bedHour) + String.format("%02d", bedMinute)
            val wake = date.year.toString() + String.format("%02d", date.monthValue) + String.format("%02d", date.dayOfMonth) +
               String.format("%02d", wakeTime / 60) + String.format("%02d", wakeTime % 60)

            val bedToDateTime = LocalDateTime.parse(bed, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
            val wakeToDateTime = LocalDateTime.parse(wake, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
            val bedFormat = dateTimeToIso(bedToDateTime)
            val wakeFormat = dateTimeToIso(wakeToDateTime)

            if(getSleep.createdAt == "") {
               dataManager.insertSleep(Sleep(startTime = bedFormat, endTime = wakeFormat, createdAt = selectedDate.toString()))
               Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            }else {
               dataManager.updateSleep(Sleep(startTime = bedFormat, endTime = wakeFormat, createdAt = selectedDate.toString(), isUpdated = 1))
               Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            }

            replaceFragment3(requireActivity(), DetailFragment())
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}