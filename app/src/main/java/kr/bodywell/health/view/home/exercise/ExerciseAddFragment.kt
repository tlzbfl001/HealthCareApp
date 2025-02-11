package kr.bodywell.health.view.home.exercise

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
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentExerciseAddBinding
import kr.bodywell.health.model.ActivityData
import kr.bodywell.health.model.Constant.HIGH
import kr.bodywell.health.model.Constant.LOW
import kr.bodywell.health.model.Constant.MODERATE
import kr.bodywell.health.model.Workout
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.getUUID
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import java.util.Calendar

class ExerciseAddFragment : Fragment() {
    private var _binding: FragmentExerciseAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback
    private var getActivity = ActivityData()
    private var intensity = HIGH

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                replaceFragment3(requireActivity().supportFragmentManager, ExerciseRecord1Fragment())
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
            replaceFragment3(requireActivity().supportFragmentManager, ExerciseRecord1Fragment())
        }

        binding.tvIntensity1.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_purple)
            binding.tvIntensity1.setTextColor(Color.WHITE)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity2.setTextColor(Color.BLACK)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity3.setTextColor(Color.BLACK)
            intensity = HIGH
        }

        binding.tvIntensity2.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_purple)
            binding.tvIntensity2.setTextColor(Color.WHITE)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity3.setTextColor(Color.BLACK)
            intensity = MODERATE
        }

        binding.tvIntensity3.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity2.setTextColor(Color.BLACK)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_purple)
            binding.tvIntensity3.setTextColor(Color.WHITE)
            intensity = LOW
        }

        binding.cvSave.setOnClickListener {
            if(binding.etTime.text.toString() == "" || binding.etKcal.text.toString() == "") {
                Toast.makeText(requireActivity(), "데이터를 입력하세요.", Toast.LENGTH_SHORT).show()
            }else if(binding.etTime.text.toString().toInt() == 0 || binding.etKcal.text.toString().toInt() == 0) {
                Toast.makeText(requireActivity(), "시간, 칼로리는 1이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
            }else {
                lifecycleScope.launch {
                    powerSync.insertWorkout(Workout(id = getUUID(), name = getActivity.name, calorie = binding.etKcal.text.toString().toInt(),
                        intensity = intensity, time = binding.etTime.text.toString().toInt(), date = selectedDate.toString(),
                        createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance()), activityId = id))
                }

                Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                replaceFragment3(requireActivity().supportFragmentManager, ExerciseListFragment())
            }
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}