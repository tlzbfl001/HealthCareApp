package kr.bodywell.android.view.home.food

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.github.f4b6a3.uuid.UuidCreator
import kotlinx.coroutines.launch
import kr.bodywell.android.databinding.FragmentFoodAddBinding
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateToIso
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.setStatusBar
import java.time.LocalDateTime
import java.util.UUID

class FoodAddFragment : Fragment() {
	private var _binding: FragmentFoodAddBinding? = null
	val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private var bundle = Bundle()
	private val uuid: UUID = UuidCreator.getTimeOrderedEpoch()
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
		_binding = FragmentFoodAddBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		val food = arguments?.getParcelable<Food>("food")!!
		val type = arguments?.getString("type").toString()
		bundle.putString("type", type)

		unit = food.volumeUnit
		binding.tvName.text = food.name
		binding.tvAmount.text = food.volume.toString()
		binding.tvUnit.text = unit
		binding.etKcal.text = food.calorie.toString()
		binding.tvCar.text = String.format("%.1f", food.carbohydrate) + "g"
		binding.tvProtein.text = String.format("%.1f", food.protein) + "g"
		binding.tvFat.text = String.format("%.1f", food.fat) + "g"

		binding.cl1.setOnTouchListener { _, _ ->
			hideKeyboard(requireActivity())
			true
		}

		binding.clX.setOnClickListener {
			replaceFragment()
		}

		binding.cvSave.setOnClickListener {
			lifecycleScope.launch {
				val getDiet = powerSync.getDiet(food.name, selectedDate.toString())

				if(getDiet.id == "") {
					val getData = powerSync.getData("foods", "id", "name", food.name)
					val dateToIso = dateToIso(selectedDate)
					powerSync.insertDiet(Food(id = uuid.toString(), mealTime = type, name = food.name, calorie = food.calorie, carbohydrate = food.carbohydrate,
						protein = food.protein, fat = food.fat, volume = food.volume, volumeUnit = food.volumeUnit, date = dateToIso,
						createdAt = LocalDateTime.now().toString(), updatedAt = LocalDateTime.now().toString(), foodId = getData))
				}else {
					powerSync.updateDiet(Food(id = getDiet.id, quantity = food.quantity + 1))
				}
			}

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