package kr.bodywell.health.view.home.food

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.health.databinding.FragmentFoodAddBinding
import kr.bodywell.health.model.Constant.FOODS
import kr.bodywell.health.model.Food
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.dateTimeToIso
import kr.bodywell.health.util.CustomUtil.getUUID
import kr.bodywell.health.util.CustomUtil.hideKeyboard
import kr.bodywell.health.util.CustomUtil.replaceFragment4
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp.Companion.powerSync
import java.util.Calendar

class FoodAddFragment : Fragment() {
	private var _binding: FragmentFoodAddBinding? = null
	val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var bundle = Bundle()
	private var getFood = Food()
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
		_binding = FragmentFoodAddBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		val foodId = arguments?.getString("foodId")!!
		val type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		lifecycleScope.launch {
			getFood = powerSync.getFood(foodId)
		}

		unit = getFood.volumeUnit
		binding.tvName.text = getFood.name
		binding.tvAmount.text = getFood.volume.toString()
		binding.tvUnit.text = unit
		binding.etKcal.text = getFood.calorie.toString()
		binding.tvCar.text = String.format("%.1f", getFood.carbohydrate) + "g"
		binding.tvProtein.text = String.format("%.1f", getFood.protein) + "g"
		binding.tvFat.text = String.format("%.1f", getFood.fat) + "g"

		binding.cl1.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment4(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
		}

		binding.cvSave.setOnClickListener {
			lifecycleScope.launch {
				val getDiet = powerSync.getDiet(type, getFood.name, selectedDate.toString())

				if(getDiet.id == "") {
					val getData = powerSync.getData(FOODS, "id", "name", getFood.name)
					powerSync.insertDiet(Food(id = getUUID(), mealTime = type, name = getFood.name, calorie = getFood.calorie, carbohydrate = getFood.carbohydrate,
						protein = getFood.protein, fat = getFood.fat, volume = getFood.volume, volumeUnit = getFood.volumeUnit, date = selectedDate.toString(),
						createdAt = dateTimeToIso(Calendar.getInstance()), updatedAt = dateTimeToIso(Calendar.getInstance()), foodId = getData))
				}else {
					powerSync.updateDiet(Food(id = getDiet.id, quantity = getFood.quantity + 1))
				}
			}

			Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
			replaceFragment4(requireActivity().supportFragmentManager, FoodRecord1Fragment(), bundle)
		}

		return binding.root
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}