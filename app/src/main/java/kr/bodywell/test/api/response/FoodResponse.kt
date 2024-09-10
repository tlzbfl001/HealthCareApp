package kr.bodywell.test.api.response

import com.google.gson.annotations.SerializedName

data class FoodResponse (
	@SerializedName("id")
	var id: String = "",

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
	var volumeUnit: String = "",

	@SerializedName("registerType")
	var registerType: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?,

	@SerializedName("deletedAt")
	var deletedAt: String?,

	@SerializedName("usages")
	var usages: ArrayList<Usages>?
)

data class Usages (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("usageCount")
	var usageCount: Int = 0,

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)