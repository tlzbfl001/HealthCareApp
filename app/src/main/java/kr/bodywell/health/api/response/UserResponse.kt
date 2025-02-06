package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("username")
	var username: String = "",

	@SerializedName("email")
	var email: String = "",

	@SerializedName("role")
	var role: String = ""
)

data class ProfileResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("name")
	var name: String? = "",

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

	@SerializedName("pictureId")
	var pictureId: String? = ""
)