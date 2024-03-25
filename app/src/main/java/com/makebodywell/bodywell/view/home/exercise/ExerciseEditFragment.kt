package com.makebodywell.bodywell.view.home.exercise

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseEditBinding
import com.makebodywell.bodywell.databinding.FragmentExerciseInputBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.food.FoodRecord1Fragment
import java.time.LocalDateTime

class ExerciseEditFragment : Fragment() {
	private var _binding: FragmentExerciseEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var intensity = ""

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	@SuppressLint("ClickableViewAccessibility", "InternalInsetResource")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentExerciseEditBinding.inflate(layoutInflater)

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

		val exercise = dataManager.getExercise(id)

		binding.etName.setText(exercise.name)
		binding.etTime.setText(exercise.workoutTime.toString())
		binding.etKcal.setText(exercise.kcal.toString())

		when(exercise.intensity) {
			"상" -> unit1()
			"중" -> unit2()
			"하" -> unit3()
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment()
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

		binding.cvEdit.setOnClickListener {
			if(id > -1) {
				val getData = dataManager.getExercise("name", binding.etName.text.toString().trim())

				if(binding.etName.text.toString().trim() == "") {
					Toast.makeText(context, "운동명을 입력해주세요.", Toast.LENGTH_SHORT).show()
				}else if (getData.name != "" && (getData.name != exercise.name)) {
					Toast.makeText(context, "같은 이름의 데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
				}else {
					dataManager.updateExercise(Exercise(id = id, name = binding.etName.text.toString().trim(), intensity = intensity,
						workoutTime = binding.etTime.text.toString().trim().toInt(), kcal = binding.etKcal.text.toString().trim().toInt()))

					Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
					replaceFragment()
				}
			}else {
				Toast.makeText(context, "수정 실패", Toast.LENGTH_SHORT).show()
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
		intensity = "상"
	}

	private fun unit2() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity2.setTextColor(Color.WHITE)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity3.setTextColor(Color.BLACK)
		intensity = "중"
	}

	private fun unit3() {
		binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity1.setTextColor(Color.BLACK)
		binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
		binding.tvIntensity2.setTextColor(Color.BLACK)
		binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
		binding.tvIntensity3.setTextColor(Color.WHITE)
		intensity = "하"
	}

	private fun replaceFragment() {
		when(arguments?.getString("back")) {
			"1" -> CustomUtil.replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
			else -> CustomUtil.replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}