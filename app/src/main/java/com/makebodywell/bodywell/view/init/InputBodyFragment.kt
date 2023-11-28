package com.makebodywell.bodywell.view.init

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentInputBodyBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputBodyFragment : Fragment() {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   var isTwoDigitDecimal1 = false
   var isThreeDigit1 = false
   var isTwoDigitDecimal2 = false
   var isThreeDigit2 = false

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputBodyBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      binding.tvSkip.setOnClickListener {
         replaceInputFragment(requireActivity(), InputGoalFragment())
      }

      binding.cvContinue.setOnClickListener {
         replaceInputFragment(requireActivity(), InputGoalFragment())
      }

      binding.cvMan.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#5A8EFF"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
         binding.tvMan.setTextColor(Color.WHITE)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.aaa))
         binding.tvWoman.setTextColor(Color.BLACK)
      }

      binding.cvWoman.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.aaa))
         binding.tvMan.setTextColor(Color.BLACK)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#FD7E9B"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), R.color.white))
         binding.tvWoman.setTextColor(Color.WHITE)
      }

      // 소수점입력 설정
      binding.etHeight.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.length < 3) {
               isTwoDigitDecimal1 = false
               isThreeDigit1 = false
            }
            if(!isTwoDigitDecimal1 && s.length == 3) {
               var text = s[0].toString()+s[1].toString()+"."+s[2].toString()
               binding.etHeight.setText(text)
               binding.etHeight.setSelection(text.length)
               isTwoDigitDecimal1 = true
            }
            if(isTwoDigitDecimal1 && s.length == 5) {
               var text = s[0].toString()+s[1].toString()+s[4].toString()
               binding.etHeight.setText(text)
               binding.etHeight.setSelection(text.length)
               isTwoDigitDecimal1 = false
               isThreeDigit1 = true
            }
            if(isThreeDigit1 && s.length == 4) {
               var text = s[0].toString()+s[1].toString()+s[2].toString()+"."+s[3].toString()
               binding.etHeight.setText(text)
               binding.etHeight.setSelection(text.length)
               isThreeDigit1 = false
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etWeight.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.length < 3) {
               isTwoDigitDecimal2 = false
               isThreeDigit2 = false
            }
            if(!isTwoDigitDecimal2 && s.length == 3) {
               var text = s[0].toString()+s[1].toString()+"."+s[2].toString()
               binding.etWeight.setText(text)
               binding.etWeight.setSelection(text.length)
               isTwoDigitDecimal2 = true
            }
            if(isTwoDigitDecimal2 && s.length == 5) {
               var text = s[0].toString()+s[1].toString()+s[4].toString()
               binding.etWeight.setText(text)
               binding.etWeight.setSelection(text.length)
               isTwoDigitDecimal2 = false
               isThreeDigit2 = true
            }
            if(isThreeDigit2 && s.length == 4) {
               var text = s[0].toString()+s[1].toString()+s[2].toString()+"."+s[3].toString()
               binding.etWeight.setText(text)
               binding.etWeight.setSelection(text.length)
               isThreeDigit2 = false
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
            replaceInputFragment(requireActivity(), InputInfoFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}