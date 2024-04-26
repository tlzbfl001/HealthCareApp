package kr.bodywell.android.retrofit

import kr.bodywell.android.retrofit.dto.BodyDto
import kr.bodywell.android.retrofit.response.BodyResponse
import kr.bodywell.android.retrofit.response.DeviceResponse
import kr.bodywell.android.retrofit.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
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
		@Body dto: BodyDto
	): Response<BodyResponse>

	@FormUrlEncoded
	@POST("/health/bodies/{uid}")
	suspend fun updateBody(
		@Header("Authorization") token: String,
		@Path("uid") uid: String,
		@Field("height") height: Double,
		@Field("weight") weight: Double,
		@Field("bodyFatPercentage") bodyFatPercentage: Double,
		@Field("skeletalMuscleMass") skeletalMuscleMass: Double,
		@Field("bodyMassIndex") bodyMassIndex: Double,
		@Field("basalMetabolicRate") basalMetabolicRate: Double,
		@Field("workoutIntensity") workoutIntensity: Int,
		@Field("time") time: String
	): Response<BodyResponse>

	@POST("/auth/refresh-token")
	suspend fun refreshToken(
		@Header("Authorization") token: String
	): Response<TokenResponse>

	@DELETE("/users/{uid}")
	suspend fun deleteUser(
		@Header("Authorization") token: String,
		@Path("uid") uid: String
	): Response<Void>
}