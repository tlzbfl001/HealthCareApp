package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class TokenResponse (
	@SerializedName("accessToken")
	var accessToken: String = "",

	@SerializedName("refreshToken")
	var refreshToken: String = "",
)