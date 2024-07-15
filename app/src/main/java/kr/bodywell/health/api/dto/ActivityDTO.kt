package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class ActivityDTO(
	@SerializedName("name")
	var name: String = ""
)