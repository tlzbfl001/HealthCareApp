package com.makebodywell.bodywell.view.home.food

<<<<<<< HEAD
=======
import android.content.Context
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
<<<<<<< HEAD
=======
import androidx.activity.OnBackPressedCallback
>>>>>>> 3efab1c7d38269b4ee96ffb382a8145466a19130
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
      bundle.putString("type", type)

      binding.clOut.setOnClickListener {
         when(type) {
            "breakfast" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
            "lunch" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
            "dinner" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
            "snack" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
         }
      }

      binding.tvSave.setOnClickListener {
         var amount = 1
         if(binding.etAmount.text.toString() != "") {
            amount = Integer.parseInt(binding.etAmount.text.toString())
         }

         when(type) {
            "breakfast" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = "breakfast", regDate = calendarDate))
            }
            "lunch" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = "lunch", regDate = calendarDate))
            }
            "dinner" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = "dinner", regDate = calendarDate))
            }
            "snack" -> {
               dataManager!!.insertFood(Food(name = binding.etName.text.toString(), unit = binding.etUnit.text.toString(),
                  amount = amount, kcal = binding.etKcal.text.toString(), carbohydrate = binding.etCal.text.toString(),
                  protein = binding.etProtein.text.toString(), fat = binding.etFat.text.toString(), salt = binding.etSalt.text.toString(),
                  sugar = binding.etSugar.text.toString(), type = "snack", regDate = calendarDate))
            }
         }

         Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
         replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
      }
   }
}