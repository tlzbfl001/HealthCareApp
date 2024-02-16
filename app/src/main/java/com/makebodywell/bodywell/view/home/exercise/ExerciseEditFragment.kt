package com.makebodywell.bodywell.view.home.exercise

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.util.MyApp

class ExerciseEditFragment : Fragment() {
    private var _binding: FragmentExerciseEditBinding? = null
    private val binding get() = _binding!!

    private var bundle = Bundle()
    private var dataManager: DataManager? = null
    private var intensity = "상"
    private var calendarDate = ""

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
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

        calendarDate = arguments?.getString("calendarDate")!!
        bundle.putString("calendarDate", calendarDate)

        val id = arguments?.getString("id")!!.toInt()

        val getExercise = dataManager!!.getExercise(id)

        // 텍스트 설정
        binding.tvName.text = getExercise.name
        binding.etTime.setText(getExercise.workoutTime)
        binding.etKcal.setText(getExercise.calories.toString())

        binding.clX.setOnClickListener {
            replaceFragment2(requireActivity(), ExerciseRecord1Fragment(), bundle)
        }

        when(getExercise.intensity) {
            "상" -> {
                binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
                binding.tvIntensity1.setTextColor(Color.WHITE)
                binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity2.setTextColor(Color.BLACK)
                binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity3.setTextColor(Color.BLACK)
                intensity = "상"
            }
            "중" -> {
                binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity1.setTextColor(Color.BLACK)
                binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
                binding.tvIntensity2.setTextColor(Color.WHITE)
                binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity3.setTextColor(Color.BLACK)
                intensity = "중"
            }
            "하" -> {
                binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity1.setTextColor(Color.BLACK)
                binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
                binding.tvIntensity2.setTextColor(Color.BLACK)
                binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
                binding.tvIntensity3.setTextColor(Color.WHITE)
                intensity = "하"
            }
        }

        binding.tvIntensity1.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_yellow)
            binding.tvIntensity1.setTextColor(Color.WHITE)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity2.setTextColor(Color.BLACK)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity3.setTextColor(Color.BLACK)
            intensity = "상"
        }

        binding.tvIntensity2.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_yellow)
            binding.tvIntensity2.setTextColor(Color.WHITE)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity3.setTextColor(Color.BLACK)
            intensity = "중"
        }

        binding.tvIntensity3.setOnClickListener {
            binding.tvIntensity1.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity1.setTextColor(Color.BLACK)
            binding.tvIntensity2.setBackgroundResource(R.drawable.rec_25_border_gray)
            binding.tvIntensity2.setTextColor(Color.BLACK)
            binding.tvIntensity3.setBackgroundResource(R.drawable.rec_25_yellow)
            binding.tvIntensity3.setTextColor(Color.WHITE)
            intensity = "하"
        }

        binding.cvSave.setOnClickListener {
            dataManager!!.updateString(TABLE_EXERCISE, "intensity", intensity, getExercise.id)
            dataManager!!.updateString(TABLE_EXERCISE, "workoutTime", binding.etTime.text.toString(), getExercise.id)
            dataManager!!.updateInt(TABLE_EXERCISE, "calories", binding.etKcal.text.toString().toInt(), getExercise.id)

            Toast.makeText(requireActivity(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment2(requireActivity(), ExerciseRecord1Fragment(), bundle)
        }

        return binding.root
    }
}