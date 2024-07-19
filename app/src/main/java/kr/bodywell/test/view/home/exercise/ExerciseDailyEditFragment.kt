package kr.bodywell.test.view.home.exercise

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.test.R
import kr.bodywell.test.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentExerciseDailyEditBinding
import kr.bodywell.test.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment1

class ExerciseDailyEditFragment : Fragment() {
	private var _binding: FragmentExerciseDailyEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var intensity = "HIGH"

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

		val exercise = dataManager.getDailyExercise("id", id)

		binding.tvName.text = exercise.name
		binding.etTime.setText(exercise.workoutTime.toString())
		binding.etKcal.setText(exercise.kcal.toString())

		when(exercise.intensity) {
			"HIGH" -> unit1()
			"MODERATE" -> unit2()
			"LOW" -> unit3()
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
				dataManager.updateStr(TABLE_DAILY_EXERCISE, "intensity", intensity, "id", id)
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "workoutTime", binding.etTime.text.toString().trim().toInt(), "id", id)
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "kcal", binding.etKcal.text.toString().trim().toInt(), "id", id)
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 1, "id", id)

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
		intensity = "HIGH"
	}

	private fun unit2() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity2.setTextColor(Color.WHITE)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = "MODERATE"
	}

	private fun unit3() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity3.setTextColor(Color.WHITE)
		intensity = "LOW"
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}