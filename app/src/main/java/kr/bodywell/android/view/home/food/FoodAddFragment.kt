package kr.bodywell.android.view.home.food

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentFoodAddBinding
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment4
import java.time.LocalDateTime

class FoodAddFragment : Fragment() {
	private var _binding: FragmentFoodAddBinding? = null
	val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private var bundle = Bundle()
	private var getFood = Food()
	private var type = "BREAKFAST"
	private var unit = "g"

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
		dataManager.open()

		val id = arguments?.getString("id").toString().toInt()
		type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		getFood = dataManager.getFood("id", id)

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

		binding.cl1.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment()
		}

		binding.cvSave.setOnClickListener {
			val getDailyFood = dataManager.getDailyFood(type = type, name = getFood.name, selectedDate.toString())

			if(getDailyFood.createdAt == "") {
				dataManager.insertDailyFood(Food(type = type, name = getFood.name, unit = getFood.unit, amount = getFood.amount, kcal = getFood.kcal,
					carbohydrate = getFood.carbohydrate, protein = getFood.protein, fat = getFood.fat, salt = getFood.salt, sugar = getFood.sugar, count = 1,
					createdAt = selectedDate.toString()))
			}else {
				dataManager.updateDailyFood(Food(id = getDailyFood.id, unit = getFood.unit, amount = getFood.amount, kcal = getFood.kcal, carbohydrate = getFood.carbohydrate,
					protein = getFood.protein, fat = getFood.fat, salt = getFood.salt, sugar = getFood.sugar, count = getDailyFood.count + 1))
			}

			dataManager.updateInt(FOOD, "useCount", getFood.useCount + 1, "id", id)
			dataManager.updateStr(FOOD, "useDate", LocalDateTime.now().toString(), "id", id)

			Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
			replaceFragment4(requireActivity(), FoodDetailFragment(), bundle)
		}

		return binding.root
	}

	private fun replaceFragment() {
		when(arguments?.getString("back")) {
			"1" -> replaceFragment4(requireActivity(), FoodRecord1Fragment(), bundle)
			else -> replaceFragment4(requireActivity(), FoodRecord2Fragment(), bundle)
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}