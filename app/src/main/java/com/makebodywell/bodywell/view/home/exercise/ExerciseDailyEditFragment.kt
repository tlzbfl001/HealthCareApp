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
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.ExerciseListAdapter
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseDailyEditBinding
import com.makebodywell.bodywell.databinding.FragmentExerciseListBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainActivity

class ExerciseDailyEditFragment : Fragment() {
	private var _binding: FragmentExerciseDailyEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var intensity = ""

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity(), ExerciseListFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	@SuppressLint("ClickableViewAccessibility")
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

		val exercise = dataManager.getDailyExercise(id)

		binding.tvName.text = exercise.name
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
			if(id > -1) {
				dataManager.updateStr(TABLE_DAILY_EXERCISE, "intensity", intensity, id)
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "workoutTime", binding.etTime.text.toString().trim().toInt(), id)
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "kcal", binding.etKcal.text.toString().trim().toInt(), id)

				Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment1(requireActivity(), ExerciseListFragment())
			}else {
				Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
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

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}