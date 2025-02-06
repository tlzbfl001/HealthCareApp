package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class WaterDTO(
	@SerializedName("mL")
	var mL: Int = 0,

	@SerializedName("count")
	var count: Int = 0,

	@SerializedName("date")
	var date: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class WaterUpdateDTO(
	@SerializedName("mL")
	var mL: Int = 0,

	@SerializedName("count")
	var count: Int = 0
)