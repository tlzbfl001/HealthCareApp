package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class WorkoutResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("kilocalories")
	var kilocalories: String = "",

	@SerializedName("intensity")
	var intensity: String = "",

	@SerializedName("time")
	var time: Int = 0,

	@SerializedName("starts")
	var starts: String = ""
)