package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("uid")
	var uid: String = ""
)

data class CheckResponse (
	@SerializedName("exists")
	var exists: Boolean = false
)