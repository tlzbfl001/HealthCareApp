package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodAddBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.CalendarDialog
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDateTime

class FoodAddFragment : Fragment(), MainActivity.OnBackPressedListener {
	private var _binding: FragmentFoodAddBinding? = null
	val binding get() = _binding!!

	private var bundle = Bundle()
	private var dataManager: DataManager? = null
	private var getFood = Food()
	private var type = "1"
	private var dataId = -1
	private var unit = "g"

	override fun onAttach(context: Context) {
		super.onAttach(context)
		(context as MainActivity).setOnBackPressedListener(this)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFoodAddBinding.inflate(layoutInflater)

		requireActivity().window?.apply {
			decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			statusBarColor = Color.TRANSPARENT
			navigationBarColor = Color.BLACK

			val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
			val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
			binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
		}

		dataManager = DataManager(activity)
		dataManager!!.open()

		type = arguments?.getString("type").toString()
		dataId = arguments?.getString("dataId").toString().toInt()
		bundle.putString("type", type)

		getFood = dataManager!!.getFood(dataId)

		unit = getFood.unit
		binding.tvName.text = getFood.name
		binding.etAmount.setText(getFood.amount.toString())
		binding.etKcal.setText(getFood.kcal.toString())
		binding.etCar.setText(String.format("%.1f", getFood.carbohydrate))
		binding.etProtein.setText(String.format("%.1f", getFood.protein))
		binding.etFat.setText(String.format("%.1f", getFood.fat))
		binding.etSalt.setText(String.format("%.1f", getFood.salt))
		binding.etSugar.setText(String.format("%.1f", getFood.sugar))

		binding.clBack.setOnClickListener {
			replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
		}

		binding.cl1.setOnTouchListener { view, motionEvent ->
			hideKeyboard(requireActivity())
			true
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
			binding.tvUnit2.text = unit
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
			binding.tvUnit2.text = unit
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
			binding.tvUnit2.text = unit
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
			binding.tvUnit2.text = unit
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
			binding.tvUnit2.text = unit
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
			val amount = if(binding.etAmount.text.toString() == "") 0 else binding.etAmount.text.toString().toInt()
			val kcal = if(binding.etKcal.text.toString() == "") 0 else binding.etKcal.text.toString().toInt()
			val carbohydrate = if(binding.etCar.text.toString() == "") 0.0 else binding.etCar.text.toString().toDouble()
			val protein = if(binding.etProtein.text.toString() == "") 0.0 else binding.etProtein.text.toString().toDouble()
			val fat = if(binding.etFat.text.toString() == "") 0.0 else binding.etFat.text.toString().toDouble()
			val salt = if(binding.etSalt.text.toString() == "") 0.0 else binding.etSalt.text.toString().toDouble()
			val sugar = if(binding.etSugar.text.toString() == "") 0.0 else binding.etSugar.text.toString().toDouble()

			val getDailyFood = dataManager!!.getDailyFood(type = type.toInt(), name = getFood.name, selectedDate.toString())
			if(getDailyFood.regDate == "") {
				dataManager!!.insertDailyFood(Food(type = type.toInt(), name = getFood.name, unit = unit, amount = amount, kcal = kcal,
					carbohydrate = carbohydrate, protein = protein, fat = fat, salt = salt, sugar = sugar, count = 1, regDate = selectedDate.toString()))
			}else {
				dataManager!!.updateFoodDaily(Food(id = getDailyFood.id, unit = unit, amount = amount, kcal = kcal, carbohydrate = carbohydrate, protein = protein, fat = fat,
					salt = salt, sugar = sugar, count = getDailyFood.count + 1))
			}

			dataManager!!.updateFood(Food(id = dataId, unit = unit, amount = amount, kcal = kcal, carbohydrate = carbohydrate, protein = protein, fat = fat,
				salt = salt, sugar = sugar, useCount = getFood.useCount + 1, useDate = LocalDateTime.now().toString()))

			Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()

			when(type) {
				"1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
				"2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
				"3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
				"4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
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