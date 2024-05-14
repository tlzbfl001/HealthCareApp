package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WorkoutDTO(
	@SerializedName("kilocalories")
	var kilocalories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = "",

	@SerializedName("isDaily")
	var isDaily: Boolean = false,

	@SerializedName("activity")
	var activity: Activity
)

data class WorkoutUpdateDTO(
	@SerializedName("kilocalories")
	var kilocalories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0
)

data class Activity(
	@SerializedName("uid")
	var uid: String = ""
)