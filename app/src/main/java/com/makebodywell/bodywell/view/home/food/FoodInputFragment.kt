package com.makebodywell.bodywell.view.home.food

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodInputBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2

class FoodInputFragment : Fragment() {
   private var _binding: FragmentFoodInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback

   private var bundle = Bundle()
   private var calendarDate = ""
   private var timezone = ""

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
      timezone = arguments?.getString("timezone").toString()
      bundle.putString("calendarDate", calendarDate)
      bundle.putString("timezone", timezone)

      binding.clOut.setOnClickListener {
         when(timezone) {
            "아침" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "점심" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "저녁" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "간식" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }

      binding.tvSave.setOnClickListener {
         var amount = 1
         if(binding.etAmount.text.toString() != "") {
            amount = Integer.parseInt(binding.etAmount.text.toString())
         }

         when(timezone) {
            "아침" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), timezone = "아침", regDate = calendarDate))
            }
            "점심" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), timezone = "점심", regDate = calendarDate))
            }
            "저녁" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), timezone = "저녁", regDate = calendarDate))
            }
            "간식" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), timezone = "간식", regDate = calendarDate))
            }
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
      }
   }

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            when(timezone) {
               "아침" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
               "점심" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
               "저녁" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
               "간식" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
            }
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}