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
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseEditBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment1

class ExerciseEditFragment : Fragment() {
	private var _binding: FragmentExerciseEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager

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

		val getExercise = dataManager.getExercise(id)

		binding.etName.setText(getExercise.name)

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment()
		}

		binding.cvEdit.setOnClickListener {
			val getData = dataManager.getExercise("name", binding.etName.text.toString().trim())

			if(binding.etName.text.toString().trim().isEmpty()) {
				Toast.makeText(context, "운동이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
			}else if(getData.name != "" && (getData.name != getExercise.name)) {
				Toast.makeText(context, "같은 이름의 데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
			}else {
				dataManager.updateStr(EXERCISE, "name", binding.etName.text.toString().trim(), "id", id)
				dataManager.updateInt(EXERCISE, IS_UPDATED, 1, "id", id)
				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
				replaceFragment()
			}
		}

		return binding.root
	}

	private fun replaceFragment() {
		when(arguments?.getString("back")) {
			"1" -> replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
			else -> replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}