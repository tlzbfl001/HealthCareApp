package kr.bodywell.android.service

import com.google.gson.annotations.SerializedName

data class BodyResponse(
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("height")
	var height: String = "",

	@SerializedName("weight")
	var weight: String = "",

	@SerializedName("bodyFatPercentage")
	var bodyFatPercentage: Double = 0.0,

	@SerializedName("skeletalMuscleMass")
	var skeletalMuscleMass: Double = 0.0,

	@SerializedName("bodyMassIndex")
	var bodyMassIndex: Double = 0.0,

	@SerializedName("basalMetabolicRate")
	var basalMetabolicRate: Double = 0.0,

	@SerializedName("workoutIntensity")
	var workoutIntensity: Int = 0,

	@SerializedName("time")
	var time: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)