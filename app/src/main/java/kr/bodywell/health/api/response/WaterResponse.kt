package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class WaterResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("mL")
	var mL: Int = 0,

	@SerializedName("count")
	var count: Int = 0,

	@SerializedName("date")
	var date: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?
)