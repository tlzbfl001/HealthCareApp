package kr.bodywell.test.api

import kr.bodywell.test.api.dto.ActivityDTO
import kr.bodywell.test.api.dto.BodyDTO
import kr.bodywell.test.api.dto.DeviceDTO
import kr.bodywell.test.api.dto.DietDTO
import kr.bodywell.test.api.dto.DietUpdateDTO
import kr.bodywell.test.api.dto.FoodDTO
import kr.bodywell.test.api.dto.GoalDTO
import kr.bodywell.test.api.dto.LoginDTO
import kr.bodywell.test.api.dto.MedicineDTO
import kr.bodywell.test.api.dto.MedicineIntakeDTO
import kr.bodywell.test.api.dto.MedicineTimeDTO
import kr.bodywell.test.api.dto.MedicineUpdateDTO
import kr.bodywell.test.api.dto.ProfileDTO
import kr.bodywell.test.api.dto.SleepDTO
import kr.bodywell.test.api.dto.SleepUpdateDTO
import kr.bodywell.test.api.dto.SyncDTO
import kr.bodywell.test.api.dto.SyncUpdateDTO
import kr.bodywell.test.api.dto.SyncedAtDTO
import kr.bodywell.test.api.dto.WaterDTO
import kr.bodywell.test.api.dto.WorkoutDTO
import kr.bodywell.test.api.dto.WorkoutUpdateDTO
import kr.bodywell.test.api.response.ActivityResponse
import kr.bodywell.test.api.response.BodyResponse
import kr.bodywell.test.api.response.DeviceResponse
import kr.bodywell.test.api.response.DietResponse
import kr.bodywell.test.api.response.FoodResponse
import kr.bodywell.test.api.response.GoalResponse
import kr.bodywell.test.api.response.MedicineAllResponse
import kr.bodywell.test.api.response.MedicineIntakeResponse
import kr.bodywell.test.api.response.MedicineResponse
import kr.bodywell.test.api.response.MedicineTimeResponse
import kr.bodywell.test.api.response.ProfileResponse
import kr.bodywell.test.api.response.SyncProfileResponse
import kr.bodywell.test.api.response.SleepResponse
import kr.bodywell.test.api.response.SyncResponse
import kr.bodywell.test.api.response.TokenResponse
import kr.bodywell.test.api.response.UserResponse
import kr.bodywell.test.api.response.WaterResponse
import kr.bodywell.test.api.response.WorkoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
	@GET("user")
	suspend fun getAllUser(): Response<List<UserResponse>>

	@DELETE("user")
	suspend fun deleteUser(
		@Header("Authorization") token: String
	): Response<Void>

	@GET("user/profile")
	suspend fun getProfile(
		@Header("Authorization") token: String
	): Response<ProfileResponse>

	@POST("user/profile/sync")
	suspend fun syncProfile(
		@Header("Authorization") token: String,
		@Body dto: SyncedAtDTO
	): Response<SyncProfileResponse>

	@PATCH("user/profile")
	suspend fun updateProfile(
		@Header("Authorization") token: String,
		@Body dto: ProfileDTO
	): Response<ProfileResponse>

	@GET("user/devices")
	suspend fun getAllDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@POST("user/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@GET("user/bodies")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@POST("user/bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@PATCH("user/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@GET("user/activities")
	suspend fun getAllActivity(
		@Header("Authorization") token: String
	): Response<List<ActivityResponse>>

	@POST("user/activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

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
	): Response<Void>

	@POST("user/activities/sync")
	suspend fun syncCreateActivity(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncResponse>

	@POST("user/activities/sync")
	suspend fun syncUpdateActivity(
		@Header("Authorization") token: String,
		@Body dto: SyncUpdateDTO
	): Response<SyncResponse>

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
	): Response<Void>

	@GET("sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@POST("sleeps")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@PATCH("sleeps/{uid}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: SleepUpdateDTO
	): Response<SleepResponse>

	@GET("user/waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@POST("user/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@PATCH("user/waters/{uid}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@GET("foods")
	suspend fun getAllFood(
		@Header("Authorization") token: String
	): Response<List<FoodResponse>>

	@POST("foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@PATCH("foods/{uid}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@DELETE("foods/{uid}")
	suspend fun deleteFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@GET("diets")
	suspend fun getAllDiet(
		@Header("Authorization") token: String
	): Response<List<DietResponse>>

	@GET("diets/{uid}")
	suspend fun getDiet(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
	): Response<DietResponse>

	@POST("diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@PATCH("diets/{uid}")
	suspend fun updateDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: DietUpdateDTO
	): Response<DietResponse>

	@DELETE("diets/{uid}")
	suspend fun deleteDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@GET("user/medicines")
	suspend fun getAllMedicine(
		@Header("Authorization") token: String
	): Response<List<MedicineAllResponse>>

	@POST("user/medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@PATCH("user/medicines/{uid}")
	suspend fun updateMedicine(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: MedicineUpdateDTO
	): Response<MedicineResponse>

	@DELETE("user/medicines/{uid}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

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
	): Response<Void>

	@POST("user/medicines/{uid}/times/{timeUid}/intakes")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String,
		@Body dto: MedicineIntakeDTO
	): Response<MedicineIntakeResponse>

	@DELETE("user/medicines/{uid}/times/{timeUid}/intakes/{intakeUid}")
	suspend fun deleteMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Path("timeUid") timeUid: String,
		@Path("intakeUid") intakeUid: String
	): Response<Void>

	@POST("auth/google/login")
	suspend fun loginWithGoogle(
		@Body dto: LoginDTO
	): Response<TokenResponse>

	@POST("auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@GET("user/goal")
	suspend fun getGoal(
		@Header("Authorization") token: String
	): Response<GoalResponse>

	@POST("user/goal")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@PATCH("user/goal")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>
}