package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment

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
         replaceInputFragment(requireActivity(), InputBodyFragment())
      }

      binding.tvSkip.setOnClickListener {
         startActivity(Intent(activity, MainActivity::class.java))
      }

      binding.cvContinue.setOnClickListener {
         val getUser = dataManager!!.getUser()
         var weightGoal = 0.0
         var kcalGoal = 0
         var waterUnit = 0
         var water = 0

         if(binding.etWeightGoal.text.toString() != "") {
            weightGoal = binding.etWeightGoal.text.toString().toDouble()
         }

         if(binding.etKcalGoal.text.toString() != "") {
            kcalGoal = binding.etKcalGoal.text.toString().toInt()
         }

         if(binding.etWaterUnit.text.toString() != "") {
            waterUnit = binding.etWaterUnit.text.toString().toInt()
         }

         if(binding.etWater.text.toString() != "") {
            water = binding.etWater.text.toString().toInt()
         }

         dataManager?.updateDouble(DBHelper.TABLE_USER, "weightGoal", weightGoal, getUser.id)
         dataManager?.updateInt(DBHelper.TABLE_USER, "kcalGoal", kcalGoal, getUser.id)
         dataManager?.updateInt(DBHelper.TABLE_USER, "waterUnit", waterUnit, getUser.id)
         dataManager?.updateInt(DBHelper.TABLE_USER, "waterGoal", water, getUser.id)

         startActivity(Intent(activity, MainActivity::class.java))
      }

      return binding.root
   }
}