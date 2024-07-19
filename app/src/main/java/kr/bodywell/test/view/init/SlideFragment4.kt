package kr.bodywell.test.view.init

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.bodywell.test.databinding.FragmentSlide4Binding

class SlideFragment4 : Fragment() {
   private var _binding: FragmentSlide4Binding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSlide4Binding.inflate(layoutInflater)

      return binding.root
   }
}