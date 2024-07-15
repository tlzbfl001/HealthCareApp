package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class LoginDTO (
	@SerializedName("idToken")
	var idToken: String = ""
)