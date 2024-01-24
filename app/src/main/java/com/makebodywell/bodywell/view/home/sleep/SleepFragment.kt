package com.makebodywell.bodywell.view.home.sleep

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSleepBinding
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import java.time.LocalDate
import java.util.Calendar

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   private var calendarDate: LocalDate? = null

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      calendarDate = LocalDate.now()
      binding.tvDate.text = dateFormat(calendarDate)

      settingGoal()

      dailyView()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.clNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyView()
      }

      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }

      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }

      binding.cvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.cvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.clRecord.setOnClickListener {
         bundle.putString("calendarDate", calendarDate.toString())
         replaceFragment2(requireActivity(), SleepRecordFragment(), bundle)
      }

      return binding.root
   }

   private fun dailyView() {
      val getSleep = dataManager!!.getSleep(calendarDate.toString())
      val getDaily = dataManager!!.getDailyData(calendarDate.toString())

      binding.tvSleep.text = "${getSleep.sleepHour}h ${getSleep.sleepMinute}m"
      binding.tvGoal.text = "${getDaily.sleepHourGoal}h ${getDaily.sleepMinuteGoal}m"
      binding.tvBedtime.text = "${getSleep.bedHour}h ${getSleep.bedMinute}min"
      binding.tvWakeTime.text = "${getSleep.wakeHour}h ${getSleep.wakeMinute}min"

      if(getDaily.sleepHourGoal > 0) {
         val cal1 = Calendar.getInstance()
         cal1.set(Calendar.HOUR_OF_DAY, getDaily.sleepHourGoal)
         cal1.set(Calendar.MINUTE, getDaily.sleepMinuteGoal)
         cal1.set(Calendar.SECOND, 0)
         cal1.set(Calendar.MILLISECOND, 0)
         val goal = cal1.timeInMillis

         val cal2 = Calendar.getInstance()
         cal2.set(Calendar.HOUR_OF_DAY, getSleep.sleepHour)
         cal2.set(Calendar.MINUTE, getSleep.sleepMinute)
         cal2.set(Calendar.SECOND, 0)
         cal2.set(Calendar.MILLISECOND, 0)
         val sleep = cal2.timeInMillis

         val result = (goal - sleep) / 1000
         if(result > 0) {
            binding.tvRemain.text = "${(result / (60 * 60))}h ${((result / 60) % 60)}m"
         }
      }
   }

   private fun settingGoal() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_sleep_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      btnSave.setOnClickListener {
         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }
   }
}