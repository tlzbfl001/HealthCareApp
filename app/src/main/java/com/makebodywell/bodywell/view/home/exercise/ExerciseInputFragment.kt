package com.makebodywell.bodywell.view.home.exercise

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseInputBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import java.time.LocalDate

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var dataManager: DataManager? = null
   private var intensity = "상"

   private var calendarDate = ""

   @SuppressLint("ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseInputBinding.inflate(layoutInflater)

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

      calendarDate = arguments?.getString("calendarDate").toString()
      bundle.putString("calendarDate", calendarDate)

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         CustomUtil.hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment2(requireActivity(), ExerciseRecord1Fragment(), bundle)
      }

      binding.tvIntensity1.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity1.setTextColor(Color.WHITE)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity2.setTextColor(Color.BLACK)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity3.setTextColor(Color.BLACK)
         intensity = "상"
      }

      binding.tvIntensity2.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity1.setTextColor(Color.BLACK)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity2.setTextColor(Color.WHITE)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity3.setTextColor(Color.BLACK)
         intensity = "중"
      }

      binding.tvIntensity3.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity1.setTextColor(Color.BLACK)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity2.setTextColor(Color.BLACK)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity3.setTextColor(Color.WHITE)
         intensity = "하"
      }

      binding.cvSave.setOnClickListener {
         if(binding.etName.text.toString().trim() == "" || binding.etTime.text.toString().trim() == "" ||
            binding.etKcal.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.insertExercise(Exercise(name = binding.etName.text.toString().trim(), intensity = intensity,
               workoutTime = binding.etTime.text.toString().trim(), calories = binding.etKcal.text.toString().trim().toInt(), regDate = calendarDate))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment2(requireActivity(), ExerciseRecord1Fragment(), bundle)
         }
      }

      return binding.root
   }
}