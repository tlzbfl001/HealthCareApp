package com.makebodywell.bodywell.view.home.exercise

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.ExerciseRecord1Adapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseRecord1Binding
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

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseListFragment())
      }

      binding.tvBtn2.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord2Fragment())
      }

      binding.tvBtn3.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseInputFragment())
      }

      val getExercise = dataManager!!.getExercise(LocalDate.now().toString())

      val adapter = ExerciseRecord1Adapter(requireActivity(), getExercise)
      binding.rv.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.rv.adapter = adapter

      return binding.root
   }
}