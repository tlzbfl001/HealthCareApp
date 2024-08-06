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
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseAddBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import java.time.LocalDateTime

class ExerciseAddFragment : Fragment() {
    private var _binding: FragmentExerciseAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback
    private lateinit var dataManager: DataManager
    private var exercise = Exercise()
    private var intensity = Constant.HIGH

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
            intensity = Constant.HIGH
        }

        binding.tvIntensity2.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
            binding.tvIntensity2.setTextColor(Color.WHITE)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity3.setTextColor(Color.BLACK)
            intensity = Constant.MODERATE
        }

        binding.tvIntensity3.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity2.setTextColor(Color.BLACK)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
            binding.tvIntensity3.setTextColor(Color.WHITE)
            intensity = Constant.LOW
        }

        binding.cvSave.setOnClickListener {
            if(binding.etTime.text.toString().toInt() == 0 || binding.etKcal.text.toString().toInt() == 0) {
                Toast.makeText(requireActivity(), "시간, 칼로리를 1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                dataManager.insertDailyExercise(Exercise(name = exercise.name, intensity = intensity.name, workoutTime = binding.etTime.text.toString().toInt(),
                    kcal = binding.etKcal.text.toString().toInt(), createdAt = selectedDate.toString()))

                val getExercise = dataManager.getExercise("name", exercise.name)
                dataManager.updateInt(EXERCISE, "useCount", getExercise.useCount + 1, "id", getExercise.id)
                dataManager.updateStr(EXERCISE, "useDate", LocalDateTime.now().toString(), "id", getExercise.id)

                Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                replaceFragment3(requireActivity(), ExerciseListFragment())
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