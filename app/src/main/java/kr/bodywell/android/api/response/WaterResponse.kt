package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class WaterResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("mL")
	var mL: Int = 0,

	@SerializedName("count")
	var count: Int = 0,

	@SerializedName("date")
	var date: String = ""
)

data class WaterResponses (
	@SerializedName("waters")
	var waters: List<WaterResponse>
)