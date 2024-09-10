package kr.bodywell.test.view.home.exercise

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentExerciseInputBinding
import kr.bodywell.test.model.Exercise
import kr.bodywell.test.util.CustomUtil.hideKeyboard
import kr.bodywell.test.util.CustomUtil.replaceFragment3
import kr.bodywell.test.util.CustomUtil.setStatusBar
import java.time.LocalDateTime

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager

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

      setStatusBar(requireActivity(), binding.mainLayout)

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clX.setOnClickListener {
         replaceFragment()
      }

      binding.cvSave.setOnClickListener {
         val getExercise = dataManager.getExercise("name", binding.etName.text.toString().trim())

         if(binding.etName.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "운동이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else if(getExercise.name != "") {
            Toast.makeText(context, "같은 이름의 데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.insertExercise(Exercise(name = binding.etName.text.toString().trim(), useCount = 1, useDate = LocalDateTime.now().toString()))
            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment()
         }
      }

      return binding.root
   }

   private fun replaceFragment() {
      when(arguments?.getString("back")) {
         "1" -> replaceFragment3(requireActivity(), ExerciseRecord1Fragment())
         else -> replaceFragment3(requireActivity(), ExerciseRecord2Fragment())
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}