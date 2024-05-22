package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WorkoutDTO(
	@SerializedName("itemId")
	var itemId: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("date")
	var date: String = "",

	@SerializedName("isDaily")
	var isDaily: Boolean = false,

	@SerializedName("activity")
	var activity: Activity
)

data class WorkoutUpdateDTO(
	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0
)

data class Activity(
	@SerializedName("uid")
	var uid: String = ""
)