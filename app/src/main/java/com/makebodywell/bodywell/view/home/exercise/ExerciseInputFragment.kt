package com.makebodywell.bodywell.view.home.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseInputBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseInputFragment : Fragment() {
   private var _binding: FragmentExerciseInputBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseInputBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      binding.cvAdd.setOnClickListener {
         if(binding.etName.text.toString().trim() == "" || binding.etIntensity.text.toString().trim() == "" ||
            binding.etTime.text.toString().trim() == "" || binding.etKcal.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "전부 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager!!.insertExercise(Exercise(name = binding.etName.text.toString(), intensity = binding.etName.text.toString(),
               workoutTime = binding.etTime.text.toString().toInt(), calories = binding.etKcal.text.toString().toInt(),
               regDate = LocalDate.now().toString()))

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
         }
      }

      return binding.root
   }
}