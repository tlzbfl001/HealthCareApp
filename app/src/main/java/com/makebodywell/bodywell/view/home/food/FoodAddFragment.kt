package com.makebodywell.bodywell.view.home.food

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodAddBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.util.CalendarUtil.Companion.selectedDate
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.hideKeyboard
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.home.MainActivity
import java.time.LocalDate
import java.time.LocalDateTime

class FoodAddFragment : Fragment(), MainActivity.OnBackPressedListener {
	private var _binding: FragmentFoodAddBinding? = null
	val binding get() = _binding!!

	private var bundle = Bundle()
	private var dataManager: DataManager? = null
	private var getFood = Food()
	private var type = "1"
	private var unit = "g"

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

		(context as MainActivity).setOnBackPressedListener(this)

		dataManager = DataManager(activity)
		dataManager!!.open()

		val id = if(arguments?.getString("id") == null) -1 else arguments?.getString("id").toString().toInt()
		type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		getFood = dataManager!!.getFood(id)

		unit = getFood.unit
		binding.tvName.text = getFood.name
		binding.tvAmount.text = getFood.amount.toString()
		binding.tvUnit.text = getFood.unit
		binding.etKcal.text = getFood.kcal.toString()
		binding.tvCar.text = String.format("%.1f", getFood.carbohydrate) + "g"
		binding.tvProtein.text = String.format("%.1f", getFood.protein) + "g"
		binding.tvFat.text = String.format("%.1f", getFood.fat) + "g"
		binding.tvSalt.text = String.format("%.1f", getFood.salt) + "g"
		binding.tvSugar.text = String.format("%.1f", getFood.sugar) + "g"

		binding.cl1.setOnTouchListener { view, motionEvent ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clBack.setOnClickListener {
			replaceFragment()
		}

		binding.cvSave.setOnClickListener {
			if(id > -1) {
				val getDailyFood = dataManager!!.getDailyFood(type = type.toInt(), name = getFood.name, selectedDate.toString())
				if(getDailyFood.regDate == "") {
					dataManager!!.insertDailyFood(Food(type = type.toInt(), name = getFood.name, unit = getFood.unit, amount = getFood.amount, kcal = getFood.kcal,
						carbohydrate = getFood.carbohydrate, protein = getFood.protein, fat = getFood.fat, salt = getFood.salt, sugar = getFood.sugar, count = 1,
						regDate = selectedDate.toString()))
				}else {
					dataManager!!.updateDailyFood(Food(id = getDailyFood.id, unit = getFood.unit, amount = getFood.amount, kcal = getFood.kcal, carbohydrate = getFood.carbohydrate,
						protein = getFood.protein, fat = getFood.fat, salt = getFood.salt, sugar = getFood.sugar, count = getDailyFood.count + 1))
				}

				Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()

				dataManager!!.updateInt(TABLE_FOOD, "useCount", getFood.useCount + 1, id)
				dataManager!!.updateStr(TABLE_FOOD, "useDate", LocalDateTime.now().toString(), id)

				when(type) {
					"1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
					"2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
					"3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
					"4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
				}
			}else {
				Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show()
			}
		}

		return binding.root
	}

	private fun replaceFragment() {
		when(arguments?.getString("back")) {
			"1" -> replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
			else -> replaceFragment2(requireActivity(), FoodRecord2Fragment(), bundle)
		}
	}

	override fun onBackPressed() {
		val activity = activity as MainActivity?
		activity!!.setOnBackPressedListener(null)
		replaceFragment()
	}
}