package kr.bodywell.android.view.home.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentFoodEditBinding
import kr.bodywell.android.model.Constant.FOODS
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.setStatusBar

class FoodEditFragment : Fragment() {
	private var _binding: FragmentFoodEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var bundle = Bundle()
	private var getFood = Food()
	private var unit = "mg"

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment2(parentFragmentManager, FoodRecord1Fragment(), bundle)
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFoodEditBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		val foodId = arguments?.getString("foodId")!!
		val type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		lifecycleScope.launch {
			getFood = powerSync.getFood(foodId)
		}

		binding.tvName.text = getFood.name
		binding.etVolume.setText(getFood.volume.toString())
		binding.etKcal.setText(getFood.calorie.toString())
		binding.etCar.setText(getFood.carbohydrate.toString())
		binding.etProtein.setText(getFood.protein.toString())
		binding.etFat.setText(getFood.fat.toString())

		when(getFood.volumeUnit) {
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
			replaceFragment2(parentFragmentManager, FoodRecord1Fragment(), bundle)
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

		binding.cvEdit.setOnClickListener {
			lifecycleScope.launch {
				if(binding.etVolume.text.toString() == "" || binding.etVolume.text.toString().toDouble() < 1 || binding.etKcal.text.toString() == "" || binding.etKcal.text.toString().toDouble() < 1) {
					Toast.makeText(context, "섭취량, 칼로리는 1이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
				}else if(binding.etCar.text.toString() == "" || binding.etCar.text.toString().toDouble() < 0.1 || binding.etProtein.text.toString() == "" ||
					binding.etProtein.text.toString().toDouble() < 0.1 || binding.etFat.text.toString() == ""  || binding.etFat.text.toString().toDouble() < 0.1) {
					Toast.makeText(context, "영양성분은 0이상 입력해야합니다.", Toast.LENGTH_SHORT).show()
				}else {
					powerSync.updateFood(Food(id = getFood.id, calorie = binding.etKcal.text.toString().trim().toInt(), carbohydrate = binding.etCar.text.toString().trim().toDouble(),
						protein = binding.etProtein.text.toString().trim().toDouble(), fat = binding.etFat.text.toString().trim().toDouble(),
						volume = binding.etVolume.text.toString().trim().toInt(), volumeUnit = unit))

					Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
					replaceFragment2(parentFragmentManager, FoodRecord1Fragment(), bundle)
				}
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

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}