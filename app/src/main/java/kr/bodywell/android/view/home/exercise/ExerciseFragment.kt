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
import kr.bodywell.android.databinding.FragmentExerciseBinding
import kr.bodywell.android.model.Constant.GOALS
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Workout
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.getExerciseCalories
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.view.MainViewModel
import java.time.LocalDate
import java.util.Calendar

class ExerciseFragment : Fragment() {
   private var _binding: FragmentExerciseBinding? = null
   private val binding get() = _binding!!

   private val viewModel: MainViewModel by activityViewModels()
   private var adapter: ExerciseAdapter? = null
   private var getGoal = Goal()
   private var sum = 0

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseBinding.inflate(layoutInflater)

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val et = dialog.findViewById<TextView>(R.id.et)
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      tvTitle.text = "운동 / 목표 칼로리 입력"
      btnSave.setCardBackgroundColor(Color.parseColor("#FF5B5B"))

      btnSave.setOnClickListener {
         if(et.text.toString().trim() == "") {
            Toast.makeText(requireActivity(), "목표를 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            lifecycleScope.launch {
               if(getGoal.id == "") {
                  powerSync.insertGoal(Goal(id = getUUID(), kcalOfWorkout = et.text.toString().toInt(), date = selectedDate.toString(),
                     createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance())))
                  getGoal = powerSync.getGoal(selectedDate.toString())
               }else {
                  powerSync.updateData(GOALS, "kcal_of_workout", et.text.toString(), getGoal.id)
               }
            }

            binding.pbExercise.max = et.text.toString().toInt()
            binding.tvGoal.text = "${et.text} kcal"

            val remain = et.text.toString().toInt() - sum
            if(remain > 0) binding.tvRemain.text = "$remain kcal" else binding.tvRemain.text = "0 kcal"

            dialog.dismiss()
         }
      }

      binding.clGoal.setOnClickListener {
         dialog.show()
      }

      binding.clRecord.setOnClickListener {
         replaceFragment1(parentFragmentManager, ExerciseListFragment())
      }

      viewModel.dateVM.observe(viewLifecycleOwner, Observer<LocalDate> {
         dailyView()
      })

      dailyView()

      return binding.root
   }

   private fun dailyView() {
      binding.pbExercise.setProgressStartColor(Color.TRANSPARENT)
      binding.pbExercise.setProgressEndColor(Color.TRANSPARENT)
      binding.tvConsume.text = "0 kcal"
      binding.tvGoal.text = "0 kcal"
      binding.tvRemain.text = "0 kcal"

      lifecycleScope.launch {
         getGoal = powerSync.getGoal(selectedDate.toString())
         sum = getExerciseCalories(selectedDate.toString())

         if(sum > 0) {
            binding.pbExercise.setProgressStartColor(resources.getColor(R.color.exercise))
            binding.pbExercise.setProgressEndColor(resources.getColor(R.color.exercise))
            binding.pbExercise.max = getGoal.kcalOfWorkout
            binding.pbExercise.progress = sum
         }

         binding.tvGoal.text = "${getGoal.kcalOfWorkout} kcal"
         binding.tvConsume.text = "$sum kcal"

         val remain = getGoal.kcalOfWorkout - sum
         if(remain > 0) binding.tvRemain.text = "$remain kcal" else binding.tvRemain.text = "0 kcal"

         val getAllWorkout = powerSync.getWorkouts(selectedDate.toString()) as ArrayList<Workout>

         adapter = ExerciseAdapter(getAllWorkout)
         binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
         binding.rv.adapter = adapter
      }
   }
}