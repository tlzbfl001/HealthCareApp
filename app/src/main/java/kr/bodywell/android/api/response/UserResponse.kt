package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("id")
	var id: String = ""
)

data class ProfileResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("name")
	var name: String? = "",

	@SerializedName("pictureUrl")
	var pictureUrl: String?,

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
	var updatedAt: String = "",

	@SerializedName("userId")
	var userId: String = ""
)

data class ExistResponse (
	@SerializedName("exists")
	var exists: Boolean = false
)