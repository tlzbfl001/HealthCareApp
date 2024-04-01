package com.makebodywell.bodywell.view.init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.view.home.MainActivity

class InputGoalFragment : Fragment() {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var weightGoal = 55.0
   private var kcalGoal = 2000

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            requireActivity().supportFragmentManager.beginTransaction().apply {
               replace(R.id.inputFrame, InputBodyFragment())
               commit()
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputGoalBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager.open()

      val getUser = dataManager.getUser()

      if(getUser.gender == "MALE") {
         weightGoal = 65.0
         kcalGoal = 2200
         binding.etWeightGoal.hint = weightGoal.toString()
         binding.etKcalGoal.hint = kcalGoal.toString()
      }

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivBack.setOnClickListener {
         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputBodyFragment())
            commit()
         }
      }

      binding.tvSkip.setOnClickListener {
         startActivity(Intent(activity, MainActivity::class.java))
      }

      binding.cvContinue.setOnClickListener {
         if(binding.etWeightGoal.text.toString() != "") weightGoal = binding.etWeightGoal.text.toString().toDouble()
         if(binding.etKcalGoal.text.toString() != "") kcalGoal = binding.etKcalGoal.text.toString().toInt()
         val waterUnit = if(binding.etWaterUnit.text.toString() == "") 200 else {binding.etWaterUnit.text.toString().toInt()}
         val waterGoal = if(binding.etWaterGoal.text.toString() == "") 6 else {binding.etWaterGoal.text.toString().toInt()}

         dataManager.updateUserDouble(TABLE_USER, "weightGoal", weightGoal)
         dataManager.updateUserInt(TABLE_USER, "kcalGoal", kcalGoal)
         dataManager.updateUserInt(TABLE_USER, "waterUnit", waterUnit)
         dataManager.updateUserInt(TABLE_USER, "waterGoal", waterGoal)

         startActivity(Intent(requireActivity(), MainActivity::class.java))
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}