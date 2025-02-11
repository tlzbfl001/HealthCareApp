package kr.bodywell.health.view.home.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.databinding.FragmentFoodInputBinding
import kr.bodywell.health.model.Constant.BREAKFAST
import kr.bodywell.health.model.Constant.FOODS
import kr.bodywell.health.model.Food
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.filterText
import kr.bodywell.health.util.CustomUtil.getUUID
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment4
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import java.util.Calendar

class FoodInputFragment : Fragment() {
   private var _binding: FragmentFoodInputBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bundle = Bundle()
   private var type = BREAKFAST
   private var unit = "mg"

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment4(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
         }
      }
      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentFoodInputBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      type = arguments?.getString("type")!!
      bundle.putString("type", type)

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.constraint.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment4(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
      }

      binding.tvMg.setOnClickListener {
         unit1()
      }

      binding.tvG.setOnClickListener {
         unit2()
      }

      binding.tvKg.setOnClickListener {
         unit3()
      }

      binding.tvMl.setOnClickListener {
         unit4()
      }

      binding.tvL.setOnClickListener {
         unit5()
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

      binding.cvSave.setOnClickListener {
         var name = ""
         var amount = 0
         var calorie = 0
         var carbohydrate = 0.0
         var protein = 0.0
         var fat = 0.0

         if(binding.etName.text.toString().trim() == "" && binding.etAmount.text.toString().trim() == "" && binding.etKcal.text.toString().trim() == "" &&
            binding.etCar.text.toString().trim() == "" && binding.etProtein.text.toString().trim() == "" && binding.etFat.text.toString().trim() == "") {
            name = "사과"
            amount = 100
            calorie = 52
            carbohydrate = 13.81
            protein = 0.26
            fat = 0.17
         }else {
            if(binding.etName.text.toString() != "") name = binding.etName.text.toString().trim()
            if(binding.etAmount.text.toString() != "") amount = binding.etAmount.text.toString().toInt()
            if(binding.etKcal.text.toString() != "") calorie = binding.etKcal.text.toString().toInt()
            if(binding.etCar.text.toString() != "") carbohydrate = binding.etCar.text.toString().toDouble()
            if(binding.etProtein.text.toString() != "") protein = binding.etProtein.text.toString().toDouble()
            if(binding.etFat.text.toString() != "") fat = binding.etFat.text.toString().toDouble()
         }

         lifecycleScope.launch {
            val getData = powerSync.getData(FOODS, "name", "name", name)

            if(name == "") {
               Toast.makeText(context, "음식이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!filterText(name)) {
               Toast.makeText(context, "특수문자는 입력 불가합니다.", Toast.LENGTH_SHORT).show()
            }else if(getData != "") {
               Toast.makeText(context, "음식이름이 중복됩니다.", Toast.LENGTH_SHORT).show()
            }else if(amount < 1 || calorie < 1) {
               Toast.makeText(context, "섭취량, 칼로리는 1이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
            }else if(carbohydrate < 0.1 || protein < 0.1 || fat < 0.1) {
               Toast.makeText(context, "영양성분은 0이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
            }else {
               powerSync.insertFood(Food(id = getUUID(), name = name, calorie = calorie, carbohydrate = carbohydrate, protein = protein, fat = fat,
                  volume = amount, volumeUnit = unit, createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance())))

               Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
               replaceFragment4(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
            }
         }
      }

      return binding.root
   }

   private fun unit1() {
      unit = "mg"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_purple)
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

   private fun unit2() {
      unit = "g"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tvG.setTextColor(Color.WHITE)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun unit3() {
      unit = "kg"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tvKg.setTextColor(Color.WHITE)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun unit4() {
      unit = "mL"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tvMl.setTextColor(Color.WHITE)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvL.setTextColor(Color.BLACK)
      binding.tvUnit.text = unit
   }

   private fun unit5() {
      unit = "L"
      binding.tvMg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMg.setTextColor(Color.BLACK)
      binding.tvG.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvG.setTextColor(Color.BLACK)
      binding.tvKg.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvKg.setTextColor(Color.BLACK)
      binding.tvMl.setBackgroundResource(R.drawable.rec_25_border_gray)
      binding.tvMl.setTextColor(Color.BLACK)
      binding.tvL.setBackgroundResource(R.drawable.rec_25_purple)
      binding.tvL.setTextColor(Color.WHITE)
      binding.tvUnit.text = unit
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}