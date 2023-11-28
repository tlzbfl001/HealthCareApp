package com.makebodywell.bodywell.view.init

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputGoalFragment : Fragment() {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   var isTwoDigitDecimal = false
   var isThreeDigit = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputGoalBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         replaceInputFragment(requireActivity(), InputBodyFragment())
      }

      binding.tvSkip.setOnClickListener {
         replaceInputFragment(requireActivity(), InputSleepFragment())
      }

      binding.cvContinue.setOnClickListener {
         replaceInputFragment(requireActivity(), InputSleepFragment())
      }

      binding.etWeightGoal.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(!isTwoDigitDecimal && s.length == 3) {
               val text = s[0].toString()+s[1].toString()+"."+s[2].toString()
               binding.etWeightGoal.setText(text)
               binding.etWeightGoal.setSelection(text.length)
               isTwoDigitDecimal = true
            }
            if(isTwoDigitDecimal && s.length == 5) {
               val text = s[0].toString()+s[1].toString()+s[4].toString()
               binding.etWeightGoal.setText(text)
               binding.etWeightGoal.setSelection(text.length)
               isTwoDigitDecimal = false
               isThreeDigit = true
            }
            if(isThreeDigit && s.length == 4) {
               val text = s[0].toString()+s[1].toString()+s[2].toString()+"."+s[3].toString()
               binding.etWeightGoal.setText(text)
               binding.etWeightGoal.setSelection(text.length)
               isThreeDigit = false
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceInputFragment(requireActivity(), InputBodyFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}