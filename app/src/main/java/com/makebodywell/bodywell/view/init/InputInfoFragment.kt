package com.makebodywell.bodywell.view.init

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentInputInfoBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputInfoFragment : Fragment() {
   private var _binding: FragmentInputInfoBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputInfoBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_signup)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)
      btnConfirm.setOnClickListener {
         replaceInputFragment(requireActivity(), InputBodyFragment())
         dialog.dismiss()
      }

      binding.cvContinue.setOnClickListener {
         dialog.show()
      }

      binding.ivBack.setOnClickListener {
         replaceInputFragment(requireActivity(), InputTermsFragment())
      }
   }
}