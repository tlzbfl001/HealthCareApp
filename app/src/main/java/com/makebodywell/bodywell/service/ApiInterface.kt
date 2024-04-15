package com.makebodywell.bodywell.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
	@GET("/users/{uid}")
	fun getUser(
		@Path("uid") uid:String
	): Call<UserResponse>
}