package kr.bodywell.android.api

import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.api.response.ActivityResponse
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.DevicesResponse
import kr.bodywell.android.api.response.DietResponse
import kr.bodywell.android.api.response.FoodResponse
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.SleepsResponse
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
	suspend fun getUsers(): Response<UserResponse>

	@GET("/devices")
	suspend fun getDevices(
		@Header("Authorization") token: String
	): Response<DevicesResponse>

	@GET("/health/sleep")
	suspend fun getSleeps(
		@Header("Authorization") token: String
	): Response<SleepsResponse>

	@FormUrlEncoded
	@POST("/auth/google/login")
	suspend fun googleLogin(
		@Field("idToken") idToken: String
	): Response<TokenResponse>

	@FormUrlEncoded
	@POST("/devices")
	suspend fun createDevice(
		@Header("Authorization") token: String,
		@Field("label") label: String,
		@Field("name") name: String,
		@Field("manufacturer") manufacturer: String,
		@Field("model") model: String,
		@Field("hardwareVersion") hardwareVersion: String,
		@Field("softwareVersion") softwareVersion: String
	): Response<DeviceResponse>

	@POST("/health/foods")
	suspend fun createFood(
		@Header("Authorization") token: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@POST("/health/diets")
	suspend fun createDiets(
		@Header("Authorization") token: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@POST("/health/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@POST("/health/bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("/health/activities")
	suspend fun createActivity(
		@Header("Authorization") token: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@POST("/health/workouts")
	suspend fun createWorkout(
		@Header("Authorization") token: String,
		@Body dto: WorkoutDTO
	): Response<WorkoutResponse>

	@POST("/health/sleep")
	suspend fun createSleep(
		@Header("Authorization") token: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@POST("/auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@PATCH("/health/foods/{uid}")
	suspend fun updateFood(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: FoodDTO
	): Response<FoodResponse>

	@PATCH("/health/diets/{uid}")
	suspend fun updateDiets(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: DietDTO
	): Response<DietResponse>

	@PATCH("/health/waters/{uid}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@PATCH("/health/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@PATCH("/health/activities/{uid}")
	suspend fun updateActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: ActivityDTO
	): Response<ActivityResponse>

	@PATCH("/health/workouts/{uid}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WorkoutUpdateDTO
	): Response<WorkoutResponse>

	@PATCH("/health/sleep/{uid}")
	suspend fun updateSleep(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: SleepDTO
	): Response<SleepResponse>

	@DELETE("/users/{uid}")
	suspend fun deleteUser(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>

	@DELETE("/health/activities/{uid}")
	suspend fun deleteActivity(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>
}