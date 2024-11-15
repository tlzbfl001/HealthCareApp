package kr.bodywell.android.view.home.exercise

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.ExerciseAdapter
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentExerciseBinding
import kr.bodywell.android.model.GoalInit
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.getExerciseCalories
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate

class ExerciseFragment : Fragment() {
   private var _binding: FragmentExerciseBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private val viewModel: MainViewModel by activityViewModels()
   private var adapter: ExerciseAdapter? = null
   private var dailyGoal = GoalInit()
   private var sum = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseBinding.inflate(layoutInflater)

//      dataManager = DataManager(activity)
//      dataManager.open()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)

      tvTitle.text = "운동 / 목표 칼로리 입력"
      btnSave.setCardBackgroundColor(Color.parseColor("#B3F6BD4B"))
      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
//            if(dailyGoal.createdAt == "") {
//               dataManager.insertGoal(GoalInit(exercise = et.text.toString().toInt(), createdAt = selectedDate.toString()))
//               dailyGoal = dataManager.getGoal(selectedDate.toString())
//            }else {
//               dataManager.updateInt(GOAL, EXERCISE, et.text.toString().toInt(), selectedDate.toString())
//               dataManager.updateInt(GOAL, IS_UPDATED, 1, "id", dailyGoal.id)
//            }

            binding.pbExercise.max = et.text.toString().toInt()
            binding.tvGoal.text = "${et.text} kcal"

            val remain = et.text.toString().toInt() - sum
            if(remain > 0) {
               binding.tvRemain.text = "$remain kcal"
            }else {
               binding.tvRemain.text = "0 kcal"
            }

            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      // 목표 초기화
      binding.pbExercise.setProgressStartColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressEndColor(Color.TRANSPARENT)
      binding.tvConsume.text = "0 kcal"
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

//      dailyGoal = dataManager.getGoal(selectedDate.toString())
      sum = getExerciseCalories(requireActivity(), selectedDate.toString())

      if(sum > 0) {
         binding.pbExercise.setProgressStartColor(resources.getColor(R.color.exercise))
         binding.pbExercise.setProgressEndColor(resources.getColor(R.color.exercise))
         binding.pbExercise.max = dailyGoal.exercise
         binding.pbExercise.progress = sum
      }

      binding.tvGoal.text = "${dailyGoal.exercise} kcal"
      binding.tvConsume.text = "$sum kcal"

      val remain = dailyGoal.exercise - sum
      if(remain > 0) {
         binding.tvRemain.text = "$remain kcal"
      }else {
         binding.tvRemain.text = "0 kcal"
      }

//      val getExercise = dataManager.getAllWorkout(CREATED_AT, selectedDate.toString())
      lifecycleScope.launch {
         val getAllWorkout = powerSync.getAllWorkout(selectedDate.toString()) as ArrayList<Workout>

         adapter = ExerciseAdapter(getAllWorkout)
         binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv.adapter = adapter
      }
   }
}