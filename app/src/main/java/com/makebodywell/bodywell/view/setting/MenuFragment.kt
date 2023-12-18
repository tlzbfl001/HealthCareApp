package com.makebodywell.bodywell.view.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.databinding.FragmentMenuBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class MenuFragment : Fragment() {
   private var _binding: FragmentMenuBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentMenuBinding.inflate(layoutInflater)

      binding.tvConnect.setOnClickListener {
         replaceFragment1(requireActivity(), ConnectFragment())
      }

      return binding.root
   }
}