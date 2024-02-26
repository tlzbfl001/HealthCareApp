package com.makebodywell.bodywell.view.init

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputBodyBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1

class InputBodyFragment : Fragment() {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var gender = "MALE"

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.ivBack.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputInfoFragment())
      }

      binding.tvSkip.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputGoalFragment())
      }

      binding.cvMan.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvMan.setTextColor(Color.WHITE)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvWoman.setTextColor(Color.BLACK)

         gender = "MALE"
      }

      binding.cvWoman.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvMan.setTextColor(Color.BLACK)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvWoman.setTextColor(Color.WHITE)

         gender = "FEMALE"
      }

      binding.cvContinue.setOnClickListener {
         val height = if(binding.etHeight.text.toString() == "") 0.0 else {binding.etHeight.text.toString().toDouble()}
         val weight = if(binding.etWeight.text.toString() == "") 0.0 else {binding.etWeight.text.toString().toDouble()}

         dataManager?.updateUserStr(TABLE_USER, "gender", gender)
         dataManager?.updateUserDouble(TABLE_USER, "height", height)
         dataManager?.updateUserDouble(TABLE_USER, "weight", weight)

         replaceLoginFragment1(requireActivity(), InputGoalFragment())
      }

      return binding.root
   }
}