package kr.bodywell.android.view.home.exercise

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseInputBinding
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDateTime

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var intensity = "HIGH"

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment()
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment()
      }

      binding.tvIntensity1.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity1.setTextColor(Color.WHITE)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity2.setTextColor(Color.BLACK)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity3.setTextColor(Color.BLACK)
         intensity = "HIGH"
      }

      binding.tvIntensity2.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity1.setTextColor(Color.BLACK)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity2.setTextColor(Color.WHITE)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity3.setTextColor(Color.BLACK)
         intensity = "MODERATE"
      }

      binding.tvIntensity3.setOnClickListener {
         binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity1.setTextColor(Color.BLACK)
         binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
         binding.tvIntensity2.setTextColor(Color.BLACK)
         binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
         binding.tvIntensity3.setTextColor(Color.WHITE)
         intensity = "LOW"
      }

      binding.cvSave.setOnClickListener {
         if(binding.etName.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "운동명을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(binding.etTime.text.toString() == "" || binding.etTime.text.toString().toInt() < 1 || binding.etKcal.text.toString() == ""
            || binding.etKcal.text.toString().toInt() < 1) {
            Toast.makeText(requireActivity(), "데이터는 0이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.insertExercise(Exercise(uid = "", name = binding.etName.text.toString().trim(), intensity = intensity, workoutTime = binding.etTime.text.toString().toInt(),
               kcal = binding.etKcal.text.toString().toInt(), useCount = 1, useDate = LocalDateTime.now().toString()))

            val getExercise = dataManager.getExercise("name", binding.etName.text.toString().trim())

            dataManager.insertDailyExercise(Exercise(uid = "", exerciseId = getExercise.id, name = binding.etName.text.toString().trim(), intensity = intensity,
               workoutTime = binding.etTime.text.toString().toInt(), kcal = binding.etKcal.text.toString().toInt(), regDate = selectedDate.toString()))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), ExerciseListFragment())
         }
      }

      return binding.root
   }

   private fun replaceFragment() {
      when(arguments?.getString("back")) {
         "1" -> replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
         else -> replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}