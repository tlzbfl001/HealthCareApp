package kr.bodywell.android.service

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
	@GET("/devices/{uid}")
	fun getDevice(
		@Header("Authorization") apiKey: String,
		@Path("uid") uid: String
	): Call<DeviceResponse>

	@GET("/health/bodies/{uid}")
	fun getBody(
		@Header("Authorization") apiKey: String,
		@Path("uid") uid: String
	): Call<BodyResponse>

	@FormUrlEncoded
	@POST("/auth/google/login")
	fun googleLogin(
		@Field("idToken") idToken: String
	): Call<TokenResponse>

	@FormUrlEncoded
	@POST("/devices")
	fun createDevice(
		@Header("Authorization") apiKey: String,
		@Field("label") label: String,
		@Field("name") name: String,
		@Field("manufacturer") manufacturer: String,
		@Field("model") model: String,
		@Field("hardwareVersion") hardwareVersion: String,
		@Field("softwareVersion") softwareVersion: String
	): Call<DeviceResponse>

	@FormUrlEncoded
	@POST("/health/bodies")
	fun createBody(
		@Header("Authorization") apiKey: String,
		@Field("height") height: Double,
		@Field("weight") weight: Double,
		@Field("bodyFatPercentage") bodyFatPercentage: Double,
		@Field("skeletalMuscleMass") skeletalMuscleMass: Double,
		@Field("bodyMassIndex") bodyMassIndex: Double,
		@Field("basalMetabolicRate") basalMetabolicRate: Double,
		@Field("workoutIntensity") workoutIntensity: Int,
		@Field("time") time: String
	): Call<BodyResponse>

	@FormUrlEncoded
	@POST("/health/bodies/{uid}")
	fun updateBody(
		@Header("Authorization") apiKey: String,
		@Path("uid") uid: String,
		@Field("birth") birth: String,
		@Field("gender") gender: String,
		@Field("height") height: Double,
		@Field("weight") weight: Double,
		@Field("bodyFatPercentage") bodyFatPercentage: Double,
		@Field("skeletalMuscleMass") skeletalMuscleMass: Double,
		@Field("bodyMassIndex") bodyMassIndex: Double,
		@Field("basalMetabolicRate") basalMetabolicRate: Double,
		@Field("workoutIntensity") workoutIntensity: Int,
		@Field("time") time: String
	): Call<BodyResponse>

	@DELETE("/users/{uid}")
	fun deleteUser(
		@Header("Authorization") apiKey: String,
		@Path("uid") uid: String
	): Call<Void>
}