package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.makebodywell.bodywell.R
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
   private var unit = "g"

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

      binding.tvMg.setOnClickListener {
         button1()
      }

      binding.tvG.setOnClickListener {
         button2()
      }

      binding.tvKg.setOnClickListener {
         button3()
      }

      binding.tvMl.setOnClickListener {
         button4()
      }

      binding.tvL.setOnClickListener {
         button5()
      }

      binding.etCar.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etCar.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etCar.removeTextChangedListener(this)
                  binding.etCar.setText(format)
                  binding.etCar.setSelection(format.length)
                  binding.etCar.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etCar.removeTextChangedListener(this)
                  binding.etCar.setText(format)
                  binding.etCar.setSelection(format.length)
                  binding.etCar.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etCar.removeTextChangedListener(this)
                  binding.etCar.setText(format)
                  binding.etCar.setSelection(format.length)
                  binding.etCar.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etProtein.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etProtein.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etProtein.removeTextChangedListener(this)
                  binding.etProtein.setText(format)
                  binding.etProtein.setSelection(format.length)
                  binding.etProtein.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etProtein.removeTextChangedListener(this)
                  binding.etProtein.setText(format)
                  binding.etProtein.setSelection(format.length)
                  binding.etProtein.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etProtein.removeTextChangedListener(this)
                  binding.etProtein.setText(format)
                  binding.etProtein.setSelection(format.length)
                  binding.etProtein.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etFat.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etFat.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etFat.removeTextChangedListener(this)
                  binding.etFat.setText(format)
                  binding.etFat.setSelection(format.length)
                  binding.etFat.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etFat.removeTextChangedListener(this)
                  binding.etFat.setText(format)
                  binding.etFat.setSelection(format.length)
                  binding.etFat.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etFat.removeTextChangedListener(this)
                  binding.etFat.setText(format)
                  binding.etFat.setSelection(format.length)
                  binding.etFat.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etSalt.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etSalt.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etSalt.removeTextChangedListener(this)
                  binding.etSalt.setText(format)
                  binding.etSalt.setSelection(format.length)
                  binding.etSalt.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etSalt.removeTextChangedListener(this)
                  binding.etSalt.setText(format)
                  binding.etSalt.setSelection(format.length)
                  binding.etSalt.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etSalt.removeTextChangedListener(this)
                  binding.etSalt.setText(format)
                  binding.etSalt.setSelection(format.length)
                  binding.etSalt.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.etSugar.addTextChangedListener(object : TextWatcher {
         override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if(s.toString() != "") {
               val text = s.toString().replace(".","")

               if(s.length == 1 && s[0].toString() == ".") {
                  binding.etSugar.setText("")
               }

               if(text.length == 2) {
                  val format = text[0].toString() + "." + text[1].toString()
                  binding.etSugar.removeTextChangedListener(this)
                  binding.etSugar.setText(format)
                  binding.etSugar.setSelection(format.length)
                  binding.etSugar.addTextChangedListener(this)
               }

               if(text.length == 3) {
                  val format = text[0].toString() + text[1].toString() + "." + text[2].toString()
                  binding.etSugar.removeTextChangedListener(this)
                  binding.etSugar.setText(format)
                  binding.etSugar.setSelection(format.length)
                  binding.etSugar.addTextChangedListener(this)
               }

               if(text.length == 4) {
                  val format = text[0].toString() + text[1].toString() + text[2].toString() + "." + text[3].toString()
                  binding.etSugar.removeTextChangedListener(this)
                  binding.etSugar.setText(format)
                  binding.etSugar.setSelection(format.length)
                  binding.etSugar.addTextChangedListener(this)
               }
            }
         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
         override fun afterTextChanged(p0: Editable?) {}
      })

      binding.cvSave.setOnClickListener {
         var name = ""
         var amount = 0
         var kcal = 0
         var carbohydrate = 0.0
         var protein = 0.0
         var fat = 0.0
         var salt = 0.0
         var sugar = 0.0

         if(binding.etName.text.toString().trim() == "" && binding.etAmount.text.toString().trim() == "" &&
            binding.etKcal.text.toString().trim() == "" && binding.etCar.text.toString().trim() == "" && binding.etProtein.text.toString().trim() == "" &&
            binding.etFat.text.toString().trim() == "" && binding.etSalt.text.toString().trim() == "" && binding.etSugar.text.toString().trim() == "") {
            name = "사과"
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

   private fun button1() {
      unit = "mg"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_pink)
      binding.tvMg.setTextColor(Color.WHITE)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun button2() {
      unit = "g"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_pink)
      binding.tvG.setTextColor(Color.WHITE)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun button3() {
      unit = "kg"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_pink)
      binding.tvKg.setTextColor(Color.WHITE)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun button4() {
      unit = "mL"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_pink)
      binding.tvMl.setTextColor(Color.WHITE)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun button5() {
      unit = "L"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_pink)
      binding.tvL.setTextColor(Color.WHITE)
      binding.tvUnit.text = unit
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
   }
}