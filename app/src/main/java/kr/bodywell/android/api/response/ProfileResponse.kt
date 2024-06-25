package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SyncProfileResponse (
	@SerializedName("data")
	var data: ProfileResponse
)

data class ProfileResponse (
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
	var height: Int = 0,

	@SerializedName("weight")
	var weight: Int = 0,

	@SerializedName("timezone")
	var timezone: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)