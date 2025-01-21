package kr.bodywell.android.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAPI {
//	private const val BASE_URL = "https://api.app.bodywell.dev/" // development
	private const val BASE_URL = "https://api.app.bodywell.kr/" // production

	private val getRetrofit by lazy {
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	val api: APIService by lazy { getRetrofit.create(APIService::class.java)}
}