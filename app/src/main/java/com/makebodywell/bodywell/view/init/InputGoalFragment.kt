package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment
import com.makebodywell.bodywell.util.MyApp

class InputGoalFragment : Fragment() {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

   private val bundle = Bundle()
   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputGoalBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      val getUser = dataManager!!.getUser(MyApp.prefs.userId())
      Log.d(TAG, "InputGoalFragment user: $getUser")

      binding.ivBack.setOnClickListener {
         replaceInputFragment(requireActivity(), InputBodyFragment())
      }

      binding.tvSkip.setOnClickListener {
         startActivity(Intent(activity, MainActivity::class.java))
      }

      binding.cvContinue.setOnClickListener {
         var weightGoal = 0.0
         var kcalGoal = 0
         var waterUnit = 0
         var waterGoal = 0

         if(binding.etWeightGoal.text.toString() != "") {
            weightGoal = binding.etWeightGoal.text.toString().toDouble()
         }

         if(binding.etKcalGoal.text.toString() != "") {
            kcalGoal = binding.etKcalGoal.text.toString().toInt()
         }

         if(binding.etWaterUnit.text.toString() != "") {
            waterUnit = binding.etWaterUnit.text.toString().toInt()
         }

         if(binding.etWaterGoal.text.toString() != "") {
            waterGoal = binding.etWaterGoal.text.toString().toInt()
         }

         dataManager?.updateDouble(TABLE_USER, "weightGoal", weightGoal, getUser.id)
         dataManager?.updateInt(TABLE_USER, "kcalGoal", kcalGoal, getUser.id)
         dataManager?.updateInt(TABLE_USER, "waterUnit", waterUnit, getUser.id)
         dataManager?.updateInt(TABLE_USER, "waterGoal", waterGoal, getUser.id)

         startActivity(Intent(requireActivity(), MainActivity::class.java))
      }

      return binding.root
   }
}