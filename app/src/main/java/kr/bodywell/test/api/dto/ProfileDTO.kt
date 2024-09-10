package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class ProfileDTO (
	@SerializedName("name")
	var name: String = "",

	@SerializedName("birth")
	var birth: String = "",

	@SerializedName("gender")
	var gender: String = "",

	@SerializedName("height")
	var height: Double = 0.0,

	@SerializedName("weight")
	var weight: Double = 0.0,

	@SerializedName("timezone")
	var timezone: String = ""
)