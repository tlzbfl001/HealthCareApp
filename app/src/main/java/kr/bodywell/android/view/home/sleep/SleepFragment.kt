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
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentSleepBinding
import kr.bodywell.android.model.Constants.GOALS
import kr.bodywell.android.model.Constants.SLEEP
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.isoToDateTime
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private var getGoal = Goal()
   private var getSleep = Sleep()
   private var starts = LocalDateTime.now()
   private var ends = LocalDateTime.now()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepBinding.inflate(layoutInflater)

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

         lifecycleScope.launch {
            if(getGoal.id == "") {
               val uuid = UuidCreator.getTimeOrderedEpoch()
               powerSync.insertGoal(Goal(id = uuid.toString(), sleep = total, date = selectedDate.toString(),
                  createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString()))
               getGoal = powerSync.getGoal(selectedDate.toString())
            }else {
               powerSync.updateData(GOALS, SLEEP, total.toString(), getGoal.id)
            }
         }

         dailyView()

         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(parentFragmentManager, SleepRecordFragment())
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

      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         getSleep = powerSync.getSleep(selectedDate.toString())

         if(getSleep.starts != "" && getSleep.ends!= "") {
            if(getSleep.starts.contains("+09:00")) {
               starts = isoToDateTime(getSleep.starts)
               ends = isoToDateTime(getSleep.ends)
            }else {
               val split1 = getSleep.starts.split(".", limit=2)
               val replace1 = split1[0].replace("T", " ")
               val split2 = getSleep.ends.split(".", limit=2)
               val replace2 = split2[0].replace("T", " ")
               starts = LocalDateTime.parse(replace1, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(9)
               ends = LocalDateTime.parse(replace2, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusHours(9)
            }

            val diff1 = Duration.between(starts, ends)
            total = diff1.toMinutes().toInt()

            binding.pbSleep.setProgressStartColor(resources.getColor(R.color.sleep))
            binding.pbSleep.setProgressEndColor(resources.getColor(R.color.sleep))
            binding.pbSleep.max = getGoal.sleep
            binding.pbSleep.progress = total

            if(starts.toString() != "" && ends.toString() != "") {
               binding.tvBedtime.text = "${starts!!.hour}h ${starts!!.minute}m"
               binding.tvWakeTime.text = "${ends!!.hour}h ${ends!!.minute}m"
            }

            val remain = (getGoal.sleep - total)
            if(remain > 0) binding.tvRemain.text = "${remain / 60}h ${remain % 60}m"
         }else {
            binding.tvBedtime.text = "0h 0m"
            binding.tvWakeTime.text = "0h 0m"
         }

         binding.tvGoal.text = "${getGoal.sleep / 60}h ${getGoal.sleep % 60}m"
         binding.tvSleep.text = "${total / 60}h ${total % 60}m"
      }
   }
}