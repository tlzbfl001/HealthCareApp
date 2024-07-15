package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("birth")
	var birth: String? = "",

	@SerializedName("gender")
	var gender: String? = "",

	@SerializedName("height")
	var height: Int? = 0,

	@SerializedName("weight")
	var weight: Int? = 0,

	@SerializedName("timezone")
	var timezone: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class SyncProfileResponse (
	@SerializedName("data")
	var data: ProfileResponse?,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)