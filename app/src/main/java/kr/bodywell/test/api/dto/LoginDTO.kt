package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class LoginDTO (
	@SerializedName("idToken")
	var idToken: String = ""
)

data class NaverLoginDTO (
	@SerializedName("accessToken")
	var accessToken: String = ""
)

data class KakaoLoginDTO (
	@SerializedName("accessToken")
	var accessToken: String = "",

	@SerializedName("idToken")
	var idToken: String = ""
)