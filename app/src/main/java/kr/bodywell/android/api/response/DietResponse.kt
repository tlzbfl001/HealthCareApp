package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class DietResponse(
	@SerializedName("id")
	var id: String = "",

	@SerializedName("mealTime")
	var mealTime: String = "",

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

	@SerializedName("date")
	var date: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("foodId")
	var foodId: String = "",

	@SerializedName("userId")
	var userId: String = "",

	@SerializedName("photos")
	var photos: List<PhotoResponse> = listOf()
)