package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class GoalResponse (
	@SerializedName("uid")
	var uid: String = "",

	@SerializedName("weight")
	var weight: Double = 0.0,

	@SerializedName("kcalOfDiet")
	var kcalOfDiet: Int = 0,

	@SerializedName("kcalOfWorkout")
	var kcalOfWorkout: Int = 0,

	@SerializedName("waterAmountOfCup")
	var waterAmountOfCup: Int = 0,

	@SerializedName("waterIntake")
	var waterIntake: Int = 0,

	@SerializedName("sleep")
	var sleep: Int = 0,

	@SerializedName("medicineIntake")
	var medicineIntake: Int = 0,

	@SerializedName("date")
	var date: String = ""
)