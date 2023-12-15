package com.makebodywell.bodywell.view.home.sleep

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.databinding.FragmentSleepRecordBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class SleepRecordFragment : Fragment() {
   private var _binding: FragmentSleepRecordBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepRecordBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceFragment1(requireActivity(), SleepFragment())
      }
   }
}