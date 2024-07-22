package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SyncWaterResponse(
	@SerializedName("data")
	var data: List<WaterResponse>
)