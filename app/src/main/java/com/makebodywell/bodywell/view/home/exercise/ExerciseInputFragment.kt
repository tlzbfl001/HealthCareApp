package com.makebodywell.bodywell.view.home.exercise

import android.annotation.SuppressLint
import android.content.Context
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
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDateTime

class ExerciseInputFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var intensity = "상"

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

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

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
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
         val workoutTime = if(binding.etTime.text.toString() == "") 0 else binding.etTime.text.toString().trim().toInt()
         val calories = if(binding.etKcal.text.toString() == "") 0 else binding.etKcal.text.toString().trim().toInt()

         if(binding.etName.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "운동명을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.insertExercise(Exercise(name = binding.etName.text.toString().trim(), intensity = intensity, workoutTime = workoutTime,
               kcal = calories, useCount = 1, useDate = LocalDateTime.now().toString()))
            dataManager!!.insertDailyExercise(Exercise(name = binding.etName.text.toString().trim(), intensity = intensity, workoutTime = workoutTime,
               kcal = calories, regDate = selectedDate.toString()))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), ExerciseListFragment())
         }
      }

      return binding.root
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
   }
}