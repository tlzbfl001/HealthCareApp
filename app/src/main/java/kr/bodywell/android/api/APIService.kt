package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.BodyUpdateDTO
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
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
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.TokenResponse
import kr.bodywell.android.api.response.UserResponse
import kr.bodywell.android.api.response.WaterResponse
import kr.bodywell.android.api.response.WorkoutResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
	@GET("/users")
	suspend fun getAllUser(): Response<List<UserResponse>>

	@GET("/devices")
	suspend fun getAllDevice(
		@Header("Authorization") token: String
	): Response<List<DeviceResponse>>

	@GET("/foods")
	suspend fun getAllFood(
		@Header("Authorization") token: String
	): Response<List<FoodResponse>>

	@GET("/workouts/{uid}")
	suspend fun getWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
	): Response<WorkoutResponse>

	@GET("/diets")
	suspend fun getAllDiet(
		@Header("Authorization") token: String
	): Response<List<DietResponse>>

	@GET("/diets/{uid}")
	suspend fun getDiet(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
	): Response<DietResponse>

	@GET("/waters")
	suspend fun getAllWater(
		@Header("Authorization") token: String
	): Response<List<WaterResponse>>

	@GET("/activities")
	suspend fun getAllActivity(
		@Header("Authorization") token: String
	): Response<List<ActivityResponse>>

	@GET("/bodies")
	suspend fun getAllBody(
		@Header("Authorization") token: String
	): Response<List<BodyResponse>>

	@GET("/sleeps")
	suspend fun getAllSleep(
		@Header("Authorization") token: String
	): Response<List<SleepResponse>>

	@FormUrlEncoded
	@POST("/auth/google/login")
	suspend fun googleLogin(
		@Field("idToken") idToken: String
	): Response<TokenResponse>

	@POST("/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Body dto: DeviceDTO
	): Response<DeviceResponse>

	@POST("/foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@POST("/diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@POST("/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@POST("/bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("/activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@POST("/workouts")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Body dto: WorkoutDTO
	): Response<WorkoutResponse>

	@POST("/sleeps")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@POST("/medicines")
	suspend fun createMedicine(
		@Header("Authorization") token: String,
		@Body dto: MedicineDTO
	): Response<MedicineResponse>

	@POST("/medicines/{uid}/times")
	suspend fun createMedicineTime(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: MedicineTimeDTO
	): Response<MedicineTimeResponse>

	@POST("/medicines/times/{uid}/intakes")
	suspend fun createMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: MedicineIntakeDTO
	): Response<MedicineIntakeResponse>

	@POST("/goals")
	suspend fun createGoal(
		@Header("Authorization") token: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@POST("/auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@PATCH("/foods/{uid}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@PATCH("/diets/{uid}")
	suspend fun updateDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: DietUpdateDTO
	): Response<DietResponse>

	@PATCH("/waters/{uid}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@PATCH("/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyUpdateDTO
	): Response<BodyResponse>

	@PATCH("/activities/{uid}")
	suspend fun updateActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@PATCH("/workouts/{uid}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WorkoutUpdateDTO
	): Response<WorkoutResponse>

	@PATCH("/sleeps/{uid}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: SleepUpdateDTO
	): Response<SleepResponse>

	@PATCH("/goals/{uid}")
	suspend fun updateGoal(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: GoalDTO
	): Response<GoalResponse>

	@DELETE("/users/{uid}")
	suspend fun deleteUser(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/foods/{uid}")
	suspend fun deleteFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/diets/{uid}")
	suspend fun deleteDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/activities/{uid}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/workouts/{uid}")
	suspend fun deleteWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/medicines/intakes/{uid}")
	suspend fun deleteMedicineIntake(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/medicines/times/{uid}")
	suspend fun deleteMedicineTime(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/medicines/{uid}")
	suspend fun deleteMedicine(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>
}