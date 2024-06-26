package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class ProfileDTO (
	@SerializedName("data")
	var data: ProfileData
)

data class ProfileData (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("picture")
	var picture: String = "",

	@SerializedName("birth")
	var birth: String = "",

	@SerializedName("gender")
	var gender: String = "",

	@SerializedName("height")
	var height: Double = 0.0,

	@SerializedName("weight")
	var weight: Double = 0.0,

	@SerializedName("timezone")
	var timezone: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)