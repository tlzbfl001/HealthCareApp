package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class ActivityResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("usageDate")
	var usageDate: String = "",

	@SerializedName("usageCount")
	var usageCount: Int = 0
)

data class ActivitiesResponse (
	@SerializedName("activities")
	var activities: List<ActivityResponse>
)