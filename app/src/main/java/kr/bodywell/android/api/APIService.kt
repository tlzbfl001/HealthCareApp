package kr.bodywell.android.api

import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.response.BodyResponse
import kr.bodywell.android.api.response.DeviceResponse
import kr.bodywell.android.api.response.SleepResponse
import kr.bodywell.android.api.response.TokenResponse
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
	@GET("/devices/{uid}")
	suspend fun getDevice(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<DeviceResponse>

	@GET("/health/bodies/{uid}")
	suspend fun getBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<BodyResponse>

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

	@POST("/health/bodies")
	suspend fun createBody(
		@Header("Authorization") token: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@POST("/health/waters")
	suspend fun createWater(
		@Header("Authorization") token: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

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

	@PATCH("/health/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: BodyDTO
	): Response<BodyResponse>

	@PATCH("/health/waters/{uid}")
	suspend fun updateWater(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WaterDTO
	): Response<WaterResponse>

	@PATCH("/health/workouts/{uid}")
	suspend fun updateWorkout(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Body dto: WorkoutDTO
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
}