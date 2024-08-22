package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WorkoutDTO(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("calorie")
	var calorie: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("date")
	var date: String = "",

	@SerializedName("activity")
	var activity: ActivityData?
)

data class ActivityData(
	@SerializedName("uid")
	var uid: String = ""
)

data class WorkoutUpdateDTO(
	@SerializedName("calorie")
	var calorie: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0
)