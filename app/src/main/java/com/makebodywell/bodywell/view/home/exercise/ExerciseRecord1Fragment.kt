package com.makebodywell.bodywell.view.home.exercise

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.ExerciseListAdapter
import com.makebodywell.bodywell.adapter.ExerciseRecord1Adapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseRecord1Binding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseRecord1Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord1Binding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord1Binding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupList()

      return binding.root
   }

   private fun initView() {
      binding.ivX.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      binding.cv2.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
      }

      binding.cv3.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseInputFragment())
      }
   }

   private fun setupList() {
      val itemList = ArrayList<Exercise>()

      val getExercise = dataManager!!.getExercise(LocalDate.now().toString())

      for(i in 0 until getExercise.size) {
         itemList.add(Exercise(id = getExercise[i].id, name = getExercise[i].name, workoutTime = getExercise[i].workoutTime, calories = getExercise[i].calories))
      }

      val adapter = ExerciseRecord1Adapter(requireActivity(), itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter
   }
}