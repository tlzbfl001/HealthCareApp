package com.makebodywell.bodywell.view.home.exercise

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.adapter.FoodRecord2Adapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentExerciseRecord2Binding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import java.time.LocalDate

class ExerciseRecord2Fragment : Fragment() {
   private var _binding: FragmentExerciseRecord2Binding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentExerciseRecord2Binding.inflate(layoutInflater)

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

      binding.cv1.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseRecord1Fragment())
      }

      binding.cv3.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseInputFragment())
      }
   }

   private fun setupList() {
      val itemList = ArrayList<Food>()

      val getExercise = dataManager!!.getExercise(LocalDate.now().toString())

      for (i in 0 until getExercise.size) {
         itemList.add(Food(name = getExercise[i].name, star = R.drawable.ic_star_rate))
      }

      val adapter = FoodRecord2Adapter(itemList)
      binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
      binding.recyclerView.adapter = adapter
   }
}