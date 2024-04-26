package kr.bodywell.android.retrofit.dto

data class BodyDto(
	var height: Double = 0.0,
	var weight: Double = 0.0,
	var bodyMassIndex: Double = 0.0,
	var bodyFatPercentage: Double = 0.0,
	var skeletalMuscleMass: Double = 0.0,
	var basalMetabolicRate: Double = 0.0,
	var workoutIntensity: Int = 0,
	var time: String = ""
)