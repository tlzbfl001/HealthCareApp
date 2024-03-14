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
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseEditBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDateTime

class ExerciseEditFragment : Fragment(), MainActivity.OnBackPressedListener {
    private var _binding: FragmentExerciseEditBinding? = null
    private val binding get() = _binding!!

    private var dataManager: DataManager? = null
    private var getExercise = Exercise()
    private var intensity = "상"
    private var type = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setOnBackPressedListener(this)
    }

    @SuppressLint("InternalInsetResource", "DiscouragedApi", "ClickableViewAccessibility")
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
        dataManager!!.open()

        val id = arguments?.getString("id")!!.toInt()
        type = arguments?.getString("type")!!

        when(type) {
            "insert" -> {
                getExercise = dataManager!!.getExercise(id)
                binding.tvName.text = getExercise.name
                binding.etTime.setText(getExercise.workoutTime.toString())
                binding.etKcal.setText(getExercise.kcal.toString())
            }
            else -> {
                getExercise = dataManager!!.getDailyExercise(id)
                binding.tvName.text = getExercise.name
                binding.etTime.setText(getExercise.workoutTime.toString())
                binding.etKcal.setText(getExercise.kcal.toString())
            }
        }

        binding.mainLayout.setOnTouchListener { view, motionEvent ->
            hideKeyboard(requireActivity())
            true
        }

        binding.clX.setOnClickListener {
            when(type) {
                "insert" -> replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
                else -> replaceFragment1(requireActivity(), ExerciseListFragment())
            }
        }

        when(getExercise.intensity) {
            "상" -> {
                button1()
            }
            "중" -> {
                button2()
            }
            "하" -> {
                button3()
            }
        }

        binding.tvIntensity1.setOnClickListener {
            button1()
        }

        binding.tvIntensity2.setOnClickListener {
            button2()
        }

        binding.tvIntensity3.setOnClickListener {
            button3()
        }

        binding.cvSave.setOnClickListener {
            val workoutTime = if(binding.etTime.text.toString().trim() != "") binding.etTime.text.toString().toInt() else 0
            val calories = if(binding.etKcal.text.toString().trim() != "") binding.etKcal.text.toString().toInt() else 0

            if(type != "" && id != 0) {
                if(type == "insert") {
                    dataManager!!.insertDailyExercise(Exercise(name = getExercise.name, intensity = intensity, workoutTime = workoutTime,
                        kcal = calories, regDate = selectedDate.toString()))

                    Toast.makeText(requireActivity(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
                }else {
                    dataManager!!.updateDailyExercise(Exercise(id=id, intensity = intensity, workoutTime = workoutTime, kcal = calories))

                    Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }

                val getExercise2 = dataManager!!.getExercise(getExercise.name)
                dataManager!!.updateInt(TABLE_EXERCISE, "useCount", getExercise2.useCount + 1, getExercise2.id)
                dataManager!!.updateStr(TABLE_EXERCISE, "useDate", LocalDateTime.now().toString(), getExercise2.id)

                replaceFragment1(requireActivity(), ExerciseListFragment())
            }else {
                Toast.makeText(requireActivity(), "오류 발생", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun button1() {
        binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
        binding.tvIntensity1.setTextColor(Color.WHITE)
        binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity2.setTextColor(Color.BLACK)
        binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity3.setTextColor(Color.BLACK)
        intensity = "상"
    }

    private fun button2() {
        binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity1.setTextColor(Color.BLACK)
        binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
        binding.tvIntensity2.setTextColor(Color.WHITE)
        binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity3.setTextColor(Color.BLACK)
        intensity = "중"
    }

    private fun button3() {
        binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity1.setTextColor(Color.BLACK)
        binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
        binding.tvIntensity2.setTextColor(Color.BLACK)
        binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
        binding.tvIntensity3.setTextColor(Color.WHITE)
        intensity = "하"
    }

    override fun onBackPressed() {
        val activity = activity as MainActivity?
        activity!!.setOnBackPressedListener(null)

        when(type) {
            "insert" -> replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
            else -> replaceFragment1(requireActivity(), ExerciseListFragment())
        }
    }
}