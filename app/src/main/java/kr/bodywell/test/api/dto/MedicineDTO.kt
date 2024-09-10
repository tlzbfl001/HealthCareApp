package kr.bodywell.test.api.dto

import com.google.gson.annotations.SerializedName

data class MedicineDTO(
	@SerializedName("category")
	var category: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("amount")
	var amount: Int = 0,

	@SerializedName("unit")
	var unit: String = "",

	@SerializedName("starts")
	var starts: String = "",

	@SerializedName("ends")
	var ends: String = ""
)

data class MedicineTimeDTO(
	@SerializedName("time")
	var time: String = ""
)