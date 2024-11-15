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
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.ExerciseListAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseDailyEditBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.InitExercise
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CalendarUtil
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar

class ExerciseDailyEditFragment : Fragment() {
	private var _binding: FragmentExerciseDailyEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
//	private lateinit var dataManager: DataManager
	private var intensity = Constant.HIGH

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity(), ExerciseListFragment())
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

//		dataManager = DataManager(activity)
//		dataManager.open()

		val getWorkout = arguments?.getParcelable<Workout>("workouts")!!

		binding.tvName.text = getWorkout.name
		binding.etTime.setText(getWorkout.time.toString())
		binding.etKcal.setText(getWorkout.calorie.toString())

		when(getWorkout.intensity) {
			Constant.HIGH.name -> unit1()
			Constant.MODERATE.name -> unit2()
			Constant.LOW.name -> unit3()
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment1(requireActivity(), ExerciseListFragment())
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
			if(binding.etTime.text.toString() == "" || binding.etTime.text.toString().toInt() < 1 || binding.etKcal.text.toString() == ""
				|| binding.etKcal.text.toString().toInt() < 1) {
				Toast.makeText(requireActivity(), "시간, 칼로리는 0이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
			}else {
				lifecycleScope.launch {
					powerSync.updateWorkout(Workout(id = getWorkout.id, calorie = binding.etKcal.text.toString().trim().toInt(),
						intensity = intensity.name, time = binding.etTime.text.toString().trim().toInt()))
				}

				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment1(requireActivity(), ExerciseListFragment())
			}
		}

		return binding.root
	}

	private fun unit1() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity1.setTextColor(Color.WHITE)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = Constant.HIGH
	}

	private fun unit2() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity2.setTextColor(Color.WHITE)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = Constant.MODERATE
	}

	private fun unit3() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity3.setTextColor(Color.WHITE)
		intensity = Constant.LOW
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}