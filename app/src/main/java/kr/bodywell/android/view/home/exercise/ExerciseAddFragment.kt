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
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentExerciseAddBinding
import kr.bodywell.android.model.Activities
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import java.time.LocalDateTime
import java.util.UUID

class ExerciseAddFragment : Fragment() {
    private var _binding: FragmentExerciseAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback
    private var getActivity = Activities()
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

        setStatusBar(requireActivity(), binding.mainLayout)

        val id = arguments?.getString("id")!!

        lifecycleScope.launch {
            getActivity = powerSync.getActivity(id)
        }

        binding.tvName.text = getActivity.name

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
            if(binding.etTime.text.toString() == "" || binding.etKcal.text.toString() == "") {
                Toast.makeText(requireActivity(), "데이터를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(binding.etTime.text.toString().toInt() == 0 || binding.etKcal.text.toString().toInt() == 0) {
                Toast.makeText(requireActivity(), "시간, 칼로리를 1이상 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else {
                lifecycleScope.launch {
                    val dateTimeFormat = dateTimeToIso(LocalDateTime.now())
                    val uuid = UuidCreator.getTimeOrderedEpoch()
                    powerSync.insertWorkout(Workout(id = uuid.toString(), name = getActivity.name, calorie = binding.etKcal.text.toString().toInt(),
                        intensity = intensity.name, time = binding.etTime.text.toString().toInt(), date = selectedDate.toString(),
                        createdAt = dateTimeFormat, updatedAt = dateTimeFormat, activityId = id))
                }

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