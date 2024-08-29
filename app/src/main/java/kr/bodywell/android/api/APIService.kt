package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodUpdateDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.GoalUpdateDTO
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.api.response.ActivityResponse
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.CheckResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.DietResponse
import kr.bodywell.android.api.response.FoodResponse
import kr.bodywell.android.api.response.GoalResponse
import kr.bodywell.android.api.response.MedicineResponse
import kr.bodywell.android.api.response.MedicineTimeResponse
import kr.bodywell.android.api.response.ProfileResponse
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.SyncActivityResponse
import kr.bodywell.android.api.response.SyncBodyResponse
import kr.bodywell.android.api.response.SyncDietsResponse
import kr.bodywell.android.api.response.SyncFoodResponse
import kr.bodywell.android.api.response.SyncGoalResponse
import kr.bodywell.android.api.response.SyncMedicineResponse
import kr.bodywell.android.api.response.SyncProfileResponse
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

	@POST("auth/naver/login")
	suspend fun loginWithNaver(
		@Body dto: NaverLoginDTO
	): Response<TokenResponse>

	@POST("auth/kakao/login")
	suspend fun loginWithKakao(
		@Body dto: KakaoLoginDTO
	): Response<TokenResponse>

	@POST("auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@GET("users/check-email/{email}")
	suspend fun getUserEmail(
		@Path("email") uid: String
	): Response<CheckResponse>

	@GET("user")
	suspend fun getUser(
		@Header("Authorization") token: String
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

	@POST("user/profile/sync")
	suspend fun syncProfile(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncProfileResponse>

	@GET("devices")
	suspend fun getDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@POST("devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@GET("foods")
	suspend fun getAllFood(
		@Header("Authorization") token: String
	): Response<List<FoodResponse>>

	@GET("foods/{uid}")
	suspend fun getFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<FoodResponse>

	@POST("foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@POST("foods/sync")
	suspend fun syncFood(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncFoodResponse>

	@PATCH("foods/{uid}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: FoodUpdateDTO
	): Response<FoodResponse>

	@DELETE("foods/{uid}")
	suspend fun deleteFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<FoodResponse>

	@GET("diets")
	suspend fun getAllDiet(
		@Header("Authorization") token: String
	): Response<List<DietResponse>>

	@GET("diets/{uid}")
	suspend fun getDiet(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<DietResponse>

	@POST("diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@POST("diets/sync")
	suspend fun syncDiets(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncDietsResponse>

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
	): Response<DietResponse>

	@GET("waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@GET("waters/{id}")
	suspend fun getWater(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WaterResponse>

	@POST("waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@POST("waters/sync")
	suspend fun syncWater(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWaterResponse>

	@PATCH("waters/{id}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@DELETE("waters/{id}")
	suspend fun deleteWater(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WaterResponse>

	@GET("activities")
	suspend fun getAllActivity(
		@Header("Authorization") token: String
	): Response<List<ActivityResponse>>

	@GET("activities/{id}")
	suspend fun getActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<ActivityResponse>

	@POST("activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@POST("activities/sync")
	suspend fun syncActivity(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncActivityResponse>

	@PATCH("activities/{id}")
	suspend fun updateActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@DELETE("activities/{id}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<ActivityResponse>

	@GET("workouts")
	suspend fun getAllWorkout(
		@Header("Authorization") token: String
	): Response<List<WorkoutResponse>>

	@GET("workouts/{id}")
	suspend fun getWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WorkoutResponse>

	@POST("workouts")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Body dto: WorkoutDTO
	): Response<WorkoutResponse>

	@POST("workouts/sync")
	suspend fun syncWorkout(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncWorkoutResponse>

	@PATCH("workouts/{id}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: WorkoutUpdateDTO
	): Response<WorkoutResponse>

	@DELETE("workouts/{id}")
	suspend fun deleteWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WorkoutResponse>

	@GET("bodies")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@POST("bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("bodies/sync")
	suspend fun syncBody(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncBodyResponse>

	@PATCH("bodies/{id}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@GET("sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@POST("sleeps")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@POST("sleeps/sync")
	suspend fun syncSleep(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncSleepResponse>

	@PATCH("sleeps/{id}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@GET("medicines")
	suspend fun getAllMedicine(
		@Header("Authorization") token: String
	): Response<List<MedicineResponse>>

	@GET("medicines/{id}")
	suspend fun getMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineResponse>

	@GET("medicines/check-date")
	suspend fun getCheckMedicine(
		@Header("Authorization") token: String,
		@Query("date") date: String
	): Response<CheckResponse>

	@POST("medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@POST("medicines/sync")
	suspend fun syncMedicine(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncMedicineResponse>

	@PATCH("medicines/{id}")
	suspend fun updateMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@DELETE("medicines/{id}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineResponse>

	@GET("medicines/{id}/times")
	suspend fun getAllMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<List<MedicineTimeResponse>>

	@GET("medicines/{id}/times/{timeUid}")
	suspend fun getMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeUid") timeUid: String
	): Response<MedicineTimeResponse>

	@POST("medicines/{id}/times")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@DELETE("medicines/{id}/times/{timeUid}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeUid") timeUid: String
	): Response<MedicineTimeResponse>

	@GET("goals")
	suspend fun getAllGoal(
		@Header("Authorization") token: String
	): Response<List<GoalResponse>>

	@POST("goals")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@POST("goals/sync")
	suspend fun syncGoal(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncGoalResponse>

	@PATCH("goals/{uid}")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: GoalUpdateDTO
	): Response<GoalResponse>
}