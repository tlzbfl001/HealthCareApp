package com.makebodywell.bodywell.view.home.food

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentFoodSearchBinding
import com.makebodywell.bodywell.model.Food
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CalendarUtil
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import java.time.LocalDate

class FoodSearchFragment : Fragment() {
	private var _binding: FragmentFoodSearchBinding? = null
	val binding get() = _binding!!

	private var bundle = Bundle()
	private var dataManager: DataManager? = null
	private var getFood = Food()
	private var imageList = ArrayList<Image>()
	private var type = "1"
	private var dataId = -1

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentFoodSearchBinding.inflate(layoutInflater)

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

		if(getFood.name != "") binding.tvName.text = getFood.name
		if(getFood.amount > 0) binding.tvAmount.text = getFood.kcal.toString()
		if(getFood.carbohydrate > 0.0) binding.tvCar.text = String.format("%.1f", getFood.carbohydrate)
		if(getFood.protein > 0.0) binding.tvProtein.text = String.format("%.1f", getFood.protein)
		if(getFood.fat > 0.0) binding.tvFat.text = String.format("%.1f", getFood.fat)
		if(getFood.salt > 0.0) binding.tvSalt.text = String.format("%.1f", getFood.salt)
		if(getFood.sugar > 0.0) binding.tvSugar.text = String.format("%.1f", getFood.sugar)

		binding.clBack.setOnClickListener {
			replaceFragment2(requireActivity(), FoodRecord1Fragment(), bundle)
		}

		binding.cvSave.setOnClickListener {
			dataManager!!.deleteItem(DBHelper.TABLE_IMAGE, "dataId", dataId)

			for(i in 0 until imageList.size) {
				dataManager!!.insertImage(imageList[i])
			}

			val getDailyFood = dataManager!!.getDailyFood(type = type.toInt(), name = getFood.name, CalendarUtil.selectedDate.toString())
			if(getDailyFood.regDate == "") {
				dataManager!!.insertDailyFood(Food(type = type.toInt(), name = getFood.name, unit = getFood.unit, amount = getFood.amount,
					kcal = getFood.kcal, carbohydrate = getFood.carbohydrate, protein = getFood.protein, fat = getFood.fat, salt = getFood.salt,
					sugar = getFood.sugar, count = 1, regDate = LocalDate.now().toString()))
			}else {
				dataManager!!.updateInt(DBHelper.TABLE_DAILY_FOOD, "count", getDailyFood.count + 1, getDailyFood.id)
			}

			val getSearch = dataManager!!.getSearch("food", getFood.name)
			dataManager!!.updateInt(DBHelper.TABLE_SEARCH, "count", getSearch.count + 1, getSearch.id)

			when(type) {
				"1" -> replaceFragment1(requireActivity(), FoodBreakfastFragment())
				"2" -> replaceFragment1(requireActivity(), FoodLunchFragment())
				"3" -> replaceFragment1(requireActivity(), FoodDinnerFragment())
				"4" -> replaceFragment1(requireActivity(), FoodSnackFragment())
			}

			Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
		}

		val getData = dataManager!!.getImage(dataId)
		for(i in 0 until getData.size) {
			imageList.add(getData[i])
		}

		return binding.root
	}
}