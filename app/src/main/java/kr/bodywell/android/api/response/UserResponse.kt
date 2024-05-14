package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("users")
	var users: List<Users>
)

data class Users (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("username")
	var username: String = "",

	@SerializedName("email")
	var email: String = ""
)