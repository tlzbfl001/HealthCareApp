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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentFoodEditBinding
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment2
import kr.bodywell.android.util.CustomUtil.setStatusBar

class FoodEditFragment : Fragment() {
	private var _binding: FragmentFoodEditBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var bundle = Bundle()
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

		setStatusBar(requireActivity(), binding.mainLayout)

		val food = arguments?.getParcelable<Food>("food")!!
		val type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		binding.tvName.text = food.name
		binding.etVolume.setText(food.volume.toString())
		binding.etKcal.setText(food.calorie.toString())
		binding.etCar.setText(food.carbohydrate.toString())
		binding.etProtein.setText(food.protein.toString())
		binding.etFat.setText(food.fat.toString())

		when(food.volumeUnit) {
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

		binding.cvEdit.setOnClickListener {
			lifecycleScope.launch {
				powerSync.updateFood(Food(id = food.id, calorie = binding.etKcal.text.toString().trim().toInt(),
					carbohydrate = binding.etCar.text.toString().trim().toDouble(), protein = binding.etProtein.text.toString().trim().toDouble(),
					fat = binding.etFat.text.toString().trim().toDouble(), volume = binding.etVolume.text.toString().trim().toInt(), volumeUnit = unit))
			}

			Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
			replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
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