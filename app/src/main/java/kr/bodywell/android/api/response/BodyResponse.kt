package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class BodyResponse(
	@SerializedName("id")
	var id: String = "",

	@SerializedName("height")
	var height: Double? = null,

	@SerializedName("weight")
	var weight: Double? = null,

	@SerializedName("bodyFatPercentage")
	var bodyFatPercentage: Double? = null,

	@SerializedName("skeletalMuscleMass")
	var skeletalMuscleMass: Double? = null,

	@SerializedName("bodyMassIndex")
	var bodyMassIndex: Double? = null,

	@SerializedName("basalMetabolicRate")
	var basalMetabolicRate: Double? = null,

	@SerializedName("workoutIntensity")
	var workoutIntensity: Int = 1,

	@SerializedName("time")
	var time: String = "",

	@SerializedName("createdAt")
	var createdAt: String?,

	@SerializedName("updatedAt")
	var updatedAt: String?
)