package kr.bodywell.test.view.init

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.bodywell.test.databinding.FragmentSlide2Binding

class SlideFragment2 : Fragment() {
   private var _binding: FragmentSlide2Binding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSlide2Binding.inflate(layoutInflater)

      return binding.root
   }
}