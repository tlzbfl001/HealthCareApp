package kr.bodywell.health.view.init

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import kr.bodywell.health.R
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.databinding.FragmentInputGoalBinding
import kr.bodywell.health.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.health.view.home.MainActivity

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

      if(getUser.gender == "Male") {
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
         val intent = Intent(activity, MainActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
         startActivity(intent)
      }

      binding.cvContinue.setOnClickListener {
         if(binding.etWeightGoal.text.toString() != "") weightGoal = binding.etWeightGoal.text.toString().toDouble()
         if(binding.etKcalGoal.text.toString() != "") kcalGoal = binding.etKcalGoal.text.toString().toInt()
         val waterUnit = if(binding.etWaterUnit.text.toString() == "") 200 else binding.etWaterUnit.text.toString().toInt()
         val waterGoal = if(binding.etWaterGoal.text.toString() == "") 6 else binding.etWaterGoal.text.toString().toInt()

         dataManager.updateUserDouble("weightGoal", weightGoal)
         dataManager.updateUserInt("kcalGoal", kcalGoal)
         dataManager.updateUserInt("waterGoal", waterGoal)
         dataManager.updateUserInt("waterUnit", waterUnit)

         val intent = Intent(activity, MainActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
         startActivity(intent)
      }

      return binding.root
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}