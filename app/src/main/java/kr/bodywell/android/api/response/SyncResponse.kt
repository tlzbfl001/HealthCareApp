package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SyncProfileResponse(
	@SerializedName("data")
	var data: ProfileResponse
)

data class SyncFoodResponse(
	@SerializedName("data")
	var data: MutableList<FoodResponse>
)

data class SyncDietsResponse(
	@SerializedName("data")
	var data: MutableList<DietResponse>
)

data class SyncWaterResponse(
	@SerializedName("data")
	var data: MutableList<WaterResponse>
)

data class SyncActivityResponse(
	@SerializedName("data")
	var data: MutableList<ActivityResponse>
)

data class SyncWorkoutResponse(
	@SerializedName("data")
	var data: MutableList<WorkoutResponse>
)

data class SyncBodyResponse(
	@SerializedName("data")
	var data: MutableList<BodyResponse>
)

data class SyncSleepResponse(
	@SerializedName("data")
	var data: MutableList<SleepResponse>
)

data class SyncMedicineResponse(
	@SerializedName("data")
	var data: MutableList<MedicineResponse>
)

data class SyncGoalResponse(
	@SerializedName("data")
	var data: List<GoalResponse>?
)