package kr.bodywell.health.api.dto

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

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("activityId")
	var activityId: String? = ""
)

data class WorkoutUpdateDTO(
	@SerializedName("calorie")
	var calorie: Int = 0,

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0
)