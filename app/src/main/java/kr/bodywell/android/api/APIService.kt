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
import kr.bodywell.android.api.dto.MedicineUpdateDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.api.dto.NoteDTO
import kr.bodywell.android.api.dto.NoteUpdateDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WaterUpdateDTO
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
import kr.bodywell.android.api.response.NoteResponse
import kr.bodywell.android.api.response.FileResponse
import kr.bodywell.android.api.response.ProfileResponse
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.TokenResponse
import kr.bodywell.android.api.response.UserResponse
import kr.bodywell.android.api.response.WaterResponse
import kr.bodywell.android.api.response.WorkoutResponse
import okhttp3.MultipartBody
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

	@GET("v1/user")
	suspend fun getUser(
		@Header("Authorization") token: String
	): Response<UserResponse>

	@DELETE("v1/user")
	suspend fun deleteUser(
		@Header("Authorization") token: String
	): Response<Void>

	@GET("v1/profile")
	suspend fun getProfile(
		@Header("Authorization") token: String
	): Response<ProfileResponse>

	@PATCH("v1/profiles/{id}")
	suspend fun updateProfile(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: ProfileDTO
	): Response<ProfileResponse>

	@Multipart
	@POST("v1/profiles/{id}/picture")
	suspend fun createProfileFile(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Part file : MultipartBody.Part
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

	@GET("v1/diets/{id}")
	suspend fun getDiets(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<DietResponse>

	@PUT("v1/diets/{id}")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@Multipart
	@PUT("v1/diets/{id}/uploads/{photoId}")
	suspend fun createDietFile(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("photoId") photoId: String,
		@Part photo : MultipartBody.Part
	): Response<DietResponse>

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

	@GET("v1/waters/{id}")
	suspend fun getWater(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<WaterResponse>

	@PUT("v1/waters/{id}")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

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

	@PUT("v1/activities/{id}")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@DELETE("v1/activities/{id}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<ActivityResponse>

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

	@PUT("v1/body-measurements/{id}")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@GET("v1/body-measurements/{id}")
	suspend fun getBody(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<BodyResponse>

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

	@PUT("v1/sleeps/{id}")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@PATCH("v1/sleeps/{id}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: SleepUpdateDTO
	): Response<SleepResponse>

	@GET("v1/medicines")
	suspend fun getMedicines(
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

	@PATCH("v1/medicines/{id}")
	suspend fun updateMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineUpdateDTO
	): Response<MedicineResponse>

	@DELETE("v1/medicines/{id}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineResponse>

	@PUT("v1/medicine-times/{id}")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@DELETE("v1/medicine-times/{id}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<Void>

	@PUT("v1/medicine-intakes/{id}")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: MedicineIntakeDTO
	): Response<MedicineIntakeResponse>

	@DELETE("v1/medicine-intakes/{id}")
	suspend fun deleteMedicineIntake(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<MedicineTimeResponse>

	@GET("v1/notes/{id}")
	suspend fun getNote(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<NoteResponse>

	@PUT("v1/notes/{id}")
	suspend fun createNote(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: NoteDTO
	): Response<NoteResponse>

	@Multipart
	@PUT("v1/notes/{id}/uploads/{photoId}")
	suspend fun createNoteFile(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Path("photoId") photoId: String,
		@Part file : MultipartBody.Part?
	): Response<NoteResponse>

	@PATCH("v1/notes/{id}")
	suspend fun updateNote(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: NoteUpdateDTO
	): Response<NoteResponse>

	@GET("v1/goals/{id}")
	suspend fun getGoal(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<GoalResponse>

	@PUT("v1/goals/{id}")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@PATCH("v1/goals/{id}")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Path("id") id: String,
		@Body dto: GoalUpdateDTO
	): Response<GoalResponse>

	@DELETE("v1/files/{id}")
	suspend fun deleteFile(
		@Header("Authorization") token: String,
		@Path("id") id: String
	): Response<FileResponse>
}