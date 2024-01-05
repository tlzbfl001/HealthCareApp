package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.databinding.FragmentSlide1Binding
import com.makebodywell.bodywell.view.HealthConnectTestActivity

class SlideFragment1 : Fragment() {
   private var _binding: FragmentSlide1Binding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSlide1Binding.inflate(layoutInflater)

      binding.ivLogo.setOnClickListener {
//         startActivity(Intent(context, MainActivity::class.java))
         startActivity(Intent(context, HealthConnectTestActivity::class.java))
      }

      return binding.root
   }
}