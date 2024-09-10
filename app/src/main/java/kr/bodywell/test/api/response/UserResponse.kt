package kr.bodywell.test.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("id")
	var id: String = ""
)

data class CheckResponse (
	@SerializedName("exists")
	var exists: Boolean = false
)