package com.makebodywell.bodywell.view.init

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputGoalBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.food.FoodFragment

class InputGoalFragment : Fragment(), InputActivity.OnBackPressedListener {
   private var _binding: FragmentInputGoalBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as InputActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputGoalBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         CustomUtil.hideKeyboard(requireActivity())
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
         val weightGoal = if(binding.etWeightGoal.text.toString() == "") 55.0 else {binding.etWeightGoal.text.toString().toDouble()}
         val kcalGoal = if(binding.etKcalGoal.text.toString() == "") 2000 else {binding.etKcalGoal.text.toString().toInt()}
         val waterUnit = if(binding.etWaterUnit.text.toString() == "") 200 else {binding.etWaterUnit.text.toString().toInt()}
         val waterGoal = if(binding.etWaterGoal.text.toString() == "") 6 else {binding.etWaterGoal.text.toString().toInt()}

         dataManager?.updateUserDouble(TABLE_USER, "weightGoal", weightGoal)
         dataManager?.updateUserInt(TABLE_USER, "kcalGoal", kcalGoal)
         dataManager?.updateUserInt(TABLE_USER, "waterUnit", waterUnit)
         dataManager?.updateUserInt(TABLE_USER, "waterGoal", waterGoal)

         startActivity(Intent(requireActivity(), MainActivity::class.java))
      }

      return binding.root
   }

   override fun onBackPressed() {
      val activity = activity as InputActivity?
      activity!!.setOnBackPressedListener(null)

      requireActivity().supportFragmentManager.beginTransaction().apply {
         replace(R.id.inputFrame, InputBodyFragment())
         commit()
      }
   }
}