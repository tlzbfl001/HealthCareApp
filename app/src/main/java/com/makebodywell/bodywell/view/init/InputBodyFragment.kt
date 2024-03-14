package com.makebodywell.bodywell.view.init

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputBodyBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.food.FoodFragment

class InputBodyFragment : Fragment(), InputActivity.OnBackPressedListener {
   private var _binding: FragmentInputBodyBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var height = 163
   private var weight = 58
   private var gender = "FEMALE"

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as InputActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputBodyBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.ivBack.setOnClickListener {
         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputInfoFragment())
            commit()
         }
      }

      binding.tvSkip.setOnClickListener {
         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputGoalFragment())
            commit()
         }
      }

      binding.cvWoman.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvMan.setTextColor(Color.BLACK)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvWoman.setTextColor(Color.WHITE)
         height = 163
         weight = 58
         gender = "FEMALE"
         binding.etHeight.hint = height.toString()
         binding.etWeight.hint = weight.toString()
      }

      binding.cvMan.setOnClickListener {
         binding.cvMan.setCardBackgroundColor(Color.parseColor("#9F98FF"))
         binding.ivMan.imageTintList = ColorStateList.valueOf(Color.WHITE)
         binding.tvMan.setTextColor(Color.WHITE)
         binding.cvWoman.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
         binding.ivWoman.imageTintList = ColorStateList.valueOf(Color.parseColor("#aaaaaa"))
         binding.tvWoman.setTextColor(Color.BLACK)
         height = 173
         weight = 68
         gender = "MALE"
         binding.etHeight.hint = height.toString()
         binding.etWeight.hint = weight.toString()
      }

      binding.cvContinue.setOnClickListener {
         val height = if(binding.etHeight.text.toString() == "") height.toDouble() else {binding.etHeight.text.toString().toDouble()}
         val weight = if(binding.etWeight.text.toString() == "") weight.toDouble() else {binding.etWeight.text.toString().toDouble()}

         dataManager?.updateUserStr(TABLE_USER, "gender", gender)
         dataManager?.updateUserDouble(TABLE_USER, "height", height)
         dataManager?.updateUserDouble(TABLE_USER, "weight", weight)

         requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.inputFrame, InputGoalFragment())
            commit()
         }
      }

      return binding.root
   }

   override fun onBackPressed() {
      val activity = activity as InputActivity?
      activity!!.setOnBackPressedListener(null)

      requireActivity().supportFragmentManager.beginTransaction().apply {
         replace(R.id.inputFrame, InputInfoFragment())
         commit()
      }
   }
}