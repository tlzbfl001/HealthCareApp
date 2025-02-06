package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class SyncDTO(
	@SerializedName("syncedAt")
	var syncedAt: String = ""
)