package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SleepResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = ""
)