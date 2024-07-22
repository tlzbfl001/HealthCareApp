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
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseAddBinding
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment3
import java.time.LocalDateTime

class ExerciseAddFragment : Fragment() {
    private var _binding: FragmentExerciseAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback
    private lateinit var dataManager: DataManager
    private var exercise = Exercise()

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
        _binding = FragmentExerciseAddBinding.inflate(layoutInflater)

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

        val id = arguments?.getString("id")!!.toInt()

        exercise = dataManager.getExercise(id)

        binding.tvName.text = exercise.name
        binding.tvTime.text = exercise.workoutTime.toString()
        binding.tvKcal.text = exercise.kcal.toString()

        when(exercise.intensity) {
            "HIGH" -> binding.tvIntensity.text = "상"
            "MODERATE" -> binding.tvIntensity.text = "중"
            "LOW" -> binding.tvIntensity.text = "하"
        }

        binding.mainLayout.setOnTouchListener { _, _ ->
            hideKeyboard(requireActivity())
            true
        }

        binding.clX.setOnClickListener {
            replaceFragment()
        }

        binding.cvSave.setOnClickListener {
            val workoutTime = if(binding.tvTime.text.toString().trim() == "") 0 else binding.tvTime.text.toString().toInt()
            val calories = if(binding.tvKcal.text.toString().trim() == "") 0 else binding.tvKcal.text.toString().toInt()

            dataManager.insertDailyExercise(Exercise(name = exercise.name, intensity = exercise.intensity, workoutTime = workoutTime, kcal = calories,
                createdAt = selectedDate.toString()))

            val getExercise = dataManager.getExercise("name", exercise.name)
            dataManager.updateInt(TABLE_EXERCISE, "useCount", getExercise.useCount + 1, "id", getExercise.id)
            dataManager.updateStr(TABLE_EXERCISE, "useDate", LocalDateTime.now().toString(), "id", getExercise.id)

            Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment3(requireActivity(), ExerciseListFragment())
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