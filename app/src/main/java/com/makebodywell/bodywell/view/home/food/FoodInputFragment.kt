package com.makebodywell.bodywell.view.home.food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodInputBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodInputFragment : Fragment() {
   private var _binding: FragmentFoodInputBinding? = null
   private val binding get() = _binding!!

   private var bundle = Bundle()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodInputBinding.inflate(layoutInflater)

      val dataManager = DataManager(activity)
      dataManager.open()

      val calendarDate = arguments?.getString("calendarDate").toString()
      val type = arguments?.getString("type").toString()
      bundle.putString("calendarDate", calendarDate)
      bundle.putString("type", type)

      binding.clBack.setOnClickListener {
         when(type) {
            "1" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "2" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "3" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "4" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }

      binding.cvSave.setOnClickListener {
         if(binding.etName.text.toString() == "" || binding.etUnit.text.toString() == "" || binding.etAmount.text.toString() == "" || binding.etKcal.text.toString() == "") {
            Toast.makeText(requireActivity(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            if(binding.etAmount.text.toString() == "") {
               binding.etAmount.setText("0")
            }
            if(binding.etKcal.text.toString() == "") {
               binding.etKcal.setText("0")
            }
            if(binding.etCar.text.toString() == "") {
               binding.etCar.setText("0.0")
            }
            if(binding.etProtein.text.toString() == "") {
               binding.etProtein.setText("0.0")
            }
            if(binding.etFat.text.toString() == "") {
               binding.etFat.setText("0.0")
            }
            if(binding.etSalt.text.toString() == "") {
               binding.etSalt.setText("0.0")
            }
            if(binding.etSugar.text.toString() == "") {
               binding.etSugar.setText("0.0")
            }

            when(type) {
               "1" -> {
                  dataManager.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                     amount = binding.etAmount.text.toString(), count = 1, kcal = binding.etKcal.text.toString(),
                     carbohydrate = binding.etCar.text.toString(), protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(),
                     salt = binding.etSalt.text.toString(), sugar = binding.etSugar.text.toString(), type = 1, regDate = calendarDate))
               }
               "2" -> {
                  dataManager.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                     amount = binding.etAmount.text.toString(), count = 1, kcal = binding.etKcal.text.toString(),
                     carbohydrate = binding.etCar.text.toString(), protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(),
                     salt = binding.etSalt.text.toString(), sugar = binding.etSugar.text.toString(), type = 2, regDate = calendarDate))
               }
               "3" -> {
                  dataManager.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                     amount = binding.etAmount.text.toString(), count = 1, kcal = binding.etKcal.text.toString(),
                     carbohydrate = binding.etCar.text.toString(), protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(),
                     salt = binding.etSalt.text.toString(), sugar = binding.etSugar.text.toString(), type = 3, regDate = calendarDate))
               }
               "4" -> {
                  dataManager.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                     amount = binding.etAmount.text.toString(), count = 1, kcal = binding.etKcal.text.toString(),
                     carbohydrate = binding.etCar.text.toString(), protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(),
                     salt = binding.etSalt.text.toString(), sugar = binding.etSugar.text.toString(), type = 4, regDate = calendarDate))
               }
            }

            Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
         }
      }

      return binding.root
   }
}