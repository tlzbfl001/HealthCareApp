package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class FoodResponse (
	@SerializedName("uid")
	var uid: String = "",

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

	@SerializedName("registerType")
	var registerType: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("foodUsages")
	var foodUsages: ArrayList<FoodUsages>
)

data class FoodUsages (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("usageCount")
	var usageCount: Int = 0,

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)