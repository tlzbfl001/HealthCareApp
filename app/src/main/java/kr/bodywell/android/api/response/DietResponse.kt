package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class DietResponse(
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("itemId")
	var itemId: String = "",

	@SerializedName("mealTime")
	var mealTime: String = "",

	@SerializedName("brandName")
	var brandName: String = "",

	@SerializedName("foodName")
	var foodName: String = "",

	@SerializedName("calories")
	var calories: Double = 0.0,

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
	var photos: ArrayList<String> = ArrayList()
)