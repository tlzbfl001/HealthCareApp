package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class SleepDTO(
	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class SleepUpdateDTO(
	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = ""
)