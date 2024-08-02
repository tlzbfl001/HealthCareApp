package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WorkoutDTO(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("calorie")
	var calories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("date")
	var date: String = ""
)

data class WorkoutUpdateDTO(
	@SerializedName("calories")
	var calories: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0
)