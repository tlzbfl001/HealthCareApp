package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class SyncProfileResponse(
	@SerializedName("data")
	var data: ProfileResponse,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncFoodResponse(
	@SerializedName("data")
	var data: MutableList<FoodResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncDietsResponse(
	@SerializedName("data")
	var data: MutableList<DietResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncWaterResponse(
	@SerializedName("data")
	var data: MutableList<WaterResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncActivityResponse(
	@SerializedName("data")
	var data: MutableList<ActivityResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncWorkoutResponse(
	@SerializedName("data")
	var data: MutableList<WorkoutResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncBodyResponse(
	@SerializedName("data")
	var data: MutableList<BodyResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncSleepResponse(
	@SerializedName("data")
	var data: MutableList<SleepResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncMedicineResponse(
	@SerializedName("data")
	var data: MutableList<MedicineResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncMedicineIntakeResponse(
	@SerializedName("data")
	var data: List<MedicineIntakeResponse>,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)

data class SyncGoalResponse(
	@SerializedName("data")
	var data: List<GoalResponse>?,

	@SerializedName("syncedAt")
	var syncedAt: String = ""
)