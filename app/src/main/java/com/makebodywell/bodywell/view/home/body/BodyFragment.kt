package com.makebodywell.bodywell.view.home.body

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_BODY
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentBodyBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.model.DailyData
import com.makebodywell.bodywell.util.CalendarUtil.Companion.dateFormat
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

   private val bundle = Bundle()
   private var dataManager: DataManager? = null
   private var getDailyData = DailyData()
   private var getBody = Body()

   private var calendarDate = LocalDate.now()
   private var isExpand = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentBodyBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.tvDate.text = dateFormat(calendarDate)

      settingGoal()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }

      binding.clPrev.setOnClickListener {
         calendarDate = calendarDate!!.minusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyGoal()
         dailyList()
      }

      binding.clNext.setOnClickListener {
         calendarDate = calendarDate!!.plusDays(1)
         binding.tvDate.text = dateFormat(calendarDate)
         dailyGoal()
         dailyList()
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
            bundle.putString("calendarDate", calendarDate.toString())
            replaceFragment2(requireActivity(), BodyRecordFragment(), bundle)
         }
      }

      binding.clBmi.setOnClickListener {
         if (isExpand) {
            binding.clBmiExpend.visibility = View.GONE
         } else {
            binding.clBmiExpend.visibility = View.VISIBLE
         }
         isExpand = !isExpand
      }

      binding.clFat.setOnClickListener {
         if (isExpand) {
            binding.clFatExpend.visibility = View.GONE
         } else {
            binding.clFatExpend.visibility = View.VISIBLE
         }
         isExpand = !isExpand
      }

      binding.clMuscle.setOnClickListener {
         if (isExpand) {
            binding.clMuscleExpend.visibility = View.GONE
         } else {
            binding.clMuscleExpend.visibility = View.VISIBLE
         }
         isExpand = !isExpand
      }

      binding.clBmr.setOnClickListener {
         if (isExpand) {
            binding.tvBmrExpend.visibility = View.GONE
         } else {
            binding.tvBmrExpend.visibility = View.VISIBLE
         }
         isExpand = !isExpand
      }

      dailyGoal()
      dailyList()

      return binding.root
   }

   private fun settingGoal() {
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
               dataManager?.insertDailyData(DailyData(bodyGoal = et.text.toString().toDouble(), regDate = calendarDate.toString()))
            }else {
               dataManager?.updateBodyGoal(DailyData(bodyGoal = et.text.toString().toDouble(), regDate = calendarDate.toString()))
            }

            if(getDailyData.regDate == "") {
               dataManager!!.insertDailyData(DailyData(bodyGoal = et.text.toString().toDouble()))
            }else {
               dataManager!!.updateDouble(TABLE_BODY, "bodyGoal", et.text.toString().toDouble(), getDailyData.id)
            }

            dailyGoal()
         }

         dialog.dismiss()
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }
   }

   private fun dailyGoal() {
      binding.pbBody.max = 0
      binding.pbBody.setProgressStartColor(Color.TRANSPARENT)
      binding.pbBody.setProgressEndColor(Color.TRANSPARENT)
      binding.tvWeight.text = "0 kg"
      binding.tvGoal.text = "0 kg"
      binding.tvRemain.text = "0 kg"

      getDailyData = dataManager!!.getDailyData(calendarDate.toString())

      val goal = getDailyData.bodyGoal
      if (goal > 0) {
         binding.pbBody.max = goal.roundToInt()

         val split = goal.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvGoal.text = "${split[0]} kg"
            else -> binding.tvGoal.text = "$goal kg"
         }
      }

      getBody = dataManager!!.getBody(calendarDate.toString())
      if (getBody.weight > 0) {
         binding.pbBody.setProgressStartColor(Color.parseColor("#AED77D"))
         binding.pbBody.setProgressEndColor(Color.parseColor("#AED77D"))
         binding.pbBody.progress = getBody.weight.toString().toDouble().roundToInt()

         val split = getBody.weight.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvWeight.text = "${split[0]} kg"
            else -> binding.tvWeight.text = "${String.format("%.1f", getBody.weight)} kg"
         }
      }

      val remain = goal - getBody.weight.toString().toDouble()
      if (remain > 0) {
         val split = remain.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvRemain.text = "${split[0]} kg"
            else -> binding.tvRemain.text = "$remain kg"
         }
         binding.tvWeight.text = "${getBody.weight} kg"
      }
   }

   private fun dailyList() {
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
         fat < 141 -> {
            binding.fatIndicator1.progress = fat
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "너무낮은수준"
         }
         fat < 211 -> {
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