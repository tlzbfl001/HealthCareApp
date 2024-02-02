package com.makebodywell.bodywell.view.home.body

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentBodyRecordBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class BodyRecordFragment : Fragment() {
   private var _binding: FragmentBodyRecordBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   private var exerciseLevel = 1
   private var gender = "MALE"

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentBodyRecordBinding.inflate(layoutInflater)

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

      val calendarDate = arguments?.getString("calendarDate").toString()
      val getBody = dataManager!!.getBody(calendarDate)

      // 데이터가 존재하는 경우 데이터 가져와서 수정
      if (getBody.regDate != "") {
         binding.etBmi.setText(getBody.bmi.toString())
         binding.etFat.setText(getBody.fat.toString())
         binding.etMuscle.setText(getBody.muscle.toString())
         binding.etHeight.setText(getBody.height.toString())
         binding.etWeight.setText(getBody.weight.toString())
         binding.etAge.setText(getBody.age.toString())

         when(getBody.gender) {
            "MALE" -> genderUI1()
            else -> genderUI2()
         }

         when(getBody.exerciseLevel) {
            1 -> {
               binding.radioBtn1.isChecked = true
               exerciseLevel = 1
            }
            2 -> {
               binding.radioBtn2.isChecked = true
               exerciseLevel = 2
            }
            3 -> {
               binding.radioBtn3.isChecked = true
               exerciseLevel = 3
            }
            4 -> {
               binding.radioBtn4.isChecked = true
               exerciseLevel = 4
            }
            5 -> {
               binding.radioBtn5.isChecked = true
               exerciseLevel = 5
            }
         }
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }

      binding.tvMan.setOnClickListener {
         genderUI1()
      }

      binding.tvWoman.setOnClickListener {
         genderUI2()
      }

      binding.radioGroup.setOnCheckedChangeListener{ _, checkedId ->
         when(checkedId) {
            R.id.radioBtn1 -> exerciseLevel = 1
            R.id.radioBtn2 -> exerciseLevel = 2
            R.id.radioBtn3 -> exerciseLevel = 3
            R.id.radioBtn4 -> exerciseLevel = 4
            R.id.radioBtn5 -> exerciseLevel = 5
         }
      }

      // BMR 구하기
      binding.cvResult.setOnClickListener {
         if(binding.etHeight.text.toString().trim() == "" || binding.etWeight.text.toString().trim() == "" || binding.etAge.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "내 신체 정보를 전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            var step = 0.0

            when(exerciseLevel) {
               1 -> step = 1.2
               2 -> step = 1.375
               3 -> step = 1.55
               4 -> step = 1.725
               5 -> step = 1.9
            }

            val result = if(gender == "MALE") {
               val num = ((10*binding.etWeight.text.toString().toDouble())+(6.25*binding.etHeight.text.toString().toDouble())-(5*binding.etAge.text.toString().toDouble())+5)*step
               String.format("%.1f", num)
            }else {
               val num = ((10*binding.etWeight.text.toString().toDouble())+(6.25*binding.etHeight.text.toString().toDouble())-(5*binding.etAge.text.toString().toDouble())-161)*step
               String.format("%.1f", num)
            }

            binding.tvBmr.text = result
         }
      }

      binding.cvSave.setOnClickListener {
         var height = binding.etHeight.text.toString()
         var weight = binding.etWeight.text.toString()
         var age = binding.etAge.text.toString()
         var fat = binding.etFat.text.toString()
         var muscle = binding.etMuscle.text.toString()
         var bmi = binding.etBmi.text.toString()
         var bmr = binding.tvBmr.text.toString()

         if(height == "") height = "0.0"
         if(weight == "") weight = "0.0"
         if(age == "") age = "0"
         if(fat == "") fat = "0.0"
         if(muscle == "") muscle = "0.0"
         if(bmi == "") bmi = "0.0"
         if(bmr == "") bmr = "0.0"

         if(getBody.regDate == "") {
            dataManager!!.insertBody(Body(height = height.toDouble(), weight = weight.toDouble(), age = age.toInt(), gender = gender, exerciseLevel = exerciseLevel,
               fat = fat.toDouble(), muscle = muscle.toDouble(), bmi = bmi.toDouble(), bmr = bmr.toDouble(), regDate = calendarDate))
            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.updateBody(Body(id = getBody.id, height = height.toDouble(), weight = weight.toDouble(), age = age.toInt(), gender = gender,
               exerciseLevel = exerciseLevel, fat = fat.toDouble(), muscle = muscle.toDouble(), bmi = bmi.toDouble(), bmr = bmr.toDouble()))
            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
         }

         replaceFragment1(requireActivity(), BodyFragment())
      }

      return binding.root
   }

   private fun genderUI1() {
      binding.tvMan.setBackgroundResource(R.drawable.rec_25_gray)
      binding.tvMan.setTextColor(Color.WHITE)
      binding.tvWoman.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvWoman.setTextColor(Color.BLACK)
      gender = "MALE"
   }

   private fun genderUI2() {
      binding.tvMan.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMan.setTextColor(Color.BLACK)
      binding.tvWoman.setBackgroundResource(R.drawable.rec_25_gray)
      binding.tvWoman.setTextColor(Color.WHITE)
      gender = "FEMALE"
   }
}