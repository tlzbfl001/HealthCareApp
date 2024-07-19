package kr.bodywell.test.api.response

import com.google.gson.annotations.SerializedName

data class SleepResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class SleepResponses (
	@SerializedName("sleeps")
	var sleeps: List<SleepResponse>
)