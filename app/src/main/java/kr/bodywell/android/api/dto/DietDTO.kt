package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class DietDTO(
	@SerializedName("mealTime")
	var mealTime: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("calorie")
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
	var quantityUnit: String = "0",

	@SerializedName("volume")
	var volume: Int = 0,

	@SerializedName("volumeUnit")
	var volumeUnit: String = "",

	@SerializedName("date")
	var date: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("foodId")
	var foodId: String?
)

data class DietUpdateDTO(
	@SerializedName("quantity")
	var quantity: Int = 1
)