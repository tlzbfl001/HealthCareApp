package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class SleepDTO(
	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = ""
)