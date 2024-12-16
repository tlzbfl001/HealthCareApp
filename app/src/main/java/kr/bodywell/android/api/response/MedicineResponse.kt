package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class MedicineResponse (
	@SerializedName("id")
	var id: String = "",

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
	var ends: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?,

	@SerializedName("deletedAt")
	var deletedAt: String?
)

data class MedicineTimeResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("time")
	var time: String = "",

	@SerializedName("medicineId")
	var medicineId: String = ""
)

data class MedicineIntakeResponse (
	@SerializedName("id")
	var id: String = "",

	@SerializedName("category")
	var category: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("amount")
	var amount: Int = 0,

	@SerializedName("unit")
	var unit: String = "",

	@SerializedName("intakedAt")
	var intakedAt: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("medicineTimeId")
	var medicineTimeId: String = "",

	@SerializedName("medicineId")
	var medicineId: String = ""
)