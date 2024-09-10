package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class FoodDTO(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("calorie")
	var calorie: Int = 0,

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
	var volumeUnit: String = ""
)

data class FoodUpdateDTO(
	@SerializedName("calorie")
	var calorie: Int = 0,

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
	var volumeUnit: String = ""
)