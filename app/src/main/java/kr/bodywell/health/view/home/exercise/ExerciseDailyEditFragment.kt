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
import kr.bodywell.health.databinding.FragmentExerciseDailyEditBinding
import kr.bodywell.health.model.Constant.HIGH
import kr.bodywell.health.model.Constant.LOW
import kr.bodywell.health.model.Constant.MODERATE
import kr.bodywell.health.model.Constant.WORKOUTS
import kr.bodywell.health.model.Workout
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment1
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync

class ExerciseDailyEditFragment : Fragment() {
	private var _binding: FragmentExerciseDailyEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var intensity = HIGH

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity().supportFragmentManager, ExerciseListFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentExerciseDailyEditBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		val getWorkout = arguments?.getParcelable<Workout>(WORKOUTS)!!

		binding.tvName.text = getWorkout.name
		binding.etTime.setText(getWorkout.time.toString())
		binding.etKcal.setText(getWorkout.calorie.toString())

		when(getWorkout.intensity) {
			HIGH -> unit1()
			MODERATE -> unit2()
			LOW -> unit3()
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment1(requireActivity().supportFragmentManager, ExerciseListFragment())
		}

		binding.tvIntensity1.setOnClickListener {
			unit1()
		}

		binding.tvIntensity2.setOnClickListener {
			unit2()
		}

		binding.tvIntensity3.setOnClickListener {
			unit3()
		}

		binding.cvSave.setOnClickListener {
			if(binding.etTime.text.toString() == "" || binding.etTime.text.toString().toInt() < 1 || binding.etKcal.text.toString() == "" || binding.etKcal.text.toString().toInt() < 1) {
				Toast.makeText(requireActivity(), "시간, 칼로리는 1이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
			}else {
				lifecycleScope.launch {
					powerSync.updateWorkout(Workout(id = getWorkout.id, calorie = binding.etKcal.text.toString().trim().toInt(),
						intensity = intensity, time = binding.etTime.text.toString().trim().toInt()))
				}

				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment1(requireActivity().supportFragmentManager, ExerciseListFragment())
			}
		}

		return binding.root
	}

	private fun unit1() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_purple)
		binding.tvIntensity1.setTextColor(Color.WHITE)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = HIGH
	}

	private fun unit2() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_purple)
		binding.tvIntensity2.setTextColor(Color.WHITE)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = MODERATE
	}

	private fun unit3() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_purple)
		binding.tvIntensity3.setTextColor(Color.WHITE)
		intensity = LOW
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}