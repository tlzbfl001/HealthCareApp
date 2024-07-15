package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("username")
	var username: String = "",

	@SerializedName("email")
	var email: String = ""
)