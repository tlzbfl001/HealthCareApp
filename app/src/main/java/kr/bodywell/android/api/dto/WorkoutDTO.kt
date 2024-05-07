package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WorkoutDTO(
	@SerializedName("activityType")
	var activityType: String = "",

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("starts")
	var starts: String = ""
)