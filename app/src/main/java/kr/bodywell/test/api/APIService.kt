package kr.bodywell.test.api

import kr.bodywell.test.api.dto.ActivityDTO
import kr.bodywell.test.api.dto.BodyDTO
import kr.bodywell.test.api.dto.DeviceDTO
import kr.bodywell.test.api.dto.DietDTO
import kr.bodywell.test.api.dto.DietUpdateDTO
import kr.bodywell.test.api.dto.FoodDTO
import kr.bodywell.test.api.dto.FoodUpdateDTO
import kr.bodywell.test.api.dto.GoalDTO
import kr.bodywell.test.api.dto.GoalUpdateDTO
import kr.bodywell.test.api.dto.KakaoLoginDTO
import kr.bodywell.test.api.dto.LoginDTO
import kr.bodywell.test.api.dto.MedicineDTO
import kr.bodywell.test.api.dto.MedicineTimeDTO
import kr.bodywell.test.api.dto.NaverLoginDTO
import kr.bodywell.test.api.dto.ProfileDTO
import kr.bodywell.test.api.dto.SleepDTO
import kr.bodywell.test.api.dto.SyncDTO
import kr.bodywell.test.api.dto.WaterDTO
import kr.bodywell.test.api.dto.WorkoutDTO
import kr.bodywell.test.api.dto.WorkoutUpdateDTO
import kr.bodywell.test.api.response.ActivityResponse
import kr.bodywell.test.api.response.BodyResponse
import kr.bodywell.test.api.response.CheckResponse
import kr.bodywell.test.api.response.DeviceResponse
import kr.bodywell.test.api.response.DietResponse
import kr.bodywell.test.api.response.FoodResponse
import kr.bodywell.test.api.response.GoalResponse
import kr.bodywell.test.api.response.MedicineResponse
import kr.bodywell.test.api.response.MedicineTimeResponse
import kr.bodywell.test.api.response.ProfileResponse
import kr.bodywell.test.api.response.SleepResponse
import kr.bodywell.test.api.response.SyncActivityResponse
import kr.bodywell.test.api.response.SyncBodyResponse
import kr.bodywell.test.api.response.SyncDietsResponse
import kr.bodywell.test.api.response.SyncFoodResponse
import kr.bodywell.test.api.response.SyncGoalResponse
import kr.bodywell.test.api.response.SyncMedicineResponse
import kr.bodywell.test.api.response.SyncProfileResponse
import kr.bodywell.test.api.response.SyncSleepResponse
import kr.bodywell.test.api.response.SyncWaterResponse
import kr.bodywell.test.api.response.SyncWorkoutResponse
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
import retrofit2.http.Query

interface APIService {
	@POST("v1/auth/google/login")
	suspend fun loginWithGoogle(
		@Body dto: LoginDTO
	): Response<TokenResponse>

	@POST("v1/auth/naver/login")
	suspend fun loginWithNaver(
		@Body dto: NaverLoginDTO
	): Response<TokenResponse>

	@POST("v1/auth/kakao/login")
	suspend fun loginWithKakao(
		@Body dto: KakaoLoginDTO
	): Response<TokenResponse>

	@POST("v1/auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@GET("v1/users/check-email/{email}")
	suspend fun getUserEmail(
		@Path("email") email: String
	): Response<CheckResponse>

	@GET("v1/user")
	suspend fun getUser(
		@Header("Authorization") token: String
	): Response<UserResponse>

	@DELETE("v1/user")
	suspend fun deleteUser(
		@Header("Authorization") token: String
	): Response<Void>

	@GET("profiles/{id}")
	suspend fun getProfile(
		@Header("Authorization") token: String
	): Response<ProfileResponse>

	@PATCH("profiles/{id}")
	suspend fun updateProfile(
		@Header("Authorization") token: String,
		@Body dto: ProfileDTO
	): Response<ProfileResponse>

	@POST("profiles/sync")
	suspend fun syncProfile(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncProfileResponse>

	@GET("v1/devices")
	suspend fun getDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@POST("v1/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@GET("v1/foods")
	suspend fun getAllFood(
		@Header("Authorization") token: String
	): Response<List<FoodResponse>>

	@GET("v1/foods/{id}")
	suspend fun getFood(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<FoodResponse>

	@POST("v1/foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@POST("v1/foods/sync")
	suspend fun syncFood(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncFoodResponse>

	@PATCH("v1/foods/{id}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: FoodUpdateDTO
	): Response<FoodResponse>

	@DELETE("v1/foods/{id}")
	suspend fun deleteFood(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<FoodResponse>

	@GET("v1/diets")
	suspend fun getAllDiet(
		@Header("Authorization") token: String
	): Response<List<DietResponse>>

	@GET("v1/diets/{id}")
	suspend fun getDiet(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<DietResponse>

	@POST("v1/diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@POST("v1/diets/sync")
	suspend fun syncDiets(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncDietsResponse>

	@PATCH("v1/diets/{id}")
	suspend fun updateDiets(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: DietUpdateDTO
	): Response<DietResponse>

	@DELETE("v1/diets/{id}")
	suspend fun deleteDiets(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<DietResponse>

	@GET("v1/waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@GET("v1/waters/{id}")
	suspend fun getWater(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WaterResponse>

	@POST("v1/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@POST("v1/waters/sync")
	suspend fun syncWater(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWaterResponse>

	@PATCH("v1/waters/{id}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@DELETE("v1/waters/{id}")
	suspend fun deleteWater(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WaterResponse>

	@GET("v1/activities")
	suspend fun getAllActivity(
		@Header("Authorization") token: String
	): Response<List<ActivityResponse>>

	@GET("v1/activities/{id}")
	suspend fun getActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<ActivityResponse>

	@POST("v1/activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@POST("v1/activities/sync")
	suspend fun syncActivity(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncActivityResponse>

	@PATCH("v1/activities/{id}")
	suspend fun updateActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@DELETE("v1/activities/{id}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<ActivityResponse>

	@GET("v1/workouts")
	suspend fun getAllWorkout(
		@Header("Authorization") token: String
	): Response<List<WorkoutResponse>>

	@GET("v1/workouts/{id}")
	suspend fun getWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WorkoutResponse>

	@POST("v1/workouts")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Body dto: WorkoutDTO
	): Response<WorkoutResponse>

	@POST("v1/workouts/sync")
	suspend fun syncWorkout(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWorkoutResponse>

	@PATCH("v1/workouts/{id}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: WorkoutUpdateDTO
	): Response<WorkoutResponse>

	@DELETE("v1/workouts/{id}")
	suspend fun deleteWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WorkoutResponse>

	@GET("v1/body-measurements")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@POST("v1/body-measurements")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("v1/body-measurements/sync")
	suspend fun syncBody(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncBodyResponse>

	@PATCH("v1/body-measurements/{id}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@GET("v1/sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@POST("v1/sleeps")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@POST("v1/sleeps/sync")
	suspend fun syncSleep(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncSleepResponse>

	@PATCH("v1/sleeps/{id}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@GET("v1/medicines")
	suspend fun getAllMedicine(
		@Header("Authorization") token: String
	): Response<List<MedicineResponse>>

	@GET("v1/medicines/{id}")
	suspend fun getMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineResponse>

	@POST("v1/medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@POST("v1/medicines/sync")
	suspend fun syncMedicine(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncMedicineResponse>

	@PATCH("v1/medicines/{id}")
	suspend fun updateMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@DELETE("v1/medicines/{id}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineResponse>

	@GET("v1/medicines/{id}/times")
	suspend fun getAllMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<List<MedicineTimeResponse>>

	@GET("v1/medicines/{id}/times/{timeUid}")
	suspend fun getMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeUid") timeUid: String
	): Response<MedicineTimeResponse>

	@POST("v1/medicines/{id}/times")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@DELETE("v1/medicines/{id}/times/{timeUid}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeUid") timeUid: String
	): Response<MedicineTimeResponse>

	@GET("v1/goals")
	suspend fun getAllGoal(
		@Header("Authorization") token: String
	): Response<List<GoalResponse>>

	@POST("v1/goals")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@POST("v1/goals/sync")
	suspend fun syncGoal(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncGoalResponse>

	@PATCH("v1/goals/{id}")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: GoalUpdateDTO
	): Response<GoalResponse>
}