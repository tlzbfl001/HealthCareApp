package kr.bodywell.android.view.home.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodEditBinding
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil.Companion.filterText
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment2

class FoodEditFragment : Fragment() {
	private var _binding: FragmentFoodEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var bundle = Bundle()
	private var type = "1"
	private var unit = "mg"

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment()
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFoodEditBinding.inflate(layoutInflater)

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

		val id = if(arguments?.getString("id") == null) -1 else arguments?.getString("id").toString().toInt()
		type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		val getFood = dataManager.getFood(id)

		binding.etName.setText(getFood.name)
		binding.etAmount.setText(getFood.amount.toString())
		binding.etKcal.setText(getFood.kcal.toString())
		binding.etCar.setText(getFood.carbohydrate.toString())
		binding.etProtein.setText(getFood.protein.toString())
		binding.etFat.setText(getFood.fat.toString())
		binding.etSalt.setText(getFood.salt.toString())
		binding.etSugar.setText(getFood.sugar.toString())

		when(getFood.unit) {
			"mg" -> unit1()
			"g" -> unit2()
			"kg" -> unit3()
			"mL" -> unit4()
			"L" -> unit5()
		}

		binding.mainLayout.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.constraint.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clBack.setOnClickListener {
			replaceFragment()
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

					if(text.length == 5) {
						val format = text[0].toString() + text[1].toString() + text[2].toString() + text[3].toString() + "." + text[4].toString()
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

		binding.cvEdit.setOnClickListener {
			if(id > -1) {
				val getData = dataManager.getFood(binding.etName.text.toString().trim())

				if(binding.etName.text.length < 2) {
					Toast.makeText(context, "음식이름은 최소 2자 ~ 최대 15자 이내로 입력하여야합니다.", Toast.LENGTH_SHORT).show()
				}else if(!filterText(binding.etName.text.toString())) {
					Toast.makeText(context, "특수문자는 입력 불가능합니다.", Toast.LENGTH_SHORT).show()
				}else if (getData.name != "" && (getData.name != getFood.name)) {
					Toast.makeText(context, "같은 이름의 데이터가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
				}else {
					dataManager.updateFood(Food(id = id, name = binding.etName.text.toString().trim(), unit = unit, amount = binding.etAmount.text.toString().trim().toInt(),
						kcal = binding.etKcal.text.toString().trim().toInt(), carbohydrate = binding.etCar.text.toString().trim().toDouble(),
						protein = binding.etProtein.text.toString().trim().toDouble(), fat = binding.etFat.text.toString().trim().toDouble(),
						salt = binding.etSalt.text.toString().trim().toDouble(), sugar = binding.etSugar.text.toString().trim().toDouble()))

					Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
					replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
				}
			}else {
				Toast.makeText(context, "수정 실패", Toast.LENGTH_SHORT).show()
			}
		}

		return binding.root
	}

	private fun unit1() {
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

	private fun unit2() {
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

	private fun unit3() {
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

	private fun unit4() {
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
		binding.tvL.setBackgroundResource(R.drawable.rec_25_pink)
		binding.tvL.setTextColor(Color.WHITE)
		binding.tvUnit.text = unit
	}

	private fun replaceFragment() {
		when(arguments?.getString("back")) {
			"1" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
			else -> replaceFragment2(requireActivity(), FoodRecord2Fragment(), bundle)
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}