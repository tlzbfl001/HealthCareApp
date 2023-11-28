package com.makebodywell.bodywell.view.report

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentReportDrugBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1

class ReportDrugFragment : Fragment() {
   private var _binding: FragmentReportDrugBinding? = null
   private val binding get() = _binding!!

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentReportDrugBinding.inflate(layoutInflater)

      setupView()

      return binding.root
   }

   private fun setupView() {
      binding.pbBody.max = 100
      binding.pbBody.progress = 50
      binding.pbFood.max = 100
      binding.pbFood.progress = 50
      binding.pbExercise.max = 100
      binding.pbExercise.progress = 50
      binding.pbDrug.max = 100
      binding.pbDrug.progress = 50

      binding.pbBody.setOnClickListener {
         replaceFragment1(requireActivity(), ReportBodyFragment())
      }

      binding.pbFood.setOnClickListener {
         replaceFragment1(requireActivity(), ReportFoodFragment())
      }

      binding.pbExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ReportExerciseFragment())
      }

      binding.pbDrug.setOnClickListener {
         binding.tvBody.setTextColor(resources.getColor(R.color.black))
         binding.tvFood.setTextColor(resources.getColor(R.color.black))
         binding.tvExercise.setTextColor(resources.getColor(R.color.black))
         binding.tvDrug.setTextColor(Color.WHITE)

         binding.clBody.setBackgroundResource(R.drawable.oval_border_gray)
         binding.clFood.setBackgroundResource(R.drawable.oval_border_gray)
         binding.clExercise.setBackgroundResource(R.drawable.oval_border_gray)
         binding.clDrug.setBackgroundResource(R.drawable.oval_report_drug)
      }
   }
}