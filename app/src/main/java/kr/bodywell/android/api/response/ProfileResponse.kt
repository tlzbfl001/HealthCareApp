package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("birth")
	var birth: String?,

	@SerializedName("gender")
	var gender: String?,

	@SerializedName("height")
	var height: Double?,

	@SerializedName("weight")
	var weight: Double?,

	@SerializedName("timezone")
	var timezone: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)