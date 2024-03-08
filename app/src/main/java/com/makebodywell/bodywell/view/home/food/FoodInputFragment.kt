package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodInputBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDateTime

class FoodInputFragment : Fragment(), MainActivity.OnBackPressedListener {
   private var _binding: FragmentFoodInputBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()
   private var type = ""

   override fun onAttach(context: Context) {
      super.onAttach(context)
      (context as MainActivity).setOnBackPressedListener(this)
   }

   @SuppressLint("DiscouragedApi", "InternalInsetResource", "ClickableViewAccessibility")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodInputBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      val dataManager = DataManager(requireActivity())
      dataManager.open()

      type = arguments?.getString("type").toString()
      bundle.putString("type", type)

      binding.mainLayout.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.constraint.setOnTouchListener { view, motionEvent ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
      }

      binding.cvSave.setOnClickListener {
         var name = ""
         var unit = ""
         var amount = 0
         var kcal = 0
         var carbohydrate = 0.0
         var protein = 0.0
         var fat = 0.0
         var salt = 0.0
         var sugar = 0.0

         if(binding.etName.text.toString().trim() == "" && binding.etUnit.text.toString().trim() == "" && binding.etAmount.text.toString().trim() == "" &&
            binding.etKcal.text.toString().trim() == "" && binding.etCar.text.toString().trim() == "" && binding.etProtein.text.toString().trim() == "" &&
            binding.etFat.text.toString().trim() == "" && binding.etSalt.text.toString().trim() == "" && binding.etSugar.text.toString().trim() == "") {
            name = "사과"
            unit = "g"
            amount = 100
            kcal = 52
            carbohydrate = 13.81
            protein = 0.26
            fat = 0.17
            salt = 1.0
            sugar = 10.8
         }else {
            if(binding.etName.text.toString() != "") {
               name = binding.etName.text.toString().trim()
            }
            if(binding.etUnit.text.toString() != "") {
               unit = binding.etUnit.text.toString().trim()
            }
            if(binding.etAmount.text.toString() != "") {
               amount = binding.etAmount.text.toString().trim().toInt()
            }
            if(binding.etKcal.text.toString() != "") {
               kcal = binding.etKcal.text.toString().trim().toInt()
            }
            if(binding.etCar.text.toString() != "") {
               carbohydrate = binding.etCar.text.toString().trim().toDouble()
            }
            if(binding.etProtein.text.toString() != "") {
               protein = binding.etProtein.text.toString().trim().toDouble()
            }
            if(binding.etFat.text.toString() != "") {
               fat = binding.etFat.text.toString().trim().toDouble()
            }
            if(binding.etSalt.text.toString() != "") {
               salt = binding.etSalt.text.toString().trim().toDouble()
            }
            if(binding.etSugar.text.toString() != "") {
               sugar = binding.etSugar.text.toString().trim().toDouble()
            }
         }

         val getFood = dataManager.getFood(name)

         if(getFood.name != "") {
            Toast.makeText(context, "같은 이름의 데이터가 존재합니다.", Toast.LENGTH_SHORT).show()
         }else {
            dataManager.insertFood(Food(type = type.toInt(), name = name, unit = unit, amount = amount, kcal = kcal, carbohydrate = carbohydrate,
               protein = protein, fat = fat, salt = salt, sugar = sugar, useCount = 1, useDate = LocalDateTime.now().toString()))

            dataManager.insertDailyFood(Food(type = type.toInt(), name = name, unit = unit, amount = amount, kcal = kcal,
               carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, count = 1, regDate = selectedDate.toString()))

            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()

            when(type) {
               "1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
               "2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
               "3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
               "4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
            }
         }
      }

      return binding.root
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
   }
}