package com.makebodywell.bodywell.view.init

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentInputTermsBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

class InputTermsFragment : Fragment() {
   private var _binding: FragmentInputTermsBinding? = null
   private val binding get() = _binding!!

   private var isAll = true

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputTermsBinding.inflate(layoutInflater)

      binding.ivBack.setOnClickListener {
         startActivity(Intent(activity, LoginActivity::class.java))
      }

      binding.cbAll.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            binding.cb1.isChecked = true
            binding.cb2.isChecked = true
            binding.cb3.isChecked = true
            binding.cb4.isChecked = true
            isAll = true
         }else if(!isChecked && isAll) {
            binding.cb1.isChecked = false
            binding.cb2.isChecked = false
            binding.cb3.isChecked = false
            binding.cb4.isChecked = false
         }
      }

      binding.cb1.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb2.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb3.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb4.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.tvView1.setOnClickListener {
         showBottomDialog("서비스 이용 약관 동의", 1)
      }

      binding.tvView2.setOnClickListener {
         showBottomDialog("개인정보처리방침 동의", 2)
      }

      binding.tvView3.setOnClickListener {
         showBottomDialog("민감정보 수집 및 이용 동의", 3)
      }

      binding.tvView4.setOnClickListener {
         showBottomDialog("마케팅 수신 동의", 4)
      }

      binding.cvContinue.setOnClickListener {
         if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
            replaceInputFragment(requireActivity(), InputInfoFragment())
         }else {
            Toast.makeText(requireActivity(), "필수 이용약관에 모두 체크해주세요.", Toast.LENGTH_SHORT).show()
         }
      }

      return binding.root
   }

   private fun showBottomDialog(title: String, id: Int) {
      val dialog = Dialog(requireActivity())
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.dialog_terms)

      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val clX = dialog.findViewById<ConstraintLayout>(R.id.clX)
      val terms1 = dialog.findViewById<TextView>(R.id.terms1)
      val terms2 = dialog.findViewById<ConstraintLayout>(R.id.terms2)
      val terms3 = dialog.findViewById<ConstraintLayout>(R.id.terms3)
      val terms4 = dialog.findViewById<TextView>(R.id.terms4)

      tvTitle.text = title

      clX.setOnClickListener {
         dialog.dismiss()
      }

      when(id) {
         1 -> terms1.visibility = View.VISIBLE
         2 -> terms2.visibility = View.VISIBLE
         3 -> terms3.visibility = View.VISIBLE
         4 -> terms4.visibility = View.VISIBLE
      }

      dialog.show()
      dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.window!!.setGravity(Gravity.BOTTOM)
   }
}