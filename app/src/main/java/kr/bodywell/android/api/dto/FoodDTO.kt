package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class FoodDTO(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("quantity")
	var quantity: Int = 0,

	@SerializedName("quantityUnit")
	var quantityUnit: String = "",

	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("carbohydrate")
	var carbohydrate: Double = 0.0,

	@SerializedName("protein")
	var protein: Double = 0.0,

	@SerializedName("fat")
	var fat: Double = 0.0
)