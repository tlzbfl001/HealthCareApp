package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.BodyUpdateDTO
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
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WaterUpdateDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.api.response.ActivityResponse
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.DietResponse
import kr.bodywell.android.api.response.ExistResponse
import kr.bodywell.android.api.response.FileResponse
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
import okhttp3.MultipartBody
import org.json.JSONArray
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

	@GET("v1/users/check-username/{username}")
	suspend fun getUserEmail(
		@Path("username") username: String
	): Response<Boolean>

	@GET("v1/user")
	suspend fun getUser(
		@Header("Authorization") token: String
	): Response<UserResponse>

	@DELETE("v1/user")
	suspend fun deleteUser(
		@Header("Authorization") token: String
	): Response<Void>

	@GET("v1/users/profile")
	suspend fun getProfile(
		@Header("Authorization") token: String
	): Response<ProfileResponse>

	@POST("v1/user/sync")
	suspend fun syncProfile(
		@Header("Authorization") token: String,
		@Body dto: SyncDTO
	): Response<SyncProfileResponse>

	@PATCH("v1/users/profile")
	suspend fun updateProfile(
		@Header("Authorization") token: String,
		@Body dto: ProfileDTO
	): Response<ProfileResponse>

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

	@PUT("v1/foods/{id}")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

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

	@PUT("v1/diets/{id}")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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
	suspend fun deleteDiet(
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

	@GET("v1/waters/check-date")
	suspend fun getExistWater(
		@Header("Authorization") token: String,
		@Query("date") date: String
	): Response<ExistResponse>

	@PUT("v1/waters/{id}")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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
		@Body dto: WaterUpdateDTO
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

	@PUT("v1/activities/{id}")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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

	@PUT("v1/workouts/{id}")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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

	@PUT("v1/body-measurements/{id}")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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
		@Body dto: BodyUpdateDTO
	): Response<BodyResponse>

	@DELETE("v1/body-measurements/{id}")
	suspend fun deleteBody(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<BodyResponse>

	@GET("v1/sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@PUT("v1/sleeps/{id}")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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
		@Body dto: SleepUpdateDTO
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

	@PUT("v1/medicines/{id}")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String,
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

	@GET("v1/medicines/{id}/times/{timeId}")
	suspend fun getMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeId") timeId: String
	): Response<MedicineTimeResponse>

	@PUT("v1/medicines/{medicineId}/times/{id}")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("medicineId") medicineId: String,
		@Path("id") id: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@DELETE("v1/medicines/{id}/times/{timeId}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeId") timeId: String
	): Response<MedicineTimeResponse>

	@GET("v1/medicines/{id}/times/{timeId}/intakes")
	suspend fun getMedicineIntake(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeId") timeId: String
	): Response<MedicineTimeResponse>

	@POST("v1/medicines/{id}/times/{timeId}/intakes")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeId") timeId: String,
		@Body dto: MedicineIntakeDTO
	): Response<MedicineTimeResponse>

	@POST("v1/medicines/{id}/times/{timeId}/intakes/sync")
	suspend fun syncMedicineIntake(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("timeId") timeId: String
	): Response<MedicineTimeResponse>

	@DELETE("v1/medicines/{medicineId}/times/{medicineTimeId}/intakes/{id}")
	suspend fun deleteMedicineIntake(
		@Header("Authorization") token: String,
		@Path("medicineId") medicineId: String,
		@Path("medicineTimeId") medicineTimeId: String,
		@Path("id") id: String
	): Response<MedicineTimeResponse>

	@GET("v1/goals")
	suspend fun getAllGoal(
		@Header("Authorization") token: String
	): Response<List<GoalResponse>>

	@PUT("v1/goals")
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

	@Multipart
	@POST("v1/files/upload")
	suspend fun uploadFile(
		@Header("Authorization") token: String,
		@Part file : MultipartBody.Part
	): Response<FileResponse>
}