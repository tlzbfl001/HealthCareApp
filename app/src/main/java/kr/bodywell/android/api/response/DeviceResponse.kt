package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class DeviceResponse(
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("label")
	var label: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("manufacturer")
	var manufacturer: String = "",

	@SerializedName("model")
	var model: String = "",

	@SerializedName("hardwareVersion")
	var hardwareVersion: String = "",

	@SerializedName("softwareVersion")
	var softwareVersion: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class DeviceResponses (
	@SerializedName("devices")
	var devices: List<DeviceResponse>
)