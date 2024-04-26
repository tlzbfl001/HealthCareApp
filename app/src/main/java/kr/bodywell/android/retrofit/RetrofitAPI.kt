package kr.bodywell.android.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAPI {
	private const val BASE_URL = "https://api.bodywell.dev/"

	private val getRetrofit by lazy {
		Retrofit.Builder()
			.baseUrl(BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	val api: APIService by lazy { getRetrofit.create(APIService::class.java)}
}