package com.makebodywell.bodywell.view.init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.databinding.FragmentInputSleepBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputSleepFragment : Fragment() {
   private var _binding: FragmentInputSleepBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputSleepBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceInputFragment(requireActivity(), InputGoalFragment())
      }
      binding.tvSkip.setOnClickListener {
         startActivity(Intent(activity, StartActivity::class.java))
      }
      binding.cvContinue.setOnClickListener {
         startActivity(Intent(activity, StartActivity::class.java))
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceInputFragment((activity as InputActivity), InputGoalFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}