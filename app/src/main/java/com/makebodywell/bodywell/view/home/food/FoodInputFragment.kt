package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
   private var type = "1"
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

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_unit)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

      val tvMg = dialog.findViewById<TextView>(R.id.tvMg)
      val tvG = dialog.findViewById<TextView>(R.id.tvG)
      val tvKg = dialog.findViewById<TextView>(R.id.tvKg)
      val tvMl = dialog.findViewById<TextView>(R.id.tvMl)
      val tvL = dialog.findViewById<TextView>(R.id.tvL)

      tvMg!!.setOnClickListener {
         unit = "mg"
         tvMg.setBackgroundResource(R.drawable.rec_25_pink)
         tvMg.setTextColor(Color.WHITE)
         tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvG.setTextColor(Color.BLACK)
         tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvKg.setTextColor(Color.BLACK)
         tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMl.setTextColor(Color.BLACK)
         tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvL.setTextColor(Color.BLACK)
         setText()
      }

      tvG!!.setOnClickListener {
         unit = "g"
         tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMg.setTextColor(Color.BLACK)
         tvG.setBackgroundResource(R.drawable.rec_25_pink)
         tvG.setTextColor(Color.WHITE)
         tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvKg.setTextColor(Color.BLACK)
         tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMl.setTextColor(Color.BLACK)
         tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvL.setTextColor(Color.BLACK)
         setText()
      }

      tvKg!!.setOnClickListener {
         unit = "kg"
         tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMg.setTextColor(Color.BLACK)
         tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvG.setTextColor(Color.BLACK)
         tvKg.setBackgroundResource(R.drawable.rec_25_pink)
         tvKg.setTextColor(Color.WHITE)
         tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMl.setTextColor(Color.BLACK)
         tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvL.setTextColor(Color.BLACK)
         setText()
      }

      tvMl!!.setOnClickListener {
         unit = "mL"
         tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMg.setTextColor(Color.BLACK)
         tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvG.setTextColor(Color.BLACK)
         tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvKg.setTextColor(Color.BLACK)
         tvMl.setBackgroundResource(R.drawable.rec_25_pink)
         tvMl.setTextColor(Color.WHITE)
         tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvL.setTextColor(Color.BLACK)
         setText()
      }

      tvL!!.setOnClickListener {
         unit = "L"
         tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMg.setTextColor(Color.BLACK)
         tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvG.setTextColor(Color.BLACK)
         tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvKg.setTextColor(Color.BLACK)
         tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
         tvMl.setTextColor(Color.BLACK)
         tvL.setBackgroundResource(R.drawable.rec_25_pink)
         tvL.setTextColor(Color.WHITE)
         setText()
      }

      binding.clUnit.setOnClickListener {
         dialog.show()
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

         if(binding.etName.text.toString().trim() == "" && binding.etAmount.text.toString().trim() == "" && binding.etKcal.text.toString().trim() == "" &&
            binding.etCar.text.toString().trim() == "" && binding.etProtein.text.toString().trim() == "" && binding.etFat.text.toString().trim() == "" &&
            binding.etSalt.text.toString().trim() == "" && binding.etSugar.text.toString().trim() == "") {
            name = "사과"
            amount = 100
            kcal = 52
            carbohydrate = 13.81
            protein = 0.26
            fat = 0.17
            salt = 1.0
            sugar = 10.8
         }else {
            if(binding.etName.text.toString() != "") name = binding.etName.text.toString().trim()
            if(binding.etAmount.text.toString() != "") amount = binding.etAmount.text.toString().trim().toInt()
            if(binding.etKcal.text.toString() != "") kcal = binding.etKcal.text.toString().trim().toInt()
            if(binding.etCar.text.toString() != "") carbohydrate = binding.etCar.text.toString().trim().toDouble()
            if(binding.etProtein.text.toString() != "") protein = binding.etProtein.text.toString().trim().toDouble()
            if(binding.etFat.text.toString() != "") fat = binding.etFat.text.toString().trim().toDouble()
            if(binding.etSalt.text.toString() != "") salt = binding.etSalt.text.toString().trim().toDouble()
            if(binding.etSugar.text.toString() != "") sugar = binding.etSugar.text.toString().trim().toDouble()
         }

         val getFood = dataManager.getFood(name)

         if(name == "") {
            Toast.makeText(context, "음식이름 미입력", Toast.LENGTH_SHORT).show()
         }else if (getFood.name != "") {
            Toast.makeText(context, "같은 이름의 데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
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

   private fun setText() {
      binding.tvUnit1.text = unit
      binding.tvUnit2.text = unit
   }

   override fun onBackPressed() {
      val activity = activity as MainActivity?
      activity!!.setOnBackPressedListener(null)
      replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
   }
}