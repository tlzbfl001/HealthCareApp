package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.BodyUpdateDTO
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.MedicineUpdateDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.SyncedAtDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.api.response.ActivityResponse
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.DietResponse
import kr.bodywell.android.api.response.FoodResponse
import kr.bodywell.android.api.response.GoalResponse
import kr.bodywell.android.api.response.MedicineAllResponse
import kr.bodywell.android.api.response.MedicineIntakeResponse
import kr.bodywell.android.api.response.MedicineResponse
import kr.bodywell.android.api.response.MedicineTimeResponse
import kr.bodywell.android.api.response.ProfileResponse
import kr.bodywell.android.api.response.SyncProfileResponse
import kr.bodywell.android.api.response.SleepResponse
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

interface APIService {
	@GET("users")
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

	@GET("devices")
	suspend fun getAllDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@POST("user/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@GET("bodies")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@POST("bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@PATCH("bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyUpdateDTO
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

	@GET("workouts")
	suspend fun getAllWorkout(
		@Header("Authorization") token: String
	): Response<List<WorkoutResponse>>

	@GET("workouts/{uid}")
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

	@GET("waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@POST("user/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@PATCH("waters/{uid}")
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

	@GET("medicines/intakes")
	suspend fun getAllMedicine(
		@Header("Authorization") token: String
	): Response<List<MedicineAllResponse>>

	@POST("medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@PATCH("medicines/{uid}")
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

	@POST("medicines/{uid}/times")
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

	@POST("medicines/times/{uid}/intakes")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
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

	@POST("goals")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@PATCH("goals/{uid}")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>
}