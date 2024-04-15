package com.makebodywell.bodywell.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiObject {
	private const val BASE_URL = "https://api.bodywell.dev/auth/"

	private val getRetrofit by lazy {
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	val api: ApiInterface by lazy { getRetrofit.create(ApiInterface::class.java)}
}