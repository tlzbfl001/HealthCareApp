package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodUpdateDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.api.response.ActivityResponse
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.DietResponse
import kr.bodywell.android.api.response.FoodResponse
import kr.bodywell.android.api.response.GoalResponse
import kr.bodywell.android.api.response.MedicineIntakeResponse
import kr.bodywell.android.api.response.MedicineResponse
import kr.bodywell.android.api.response.MedicineTimeResponse
import kr.bodywell.android.api.response.ProfileResponse
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.SyncActivityResponse
import kr.bodywell.android.api.response.SyncBodyResponse
import kr.bodywell.android.api.response.SyncDietsResponse
import kr.bodywell.android.api.response.SyncFoodResponse
import kr.bodywell.android.api.response.SyncGoalResponse
import kr.bodywell.android.api.response.SyncMedicineIntakeResponse
import kr.bodywell.android.api.response.SyncMedicineResponse
import kr.bodywell.android.api.response.SyncSleepResponse
import kr.bodywell.android.api.response.SyncWaterResponse
import kr.bodywell.android.api.response.SyncWorkoutResponse
import kr.bodywell.android.api.response.TokenResponse
import kr.bodywell.android.api.response.UserResponse
import kr.bodywell.android.api.response.WaterResponse
import kr.bodywell.android.api.response.WorkoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
	@POST("auth/google/login")
	suspend fun loginWithGoogle(
		@Body dto: LoginDTO
	): Response<TokenResponse>

	@POST("auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@GET("users/check-email/{email}")
	suspend fun getUserEmail(
		@Path("email") uid: String
	): Response<UserResponse>

	@DELETE("user")
	suspend fun deleteUser(
		@Header("Authorization") token: String
	): Response<Void>

	@GET("user/profile")
	suspend fun getProfile(
		@Header("Authorization") token: String
	): Response<ProfileResponse>

	@PATCH("user/profile")
	suspend fun updateProfile(
		@Header("Authorization") token: String,
		@Body dto: ProfileDTO
	): Response<ProfileResponse>

	@GET("user/devices")
	suspend fun getDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@POST("user/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@GET("user/foods")
	suspend fun getAllFood(
		@Header("Authorization") token: String,
		@Query("search") search: String
	): Response<List<FoodResponse>>

	@POST("user/foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@POST("user/foods/sync")
	suspend fun syncFood(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncFoodResponse>

	@PATCH("user/foods/{uid}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: FoodUpdateDTO
	): Response<FoodResponse>

	@DELETE("user/foods/{uid}")
	suspend fun deleteFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<FoodResponse>

	@GET("user/diets")
	suspend fun getAllDiet(
		@Header("Authorization") token: String
	): Response<List<DietResponse>>

	@GET("user/diets/{uid}")
	suspend fun getDiet(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
	): Response<DietResponse>

	@POST("user/diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@POST("user/diets/sync")
	suspend fun syncDiets(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncDietsResponse>

	@PATCH("user/diets/{uid}")
	suspend fun updateDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: DietUpdateDTO
	): Response<DietResponse>

	@DELETE("user/diets/{uid}")
	suspend fun deleteDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<DietResponse>

	@GET("user/waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@POST("user/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@POST("user/waters/sync")
	suspend fun syncWater(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWaterResponse>

	@PATCH("user/waters/{uid}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@DELETE("user/waters/{uid}")
	suspend fun deleteWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<WaterResponse>

	@GET("user/activities")
	suspend fun getAllActivity(
		@Header("Authorization") token: String
	): Response<List<ActivityResponse>>

	@POST("user/activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@POST("user/activities/sync")
	suspend fun syncActivity(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncActivityResponse>

	@PATCH("user/activities/{uid}")
	suspend fun updateActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@DELETE("user/activities/{uid}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<ActivityResponse>

	@GET("user/workouts")
	suspend fun getAllWorkout(
		@Header("Authorization") token: String
	): Response<List<WorkoutResponse>>

	@GET("user/workouts/{uid}")
	suspend fun getWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
	): Response<WorkoutResponse>

	@POST("user/workouts")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Body dto: WorkoutDTO
	): Response<WorkoutResponse>

	@POST("user/workouts/sync")
	suspend fun syncWorkout(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWorkoutResponse>

	@PATCH("user/workouts/{uid}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WorkoutUpdateDTO
	): Response<WorkoutResponse>

	@DELETE("user/workouts/{uid}")
	suspend fun deleteWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<WorkoutResponse>

	@GET("user/bodies")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@POST("user/bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("user/bodies/sync")
	suspend fun syncBody(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncBodyResponse>

	@PATCH("user/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@GET("user/sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@POST("user/sleeps")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@POST("user/sleeps/sync")
	suspend fun syncSleep(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncSleepResponse>

	@PATCH("user/sleeps/{uid}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: SleepUpdateDTO
	): Response<SleepResponse>

	@GET("user/medicines")
	suspend fun getMedicine(
		@Header("Authorization") token: String
	): Response<List<MedicineResponse>>

	@POST("user/medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@POST("user/medicines/sync")
	suspend fun syncMedicine(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncMedicineResponse>

	@PATCH("user/medicines/{uid}")
	suspend fun updateMedicine(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@DELETE("user/medicines/{uid}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<MedicineResponse>

	@GET("user/medicines/{uid}/times")
	suspend fun getMedicineTime(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<List<MedicineTimeResponse>>

	@POST("user/medicines/{uid}/times")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@DELETE("user/medicines/{uid}/times/{timeUid}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String
	): Response<MedicineTimeResponse>

	@GET("user/medicines/{uid}/times/{timeUid}/intakes")
	suspend fun getMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String
	): Response<List<MedicineIntakeResponse>>

	@POST("user/medicines/{uid}/times/{timeUid}/intakes")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String,
		@Body dto: MedicineIntakeDTO
	): Response<MedicineResponse>

	@POST("user/medicines/{uid}/times/{timeUid}/intakes/sync")
	suspend fun syncMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String,
		@Body dto: SyncDTO
	): Response<SyncMedicineIntakeResponse>

	@DELETE("user/medicines/{uid}/times/{timeUid}/intakes/{intakeUid}")
	suspend fun deleteMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String,
		@Path("intakeUid") intakeUid: String
	): Response<MedicineResponse>

	@GET("user/goal")
	suspend fun getGoal(
		@Header("Authorization") token: String
	): Response<GoalResponse>

	@POST("user/goal")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@POST("user/goal/sync")
	suspend fun syncGoal(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncGoalResponse>

	@PATCH("user/goal")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>
}