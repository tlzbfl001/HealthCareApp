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

   private var calendarDate = ""
   private var type = ""

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodInputBinding.inflate(layoutInflater)

      dataManager = DataManager(activity)
      dataManager!!.open()

      initView()

      return binding.root
   }

   private fun initView() {
      calendarDate = arguments?.getString("calendarDate").toString()
      type = arguments?.getString("type").toString()
      bundle.putString("calendarDate", calendarDate)
      bundle.putString("type", type.toString())

      binding.clOut.setOnClickListener {
         when(type) {
            "1" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "2" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "3" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "4" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }

      binding.tvSave.setOnClickListener {
         var amount = 1
         if(binding.etAmount.text.toString() != "") {
            amount = Integer.parseInt(binding.etAmount.text.toString())
         }

         when(type) {
            "1" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = 1, regDate = calendarDate))
            }
            "2" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = 2, regDate = calendarDate))
            }
            "3" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = 3, regDate = calendarDate))
            }
            "4" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = 4, regDate = calendarDate))
            }
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
      }
   }
}