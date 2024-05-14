package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class ActivityResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = ""
)