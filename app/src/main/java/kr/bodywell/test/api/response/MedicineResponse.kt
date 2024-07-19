package kr.bodywell.test.api.response

import com.google.gson.annotations.SerializedName

data class MedicineResponse (
	@SerializedName("uid")
	var uid: String = "",

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

data class MedicineAllResponse (
	@SerializedName("uid")
	var uid: String = "",

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

	@SerializedName("medicineTimes")
	var medicineTimes: List<MedicineTimeData>
)

data class MedicineTimeData (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("time")
	var time: String = "",

	@SerializedName("medicineIntakes")
	var medicineIntakes: List<MedicineIntakeResponse>
)

data class MedicineTimeResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("time")
	var time: String = ""
)

data class MedicineIntakeResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("intakedAt")
	var intakeAt: String = ""
)
