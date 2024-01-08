package com.makebodywell.bodywell.view.home.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseDetailBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class ExerciseDetailFragment : Fragment() {
    private var _binding: FragmentExerciseDetailBinding? = null
    private val binding get() = _binding!!

    private var dataManager: DataManager? = null

    private var bundleData: Exercise = Exercise()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseDetailBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        initView()
        setupList()

        return binding.root
    }

    private fun initView() {
        bundleData = arguments?.getParcelable("exercise")!!

        // 텍스트 설정
        binding.tvName.text = bundleData.name
        binding.etWorkoutTime.setText(bundleData.workoutTime.toString())
        binding.etKcal.setText(bundleData.calories.toString())

        binding.ivX.setOnClickListener {
            replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
        }

        binding.tvSave.setOnClickListener {
            val exercise = Exercise(id = bundleData.id, workoutTime = binding.etWorkoutTime.text.toString().toInt(), calories = binding.etKcal.text.toString().toInt())

            dataManager!!.updateExercise(exercise)

            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
        }
    }

    private fun setupList() {

    }
}