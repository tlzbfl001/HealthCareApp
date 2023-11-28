package com.makebodywell.bodywell.view.home.sleep

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentSleepBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.water.WaterFragment
import com.makebodywell.bodywell.view.home.body.BodyFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.makebodywell.bodywell.view.home.exercise.ExerciseFragment
import com.makebodywell.bodywell.view.home.food.FoodFragment

class SleepFragment : Fragment() {
   private var _binding: FragmentSleepBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSleepBinding.inflate(layoutInflater)

      initView()

      return binding.root
   }

   private fun initView() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_sleep_input)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnSave = dialog.findViewById<CardView>(R.id.btnSave)
      btnSave.setOnClickListener {
         dialog.dismiss()
      }
      binding.cvGoal.setOnClickListener {
         dialog.show()
      }

      binding.clBack.setOnClickListener {
         replaceFragment1(requireActivity(), MainFragment())
      }
      binding.cvFood.setOnClickListener {
         replaceFragment1(requireActivity(), FoodFragment())
      }
      binding.cvWater.setOnClickListener {
         replaceFragment1(requireActivity(), WaterFragment())
      }
      binding.cvExercise.setOnClickListener {
         replaceFragment1(requireActivity(), ExerciseFragment())
      }
      binding.cvBody.setOnClickListener {
         replaceFragment1(requireActivity(), BodyFragment())
      }
      binding.cvDrug.setOnClickListener {
         replaceFragment1(requireActivity(), DrugFragment())
      }
      binding.clRecord.setOnClickListener {
         replaceFragment1(requireActivity(), SleepRecordFragment())
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}