package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class ActivityResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("registerType")
	var registerType: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("usageDate")
	var usageDate: String? = "",

	@SerializedName("usageCount")
	var usageCount: Int = 0
)

data class ActivityResponses (
	@SerializedName("activities")
	var activities: List<ActivityResponse>
)