package com.makebodywell.bodywell.view.home.food

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.databinding.FragmentFoodEditBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class FoodEditFragment : Fragment() {
   private var _binding: FragmentFoodEditBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodEditBinding.inflate(layoutInflater)

      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), FoodRecord1Fragment())
      }

      return binding.root
   }
}