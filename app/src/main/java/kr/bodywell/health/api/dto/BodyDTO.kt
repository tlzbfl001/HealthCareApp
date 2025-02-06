package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class BodyDTO(
	@SerializedName("height")
	var height: Double? = null,

	@SerializedName("weight")
	var weight: Double? = null,

	@SerializedName("bodyMassIndex")
	var bodyMassIndex: Double? = null,

	@SerializedName("bodyFatPercentage")
	var bodyFatPercentage: Double? = null,

	@SerializedName("skeletalMuscleMass")
	var skeletalMuscleMass: Double? = null,

	@SerializedName("basalMetabolicRate")
	var basalMetabolicRate: Double? = null,

	@SerializedName("workoutIntensity")
	var workoutIntensity: Int = 1,

	@SerializedName("time")
	var time: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class BodyUpdateDTO(
	@SerializedName("height")
	var height: Double? = null,

	@SerializedName("weight")
	var weight: Double? = null,

	@SerializedName("bodyMassIndex")
	var bodyMassIndex: Double? = null,

	@SerializedName("bodyFatPercentage")
	var bodyFatPercentage: Double? = null,

	@SerializedName("skeletalMuscleMass")
	var skeletalMuscleMass: Double? = null,

	@SerializedName("basalMetabolicRate")
	var basalMetabolicRate: Double? = null,

	@SerializedName("workoutIntensity")
	var workoutIntensity: Int = 1
)