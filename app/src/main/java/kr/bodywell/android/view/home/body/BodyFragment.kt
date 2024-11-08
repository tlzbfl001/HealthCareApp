package kr.bodywell.android.view.home.body

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.BODY
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentBodyBinding
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.GoalInit
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate
import kotlin.math.roundToInt

class BodyFragment : Fragment() {
   private var _binding: FragmentBodyBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var dataManager: DataManager
   private var dailyGoal = GoalInit()
   private var getBody = Body()
   private var isExpand = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<EditText>(R.id.et)
      val tvUnit = dialog.findViewById<TextView>(R.id.tvUnit)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "신체 / 목표 체중 입력"
      et.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
      tvUnit.text = "kg"
      btnSave.setCardBackgroundColor(Color.parseColor("#B3AED77D"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(dailyGoal.createdAt == "") {
               dataManager.insertGoal(GoalInit(body = et.text.toString().toDouble(), createdAt = selectedDate.toString()))
               dailyGoal = dataManager.getGoal(selectedDate.toString())
            }else {
               dataManager.updateDouble(GOAL, BODY, et.text.toString().toDouble(), selectedDate.toString())
               dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
            }

            dailyGoal()
            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), BodyRecordFragment())
      }

      binding.clBmi.setOnClickListener {
         if (isExpand) {
            binding.clBmiExpend.visibility = View.GONE
            binding.ivExpand1.setImageResource(R.drawable.arrow_down)
         } else {
            binding.clBmiExpend.visibility = View.VISIBLE
            binding.ivExpand1.setImageResource(R.drawable.arrow_up)
         }
         isExpand = !isExpand
      }

      binding.clFat.setOnClickListener {
         if (isExpand) {
            binding.clFatExpend.visibility = View.GONE
            binding.ivExpand2.setImageResource(R.drawable.arrow_down)
         } else {
            binding.clFatExpend.visibility = View.VISIBLE
            binding.ivExpand2.setImageResource(R.drawable.arrow_up)
         }
         isExpand = !isExpand
      }

      binding.clMuscle.setOnClickListener {
         if (isExpand) {
            binding.clMuscleExpend.visibility = View.GONE
            binding.ivExpand3.setImageResource(R.drawable.arrow_down)
         }else {
            binding.clMuscleExpend.visibility = View.VISIBLE
            binding.ivExpand3.setImageResource(R.drawable.arrow_up)
         }
         isExpand = !isExpand
      }

      binding.clBmr.setOnClickListener {
         if (isExpand) {
            binding.tvBmrExpend.visibility = View.GONE
            binding.ivExpand4.setImageResource(R.drawable.arrow_down)
         } else {
            binding.tvBmrExpend.visibility = View.VISIBLE
            binding.ivExpand4.setImageResource(R.drawable.arrow_up)
         }
         isExpand = !isExpand
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyGoal()
         dailyList()
      })

      dailyGoal()
      dailyList()

      return binding.root
   }

   private fun dailyGoal() {
      binding.pbBody.setProgressStartColor(Color.TRANSPARENT)
      binding.pbBody.setProgressEndColor(Color.TRANSPARENT)
      binding.tvWeight.text = "0 kg"
      binding.tvGoal.text = "0 kg"
      binding.tvRemain.text = "0 kg"

      dailyGoal = dataManager.getGoal(selectedDate.toString())

      if (dailyGoal.body > 0) {
         binding.pbBody.max = dailyGoal.body.roundToInt()

         val split = dailyGoal.body.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvGoal.text = "${split[0]} kg"
            else -> binding.tvGoal.text = "${dailyGoal.body} kg"
         }
      }

      getBody = dataManager.getBody(selectedDate.toString())

      var remain = 0.0
      if(getBody.weight != null && getBody.weight!! > 0) {
         binding.pbBody.setProgressStartColor(resources.getColor(R.color.body))
         binding.pbBody.setProgressEndColor(resources.getColor(R.color.body))
         binding.pbBody.max = dailyGoal.body.roundToInt()
         binding.pbBody.progress = getBody.weight.toString().toDouble().roundToInt()

         val split = getBody.weight.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvWeight.text = "${split[0]} kg"
            else -> binding.tvWeight.text = "${String.format("%.1f", getBody.weight)} kg"
         }

         remain = dailyGoal.body - getBody.weight!!.toString().toDouble()
      }

      if(remain > 0) {
         val split = remain.toString().split(".")
         when (split[1]) {
            "0" -> binding.tvRemain.text = "${split[0]} kg"
            else -> binding.tvRemain.text = "$remain kg"
         }
         binding.tvWeight.text = "${getBody.weight} kg"
      }
   }

   private fun dailyList() {
      if(getBody.bmi != null) binding.tvBmi.text = getBody.bmi.toString() else binding.tvBmi.text = "0"
      if(getBody.fat != null) binding.tvFat.text = "${getBody.fat} %" else binding.tvFat.text = "0 %"
      if(getBody.muscle != null) binding.tvMuscle.text = "${getBody.muscle} kg" else binding.tvMuscle.text = "0 kg"
      if(getBody.bmr != null) binding.tvBmr.text = "${getBody.bmr} kcal" else binding.tvBmr.text = "0 kcal"

      // 체질량지수 범위
      var bmi = 0
      if(getBody.bmi != null) {
         val format1 = String.format("%.1f", getBody.bmi)
         bmi = format1.replace(".", "").toInt()
      }
      when {
         bmi < 186 -> {
            binding.bmiIndicator1.progress = bmi
            binding.bmiIndicator1.thumb.setTint(Color.BLACK)
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
            binding.bmiIndicator2.thumb.setTint(Color.BLACK)
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
            binding.bmiIndicator3.thumb.setTint(Color.BLACK)
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
            binding.bmiIndicator4.thumb.setTint(Color.BLACK)
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
            binding.bmiIndicator5.thumb.setTint(Color.BLACK)
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
            binding.bmiIndicator6.thumb.setTint(Color.BLACK)
            binding.tvBmiStatus.text = "심각한비만3"
         }
      }

      // 체지방율 범위
      var fat = 0
      if(getBody.bmi != null) {
         val format2 = String.format("%.1f", getBody.fat)
         fat = format2.replace(".", "").toInt()
      }
      when {
         fat < 141 -> {
            binding.fatIndicator1.progress = fat
            binding.fatIndicator1.thumb.setTint(Color.BLACK)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "너무낮은수준"
         }
         fat < 211 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.progress = fat
            binding.fatIndicator2.thumb.setTint(Color.BLACK)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "운동선수수준"
         }
         fat < 251 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.progress = fat
            binding.fatIndicator3.thumb.setTint(Color.BLACK)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "건강한수준"
         }
         fat < 321 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.progress = fat
            binding.fatIndicator4.thumb.setTint(Color.BLACK)
            binding.fatIndicator5.thumb.setTint(Color.TRANSPARENT)
            binding.tvFatStatus.text = "괜찮은수준"
         }
         fat < 401 -> {
            binding.fatIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator4.thumb.setTint(Color.TRANSPARENT)
            binding.fatIndicator5.progress = fat
            binding.fatIndicator5.thumb.setTint(Color.BLACK)
            binding.tvFatStatus.text = "높은수준"
         }
      }

      // 골격근량 범위
      var muscle = 0
      if(getBody.bmi != null) {
         val format3 = String.format("%.1f", getBody.muscle)
         muscle = format3.replace(".", "").toInt()
      }
      when {
         muscle < 267 -> {
            binding.muscleIndicator1.progress = muscle
            binding.muscleIndicator1.thumb.setTint(Color.BLACK)
            binding.muscleIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.tvMuscleStatus.text = "낮음"
         }
         muscle < 311 -> {
            binding.muscleIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator2.progress = muscle
            binding.muscleIndicator2.thumb.setTint(Color.BLACK)
            binding.muscleIndicator3.thumb.setTint(Color.TRANSPARENT)
            binding.tvMuscleStatus.text = "표준"
         }
         muscle < 401 -> {
            binding.muscleIndicator1.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator2.thumb.setTint(Color.TRANSPARENT)
            binding.muscleIndicator3.progress = muscle
            binding.muscleIndicator3.thumb.setTint(Color.BLACK)
            binding.tvMuscleStatus.text = "높음"
         }
      }
   }

   override fun onDestroyView() {
      super.onDestroyView()
      _binding = null
   }
}