package kr.bodywell.health.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAPI {
//	private const val SERVER_URL = "https://api.app.bodywell.dev/" // development
	private const val SERVER_URL = "https://api.app.bodywell.kr/" // production

	private val builder by lazy {
		Retrofit.Builder()
			.baseUrl(SERVER_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	val api: APIService by lazy { builder.create(APIService::class.java)}
}
