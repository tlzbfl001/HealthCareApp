package kr.bodywell.android.view.home.sleep

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSleepRecordBinding
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso2
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.view.home.DetailFragment
import nl.joery.timerangepicker.TimeRangePicker
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import java.util.UUID

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

      setStatusBar(requireActivity(), binding.constraint)

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

            /*val bed = selectedDate.year.toString() + String.format("%02d", selectedDate.monthValue) + String.format("%02d", selectedDate.dayOfMonth) +
               String.format("%02d", bedHour) + String.format("%02d", bedMinute)
            val wake = date.year.toString() + String.format("%02d", date.monthValue) + String.format("%02d", date.dayOfMonth) +
               String.format("%02d", wakeTime / 60) + String.format("%02d", wakeTime % 60)

            val bedTimeParse = LocalDateTime.parse(bed, DateTimeFormatter.ofPattern("yyyyMMddHHmm")).toString()
            val wakeTimeParse = LocalDateTime.parse(wake, DateTimeFormatter.ofPattern("yyyyMMddHHmm")).toString()*/

//            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
//            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")

            val bed = Calendar.getInstance()
            val wake = Calendar.getInstance()
            val getInstance = Calendar.getInstance()
            bed.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth, bedHour, bedMinute)
            wake.set(date.year, date.monthValue - 1, date.dayOfMonth, wakeTime / 60, wakeTime % 60)

//            val date1: Date = bed.time
//            val date2: Date = wake.time

            val startTime = dateTimeToIso2(bed)
            val endTime = dateTimeToIso2(wake)
            val createdAt = dateTimeToIso2(getInstance)

            lifecycleScope.launch {
               val getSleep = powerSync.getSleep(selectedDate.toString())

               if(startTime == getSleep.starts && endTime == getSleep.ends) {
                  Toast.makeText(requireActivity(), "수면시간이 동일합니다.", Toast.LENGTH_SHORT).show()
               }else {
                  if(getSleep.id == "") {
                     val uuid: UUID = UuidCreator.getTimeOrderedEpoch()
                     powerSync.insertSleep(Sleep(id = uuid.toString(), starts = startTime, ends = endTime, createdAt = createdAt, updatedAt = createdAt))

                     Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                     replaceFragment3(requireActivity(), DetailFragment())
                  }else {
                     powerSync.updateSleep(Sleep(id = getSleep.id, starts = startTime, ends = endTime))

                     Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                     replaceFragment3(requireActivity(), DetailFragment())
                  }
               }
            }
         }
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}