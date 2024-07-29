package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SyncFoodResponse(
	@SerializedName("data")
	var data: List<FoodResponse>
)

data class SyncDietsResponse(
	@SerializedName("data")
	var data: List<DietResponse>
)

data class SyncWaterResponse(
	@SerializedName("data")
	var data: List<WaterResponse>
)

data class SyncActivityResponse(
	@SerializedName("data")
	var data: List<ActivityResponse>
)

data class SyncWorkoutResponse(
	@SerializedName("data")
	var data: List<WorkoutResponse>
)

data class SyncBodyResponse(
	@SerializedName("data")
	var data: List<BodyResponse>
)

data class SyncSleepResponse(
	@SerializedName("data")
	var data: List<SleepResponse>
)

data class SyncMedicineResponse(
	@SerializedName("data")
	var data: List<MedicineResponse>
)

data class SyncGoalResponse(
	@SerializedName("data")
	var data: List<GoalResponse>
)