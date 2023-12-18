package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputGoalFragment : Fragment() {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

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
         startActivity(Intent(activity, StartActivity::class.java))
      }

      binding.cvContinue.setOnClickListener {
         startActivity(Intent(activity, StartActivity::class.java))
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
}