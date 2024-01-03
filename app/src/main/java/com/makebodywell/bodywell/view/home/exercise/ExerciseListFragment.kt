package com.makebodywell.bodywell.view.home.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.ExerciseListAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseListBinding
import com.makebodywell.bodywell.model.Exercise
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseListFragment : Fragment() {
   private var _binding: FragmentExerciseListBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseListBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()
      setupList()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }

      binding.tvSearch.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
      }

      binding.cv1.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
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
         itemList.add(Exercise(id = getExercise[i].id, category = getExercise[i].category, name = getExercise[i].name,
            workoutTime = getExercise[i].workoutTime, distance = getExercise[i].distance, calories = getExercise[i].calories))
      }

      val adapter = ExerciseListAdapter(requireActivity(), itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter
   }
}