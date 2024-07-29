package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class DietDTO(
	@SerializedName("mealTime")
	var mealTime: String = "",

	@SerializedName("brandName")
	var brandName: String = "",

	@SerializedName("foodName")
	var foodName: String = "",

	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("carbohydrate")
	var carbohydrate: Double = 0.0,

	@SerializedName("protein")
	var protein: Double = 0.0,

	@SerializedName("fat")
	var fat: Double = 0.0,

	@SerializedName("quantity")
	var quantity: Int = 0,

	@SerializedName("quantityUnit")
	var quantityUnit: String = "",

	@SerializedName("volume")
	var volume: Int = 0,

	@SerializedName("volumeUnit")
	var volumeUnit: String = "",

	@SerializedName("photos")
	var photos: ArrayList<String> = ArrayList(),

	@SerializedName("date")
	var date: String = "",

	@SerializedName("food")
	var food: FoodData
)

data class FoodData(
	@SerializedName("uid")
	var uid: String = ""
)

data class DietUpdateDTO(
	@SerializedName("mealTime")
	var mealTime: String = "",

	@SerializedName("brandName")
	var brandName: String = "",

	@SerializedName("foodName")
	var foodName: String = "",

	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("carbohydrate")
	var carbohydrate: Double = 0.0,

	@SerializedName("protein")
	var protein: Double = 0.0,

	@SerializedName("fat")
	var fat: Double = 0.0,

	@SerializedName("quantity")
	var quantity: Int = 0,

	@SerializedName("quantityUnit")
	var quantityUnit: String = "",

	@SerializedName("volume")
	var volume: Int = 0,

	@SerializedName("volumeUnit")
	var volumeUnit: String = "",

	@SerializedName("photos")
	var photos: ArrayList<String> = ArrayList(),

	@SerializedName("date")
	var date: String = ""
)