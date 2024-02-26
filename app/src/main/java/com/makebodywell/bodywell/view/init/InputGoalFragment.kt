package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1
import com.makebodywell.bodywell.view.home.MainActivity

class InputGoalFragment : Fragment() {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputGoalBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.ivBack.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputBodyFragment())
      }

      binding.tvSkip.setOnClickListener {
         startActivity(Intent(activity, MainActivity::class.java))
      }

      binding.cvContinue.setOnClickListener {
         val weightGoal = if(binding.etWeightGoal.text.toString() == "") 0.0 else {binding.etWeightGoal.text.toString().toDouble()}
         val kcalGoal = if(binding.etKcalGoal.text.toString() == "") 0 else {binding.etKcalGoal.text.toString().toInt()}
         val waterUnit = if(binding.etWaterUnit.text.toString() == "") 0 else {binding.etWaterUnit.text.toString().toInt()}
         val waterGoal = if(binding.etWaterGoal.text.toString() == "") 0 else {binding.etWaterGoal.text.toString().toInt()}

         dataManager?.updateUserDouble(TABLE_USER, "weightGoal", weightGoal)
         dataManager?.updateUserInt(TABLE_USER, "kcalGoal", kcalGoal)
         dataManager?.updateUserInt(TABLE_USER, "waterUnit", waterUnit)
         dataManager?.updateUserInt(TABLE_USER, "waterGoal", waterGoal)

         startActivity(Intent(requireActivity(), MainActivity::class.java))
      }

      return binding.root
   }
}