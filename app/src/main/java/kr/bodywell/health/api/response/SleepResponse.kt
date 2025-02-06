package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class SleepResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?
)