package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class DeviceDTO(
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
	var softwareVersion: String = ""
)