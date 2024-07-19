package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class SyncDTO(
	@SerializedName("data")
	var data: SyncData,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncData(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("registerType")
	var registerType: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class SyncUpdateDTO(
	@SerializedName("data")
	var data: SyncUpdateData,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncUpdateData(
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

	@SerializedName("deletedAt")
	var deletedAt: String? = null
)