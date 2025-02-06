package kr.bodywell.health.view.home.sleep

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.health.databinding.FragmentSleepRecordBinding
import kr.bodywell.health.model.Sleep
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.home.DetailFragment
import nl.joery.timerangepicker.TimeRangePicker
import java.util.Calendar

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bedHour = 0
   private var bedMinute = 0
   private var sleepHour = 0
   private var sleepMinute = 0

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(parentFragmentManager, DetailFragment())
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

      binding.clBack.setOnClickListener {
         replaceFragment3(parentFragmentManager, DetailFragment())
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
            binding.tvWakeTime.text = "${binding.time.endTime.hour} : ${binding.time.endTime.minute}"

            if(sleepHour == 0 && sleepMinute == 0) {
               binding.tvWakeTime.text = "0 : 0"
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
            // 잠든 시간, 기상 시간 설정
            var date = selectedDate
            val bedTime = bedHour * 60 + bedMinute
            val wakeTime: Int
            val sleepTime = sleepHour * 60 + sleepMinute

            if(bedTime + sleepTime > 1440) {
               date = selectedDate.plusDays(1)
               wakeTime = bedTime + sleepTime - 1440
            }else wakeTime = bedTime + sleepTime

            // 시간대 추가
            val calendar1 = Calendar.getInstance()
            val calendar2 = Calendar.getInstance()
            val calendar3 = Calendar.getInstance()
            calendar1.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth, bedHour, bedMinute)
            calendar2.set(date.year, date.monthValue - 1, date.dayOfMonth, wakeTime / 60, wakeTime % 60)
            calendar3.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth, 10, 0, 0)
            val parse1 = dateTimeToIso(calendar1)
            val parse2 = dateTimeToIso(calendar2)
            val parse3 = dateTimeToIso(calendar3)

            lifecycleScope.launch {
               val getSleep = powerSync.getSleep(selectedDate.toString())

               if(getSleep.id == "") {
                  val uuid = UuidCreator.getTimeOrderedEpoch()
                  powerSync.insertSleep(Sleep(id = uuid.toString(), starts = parse1, ends = parse2, createdAt = parse3, updatedAt = parse3))

                  Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                  replaceFragment3(parentFragmentManager, DetailFragment())
               }else {
                  powerSync.updateSleep(Sleep(id = getSleep.id, starts = parse1, ends = parse2))

                  Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                  replaceFragment3(parentFragmentManager, DetailFragment())
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