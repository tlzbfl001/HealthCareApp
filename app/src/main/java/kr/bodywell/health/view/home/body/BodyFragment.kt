package kr.bodywell.health.view.home.body

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
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentBodyBinding
import kr.bodywell.health.model.Body
import kr.bodywell.health.model.Constant.BODY_MEASUREMENTS
import kr.bodywell.health.model.Constant.GOALS
import kr.bodywell.health.model.Goal
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.replaceFragment1
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.MainViewModel
import java.time.LocalDate
import java.util.Calendar
import kotlin.math.roundToInt

class BodyFragment : Fragment() {
   private var _binding: FragmentBodyBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private var getGoal = Goal()
   private var getBody = Body()
   private var isExpand = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentBodyBinding.inflate(layoutInflater)

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

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               if(getGoal.id == "") {
                  val uuid = UuidCreator.getTimeOrderedEpoch()
                  powerSync.insertGoal(Goal(id = uuid.toString(), weight = et.text.toString().toDouble(), date = selectedDate.toString(),
                     createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance())))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateData(GOALS, "weight", et.text.toString(), getGoal.id)
               }
            }

            dailyGoal()
            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(parentFragmentManager, BodyRecordFragment())
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

      viewModel.dateState.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyGoal()
         dailyList()
      })

      dailyGoal()
      dailyList()

      return binding.root
   }

   private fun dailyGoal() {
      binding.tvGoal.text = "0kg"
      binding.tvWeight.text = "0kg"
      binding.tvRemain.text = "0kg"

      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         getBody = powerSync.getBody(selectedDate.toString())
         powerSync.deleteDuplicate(BODY_MEASUREMENTS, "strftime('%Y-%m-%d', time)", selectedDate.toString(), getBody.id!!)

         val split = getGoal.weight.toString().split(".")
         when(split[1]) {
            "0" -> binding.tvGoal.text = "${split[0]}kg"
            else -> binding.tvGoal.text = "${getGoal.weight}kg"
         }

         if(getBody.weight != null) {
            if(getGoal.weight > 0) binding.progressbar.max = getGoal.weight.roundToInt() else binding.progressbar.max = getBody.weight!!.roundToInt()
            binding.progressbar.progress = getBody.weight!!.roundToInt()

            val split1 = getBody.weight.toString().split(".")
            when (split1[1]) {
               "0" -> binding.tvWeight.text = "${split[0]}kg"
               else -> binding.tvWeight.text = "${String.format("%.1f", getBody.weight)}kg"
            }

            val remain = (getGoal.weight - getBody.weight!!)
            val split2 = remain.toString().split(".")
            when(split2[1]) {
               "0" -> binding.tvRemain.text = "${split2[0]}kg"
               else -> binding.tvRemain.text = "${String.format("%.1f", remain)}kg"
            }
         }else binding.progressbar.progress = 0
      }
   }

   private fun dailyList() {
      if(getBody.bodyMassIndex != null) binding.tvBmi.text = getBody.bodyMassIndex.toString() else binding.tvBmi.text = "0"
      if(getBody.bodyFatPercentage != null) binding.tvFat.text = "${getBody.bodyFatPercentage} %" else binding.tvFat.text = "0 %"
      if(getBody.skeletalMuscleMass != null) binding.tvMuscle.text = "${getBody.skeletalMuscleMass} kg" else binding.tvMuscle.text = "0 kg"
      if(getBody.basalMetabolicRate != null) binding.tvBmr.text = "${getBody.basalMetabolicRate} kcal" else binding.tvBmr.text = "0 kcal"

      // 체질량지수 범위
      var bmi = 0
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.bodyMassIndex)
         bmi = format.replace(".", "").toInt()
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
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.bodyFatPercentage)
         fat = format.replace(".", "").toInt()
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
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.skeletalMuscleMass)
         muscle = format.replace(".", "").toInt()
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
}