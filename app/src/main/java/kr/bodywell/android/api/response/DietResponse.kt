package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class DietsResponse(
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
	var calories: String = "",

	@SerializedName("softwareVersion")
	var softwareVersion: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)