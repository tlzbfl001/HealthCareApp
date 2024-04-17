package com.makebodywell.bodywell.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
	@GET("/users/{uid}")
	fun getUser(
		@Path("uid") uid: String
	): Call<UserResponse>

	@POST("/devices")
	fun createDevice(
		@Header("Authorization") apiKey: String,
		@Body dto: Dto
	): Call<Dto>
}