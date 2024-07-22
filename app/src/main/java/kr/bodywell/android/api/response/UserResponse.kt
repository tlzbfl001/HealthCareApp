package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse (
	@SerializedName("exists")
	var exists: Boolean = false
)