package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class ActivityDTO(
	@SerializedName("id")
	var id: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)