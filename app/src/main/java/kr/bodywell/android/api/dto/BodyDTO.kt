package kr.bodywell.android.api.dto

data class BodyDTO(
	var height: Double = 0.0,
	var weight: Double = 0.0,
	var bodyMassIndex: Double = 0.0,
	var bodyFatPercentage: Double = 0.0,
	var skeletalMuscleMass: Double = 0.0,
	var basalMetabolicRate: Double = 0.0,
	var workoutIntensity: Int = 0,
	var time: String = ""
)