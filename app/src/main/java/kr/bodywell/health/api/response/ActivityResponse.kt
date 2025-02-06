package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class ActivityResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("registerType")
	var registerType: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?,

	@SerializedName("usages")
	var usages: ArrayList<ActivityUsages>?
)

data class ActivityUsages (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("usageCount")
	var usageCount: Int = 0,

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)