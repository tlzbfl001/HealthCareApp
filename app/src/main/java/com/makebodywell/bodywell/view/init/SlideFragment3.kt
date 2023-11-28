package com.makebodywell.bodywell.view.init

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.databinding.FragmentSlide3Binding

class SlideFragment3 : Fragment() {
   private var _binding: FragmentSlide3Binding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSlide3Binding.inflate(layoutInflater)

      return binding.root
   }
}