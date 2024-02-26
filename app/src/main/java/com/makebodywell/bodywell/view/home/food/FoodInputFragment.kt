package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodInputBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodInputFragment : Fragment() {
   private var _binding: FragmentFoodInputBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

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

      val calendarDate = arguments?.getString("calendarDate").toString()
      val type = arguments?.getString("type").toString()
      bundle.putString("calendarDate", calendarDate)
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
         when(type) {
            "1" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
            "2" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
            "3" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
            "4" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
         }
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

         if(binding.etName.text.toString() == "" && binding.etUnit.text.toString() == "" && binding.etAmount.text.toString() == "" &&
            binding.etKcal.text.toString() == "" && binding.etCar.text.toString() == "" && binding.etProtein.text.toString() == "" &&
            binding.etFat.text.toString() == "" && binding.etSalt.text.toString() == "" && binding.etSugar.text.toString() == "") {
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
               name = binding.etName.text.toString()
            }
            if(binding.etUnit.text.toString() != "") {
               unit = binding.etUnit.text.toString()
            }
            if(binding.etAmount.text.toString() != "") {
               amount = binding.etAmount.text.toString().toInt()
            }
            if(binding.etKcal.text.toString() != "") {
               kcal = binding.etKcal.text.toString().toInt()
            }
            if(binding.etCar.text.toString() != "") {
               carbohydrate = binding.etCar.text.toString().toDouble()
            }
            if(binding.etProtein.text.toString() != "") {
               protein = binding.etProtein.text.toString().toDouble()
            }
            if(binding.etFat.text.toString() != "") {
               fat = binding.etFat.text.toString().toDouble()
            }
            if(binding.etSalt.text.toString() != "") {
               salt = binding.etSalt.text.toString().toDouble()
            }
            if(binding.etSugar.text.toString() != "") {
               sugar = binding.etSugar.text.toString().toDouble()
            }
         }

         when(type) {
            "1" -> {
               dataManager.insertFood(Food(name = name, unit = unit, amount = amount, count = 1, kcal = kcal,
                  carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, type = 1, regDate = calendarDate))
            }
            "2" -> {
               dataManager.insertFood(Food(name = name, unit = unit, amount = amount, count = 1, kcal = kcal,
                  carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, type = 2, regDate = calendarDate))
            }
            "3" -> {
               dataManager.insertFood(Food(name = name, unit = unit, amount = amount, count = 1, kcal = kcal,
                  carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, type = 3, regDate = calendarDate))
            }
            "4" -> {
               dataManager.insertFood(Food(name = name, unit = unit, amount = amount, count = 1, kcal = kcal,
                  carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, type = 4, regDate = calendarDate))
            }
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
      }

      return binding.root
   }
}