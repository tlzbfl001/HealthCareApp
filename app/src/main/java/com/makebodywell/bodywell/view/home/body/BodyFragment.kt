package com.makebodywell.bodywell.view.home.body

import android.app.Dialog
<<<<<<< HEAD
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log

=======
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
<<<<<<< HEAD
import android.text.InputType
import android.util.Log
=======
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentBodyBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
<<<<<<< HEAD
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
=======
<<<<<<< HEAD
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
=======
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment
import com.makebodywell.bodywell.view.home.sleep.SleepFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import java.time.LocalDate
import kotlin.math.roundToInt

class BodyFragment : Fragment() {
   private var _binding: FragmentBodyBinding? = null
   private val binding get() = _binding!!

   private var calendarDate = LocalDate.now()

   private var dataManager: DataManager? = null
   private var getDailyData = DailyData()
   private var getBody = Body()

   private var isExpend = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupGoal()
      dailyView()

      return binding.root
   }

   private fun initView() {
      binding.tvDate.text = dateFormat(calendarDate)

<<<<<<< HEAD
      getBody = dataManager!!.getBody(calendarDate.toString())

      // 목표 설정
=======
<<<<<<< HEAD
      // 목표 설정
=======
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val et = dialog.findViewById<EditText>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvUnit.text = "kg"
      btnSave.setCardBackgroundColor(Color.parseColor("#81C335"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(getDailyData.regDate == "") {
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
               dataManager?.insertDailyData(DailyData(bodyGoal = et.text.toString().toDouble(), regDate = calendarDate.toString()))
            }else {
               dataManager?.updateBodyGoal(DailyData(bodyGoal = et.text.toString().toDouble(), regDate = calendarDate.toString()))
            }

            binding.pbBody.max = et.text.toString().toDouble().roundToInt()
            binding.tvGoal.text = "${et.text} kg"

            val remain = et.text.toString().toDouble() - getBody.weight.toString().toDouble()
            if(remain > 0) {
               val split = remain.toString().split(".")
               when(split[1]) {
                  "0" -> binding.tvRemain.text = "${split[0]} kg"
                  else -> binding.tvRemain.text = "$remain kg"
               }
            }else {
               binding.tvRemain.text = "0 kg"
            }
<<<<<<< HEAD

            binding.pbBody.max = et.text.toString().toDouble().roundToInt()
            binding.tvGoal.text = "${et.text} kg"
            binding.tvRemain.text = "${et.text.toString().toDouble() - getBody.weight.toString().toDouble()} kg"
=======
=======
               dataManager?.insertDailyData(DailyData(bodyGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }else {
               dataManager?.updateBodyGoal(DailyData(bodyGoal = et.text.toString().toInt(), regDate = calendarDate.toString()))
            }

            binding.pbBody.max = et.text.toString().toInt()
            binding.tvGoal.text = "${et.text} kg"
            binding.tvRemain.text = "${et.text.toString().toInt() - getBody.weight.toString().toDouble().roundToInt()} kg"
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
         }

         dialog.dismiss()
      }

      binding.cvGoal.setOnClickListener {
         dialog.show()
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.ivPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
      }

      binding.ivNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         setupGoal()
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

      binding.cvSleep.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }

      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }

      binding.clRecord.setOnClickListener {
         if(getBody.regDate == "") {
            replaceFragment1(requireActivity(), BodyRecordFragment())
         }else {
<<<<<<< HEAD
            val bundle = Bundle()
=======
            var bundle = Bundle()
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
            val body = Body(id = getBody.id, height = getBody.height, weight = getBody.weight, age = getBody.age, gender = getBody.gender,
               exerciseLevel = getBody.exerciseLevel, fat = getBody.fat, muscle = getBody.muscle, bmi = getBody.bmi, bmr = getBody.bmr)
            bundle.putParcelable("body", body)
            replaceFragment2(requireActivity(), BodyRecordFragment(), bundle)
         }
      }

      binding.ivBmiButton.setOnClickListener {
         if (isExpend) {
            binding.clBmiExpend.visibility = View.GONE
         } else {
            binding.clBmiExpend.visibility = View.VISIBLE
         }
         isExpend = !isExpend
      }

      binding.ivFatButton.setOnClickListener {
         if (isExpend) {
            binding.clFatExpend.visibility = View.GONE
         } else {
            binding.clFatExpend.visibility = View.VISIBLE
         }
         isExpend = !isExpend
      }

      binding.ivMuscleButton.setOnClickListener {
         if (isExpend) {
            binding.clMuscleExpend.visibility = View.GONE
         } else {
            binding.clMuscleExpend.visibility = View.VISIBLE
         }
         isExpend = !isExpend
      }

      binding.ivBmrButton.setOnClickListener {
         if (isExpend) {
            binding.tvBmrExpend.visibility = View.GONE
         } else {
            binding.tvBmrExpend.visibility = View.VISIBLE
         }
         isExpend = !isExpend
      }
   }

   private fun setupGoal() {
<<<<<<< HEAD
      binding.tvWeight.text = "0 kg"
=======
<<<<<<< HEAD
      binding.tvWeight.text = "0 kg"
=======
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      binding.tvGoal.text = "0 kg"
      binding.tvRemain.text = "0 kg"

      getDailyData = dataManager!!.getDailyData(calendarDate.toString())
<<<<<<< HEAD

      val goal = getDailyData.bodyGoal
      if(goal > 0) {
          binding.pbBody.max = goal.roundToInt()

          val split = goal.toString().split(".")
          when (split[1]) {
              "0" -> binding.tvGoal.text = "${split[0]} kg"
              else -> binding.tvGoal.text = "$goal kg"
          }
      }

      if(getBody.weight > 0) {
         binding.pbBody.progress = getBody.weight.toString().toDouble().roundToInt()
=======
      val goal = getDailyData.bodyGoal
<<<<<<< HEAD
      if(goal > 0) {
         binding.pbBody.max = goal.roundToInt()

         val split = goal.toString().split(".")
         when(split[1]) {
            "0" -> binding.tvGoal.text = "${split[0]} kg"
            else -> binding.tvGoal.text = "$goal kg"
         }
=======
      if(goal != 0) {
         binding.pbBody.max = goal
         binding.tvGoal.text = "$goal kg"
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
      }

      getBody = dataManager!!.getBody(calendarDate.toString())
      if(getBody.weight > 0) {
         binding.pbBody.progress = getBody.weight.toString().toDouble().roundToInt()
<<<<<<< HEAD
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130

         val split = getBody.weight.toString().split(".")
         when(split[1]) {
            "0" -> binding.tvWeight.text = "${split[0]} kg"
            else -> binding.tvWeight.text = "${String.format("%.1f", getBody.weight)} kg"
         }
<<<<<<< HEAD
      }

      val remain = goal - getBody.weight.toString().toDouble()
      if(remain > 0) {
          val split = remain.toString().split(".")
          when (split[1]) {
              "0" -> binding.tvRemain.text = "${split[0]} kg"
              else -> binding.tvRemain.text = "$remain kg"
          }
=======

         val remain = goal - getBody.weight.toString().toDouble()
         if(remain > 0) {
            val split = remain.toString().split(".")
            when(split[1]) {
               "0" -> binding.tvRemain.text = "${split[0]} kg"
               else -> binding.tvRemain.text = "$remain kg"
            }
=======
         binding.tvWeight.text = "${getBody.weight} kg"
         val remain = getBody.weight.toString().toDouble() - goal
         if(remain > 0) {
            binding.tvRemain.text = "$remain kg"
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
         }
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
      }
   }

   private fun dailyView() {
      binding.tvBmi.text = getBody.bmi.toString()
      binding.tvFat.text = "${getBody.fat} %"
      binding.tvMuscle.text = "${getBody.muscle} kg"
      binding.tvBmr.text = "${getBody.bmr} kcal"

      // 체질량지수 범위
      val format1 = String.format("%.1f", getBody.bmi)
      val bmi = format1.replace(".", "").toInt()
      when{
         bmi < 186 -> {
            binding.bmiIndicator1.progress = bmi
            binding.bmiIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator6.thumb.setTint(Color.TRANSPARENT)
            binding.tvBmiStatus.text = "저체중"
         }
         bmi < 231 -> {
            binding.bmiIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator2.progress = bmi
            binding.bmiIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator6.thumb.setTint(Color.TRANSPARENT)
            binding.tvBmiStatus.text = "정상체중"
         }
         bmi < 251 -> {
            binding.bmiIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator3.progress = bmi
            binding.bmiIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator6.thumb.setTint(Color.TRANSPARENT)
            binding.tvBmiStatus.text = "과체중"
         }
         bmi < 301 -> {
            binding.bmiIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator4.progress = bmi
            binding.bmiIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator6.thumb.setTint(Color.TRANSPARENT)
            binding.tvBmiStatus.text = "비만1"
         }
         bmi < 401 -> {
            binding.bmiIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator5.progress = bmi
            binding.bmiIndicator6.thumb.setTint(Color.TRANSPARENT)
            binding.tvBmiStatus.text = "비만2"
         }
         bmi < 501 -> {
            binding.bmiIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.bmiIndicator6.progress = bmi
            binding.tvBmiStatus.text = "심각한비만3"
         }
      }

      // 체지방율 범위
      val format2 = String.format("%.1f", getBody.fat)
      val fat = format2.replace(".", "").toInt()
      when{
         fat < 101 -> {
            binding.fatIndicator1.progress = fat
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "너무낮은수준"
         }
         fat < 201 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.progress = fat
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "운동선수수준"
         }
         fat < 251 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.progress = fat
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "건강한수준"
         }
         fat < 321 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.progress = fat
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "괜찮은수준"
         }
         fat < 401 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.progress = fat
            binding.tvFatStatus.text = "높은수준"
         }
      }

      // 골격근량 범위
      val format3 = String.format("%.1f", getBody.muscle)
      val muscle = format3.replace(".", "").toInt() 
      when{
         muscle < 267 -> {
            binding.muscleIndicator1.progress = muscle
            binding.muscleIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.tvMuscleStatus.text = "낮음"
         }
         muscle < 311 -> {
            binding.muscleIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator2.progress = muscle
            binding.muscleIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.tvMuscleStatus.text = "표준"
         }
         muscle < 401 -> {
            binding.muscleIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator3.progress = muscle
            binding.tvMuscleStatus.text = "높음"
         }
      }
   }
}