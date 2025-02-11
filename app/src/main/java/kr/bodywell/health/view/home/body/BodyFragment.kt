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
         replaceFragment1(requireActivity().supportFragmentManager, BodyRecordFragment())
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
            if(remain > 0) {
               val split2 = remain.toString().split(".")
               when(split2[1]) {
                  "0" -> binding.tvRemain.text = "${split2[0]}kg"
                  else -> binding.tvRemain.text = "${String.format("%.1f", remain)}kg"
               }
            }
         }else binding.progressbar.progress = 0
      }
   }

   private fun dailyList() {
      if(getBody.bodyMassIndex != null) binding.tvBmi.text = getBody.bodyMassIndex.toString() else binding.tvBmi.text = "0"
      if(getBody.bodyFatPercentage != null) binding.tvFat.text = "${getBody.bodyFatPercentage} %" else binding.tvFat.text = "0 %"
      if(getBody.skeletalMuscleMass != null) binding.tvMuscle.text = "${getBody.skeletalMuscleMass} kg" else binding.tvMuscle.text = "0 kg"
      if(getBody.basalMetabolicRate != null) binding.tvBmr.text = "${getBody.basalMetabolicRate} kcal" else binding.tvBmr.text = "0 kcal"

      binding.bmiIndicator1.thumb.setTint(resources.getColor(R.color.transparent))
      binding.bmiIndicator2.thumb.setTint(resources.getColor(R.color.transparent))
      binding.bmiIndicator3.thumb.setTint(resources.getColor(R.color.transparent))
      binding.bmiIndicator4.thumb.setTint(resources.getColor(R.color.transparent))
      binding.bmiIndicator5.thumb.setTint(resources.getColor(R.color.transparent))
      binding.bmiIndicator6.thumb.setTint(resources.getColor(R.color.transparent))

      binding.fatIndicator1.thumb.setTint(resources.getColor(R.color.transparent))
      binding.fatIndicator2.thumb.setTint(resources.getColor(R.color.transparent))
      binding.fatIndicator3.thumb.setTint(resources.getColor(R.color.transparent))
      binding.fatIndicator4.thumb.setTint(resources.getColor(R.color.transparent))
      binding.fatIndicator5.thumb.setTint(resources.getColor(R.color.transparent))

      binding.muscleIndicator1.thumb.setTint(resources.getColor(R.color.transparent))
      binding.muscleIndicator2.thumb.setTint(resources.getColor(R.color.transparent))
      binding.muscleIndicator3.thumb.setTint(resources.getColor(R.color.transparent))

      // 체질량지수 범위
      var bmi = 1
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.bodyMassIndex)
         bmi = format.replace(".", "").toInt()
      }

      if(bmi in 1..186) {
         binding.bmiIndicator1.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator1.progress = bmi
         binding.tvBmiStatus.text = "저체중"
      }else if(bmi in 186..231) {
         binding.bmiIndicator2.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator2.progress = bmi
         binding.tvBmiStatus.text = "정상체중"
      }else if(bmi in 231..251) {
         binding.bmiIndicator3.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator3.progress = bmi
         binding.tvBmiStatus.text = "과체중"
      }else if(bmi in 251..301) {
         binding.bmiIndicator4.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator4.progress = bmi
         binding.tvBmiStatus.text = "비만1"
      }else if(bmi in 301..401) {
         binding.bmiIndicator5.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator5.progress = bmi
         binding.tvBmiStatus.text = "비만2"
      }else {
         binding.bmiIndicator6.thumb.setTint(resources.getColor(R.color.black_white))
         binding.bmiIndicator6.progress = bmi
         binding.tvBmiStatus.text = "심각한비만3"
      }

      // 체지방율 범위
      var fat = 1
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.bodyFatPercentage)
         fat = format.replace(".", "").toInt()
      }

      if(fat in 1..141) {
         binding.fatIndicator1.thumb.setTint(resources.getColor(R.color.black_white))
         binding.fatIndicator1.progress = fat
         binding.tvFatStatus.text = "너무낮은수준"
      }else if(fat in 141..211) {
         binding.fatIndicator2.thumb.setTint(resources.getColor(R.color.black_white))
         binding.fatIndicator2.progress = fat
         binding.tvFatStatus.text = "운동선수수준"
      }else if(fat in 211..251) {
         binding.fatIndicator3.thumb.setTint(resources.getColor(R.color.black_white))
         binding.fatIndicator3.progress = fat
         binding.tvFatStatus.text = "건강한수준"
      }else if(fat in 251..321) {
         binding.fatIndicator4.thumb.setTint(resources.getColor(R.color.black_white))
         binding.fatIndicator4.progress = fat
         binding.tvFatStatus.text = "괜찮은수준"
      }else {
         binding.fatIndicator5.thumb.setTint(resources.getColor(R.color.black_white))
         binding.fatIndicator5.progress = fat
         binding.tvFatStatus.text = "높은수준"
      }

      // 골격근량 범위
      var muscle = 1
      if(getBody.bodyMassIndex != null) {
         val format = String.format("%.1f", getBody.skeletalMuscleMass)
         muscle = format.replace(".", "").toInt()
      }

      if(muscle in 1..267) {
         binding.muscleIndicator1.thumb.setTint(resources.getColor(R.color.black_white))
         binding.muscleIndicator1.progress = muscle
         binding.tvMuscleStatus.text = "낮음"
      }else if(muscle in 267..311) {
         binding.muscleIndicator2.thumb.setTint(resources.getColor(R.color.black_white))
         binding.muscleIndicator2.progress = muscle
         binding.tvMuscleStatus.text = "표준"
      }else {
         binding.muscleIndicator3.thumb.setTint(resources.getColor(R.color.black_white))
         binding.muscleIndicator3.progress = muscle
         binding.tvMuscleStatus.text = "높음"
      }
   }

   override fun onResume() {
      super.onResume()
      dailyList()
   }
}