package kr.bodywell.android.api.dto

import com.google.gson.annotations.SerializedName

data class WaterDTO(
	@SerializedName("mL")
	var mL: Int = 0,

	@SerializedName("count")
	var count: Int = 0,

	@SerializedName("date")
	var date: String = ""
)