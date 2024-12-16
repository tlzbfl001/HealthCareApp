package kr.bodywell.android.util

import android.util.Log
import kotlinx.coroutines.delay
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constants.GOOGLE
import kr.bodywell.android.model.Constants.KAKAO
import kr.bodywell.android.model.Constants.NAVER
import kr.bodywell.android.model.Token
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.getUser
import java.time.Duration
import java.time.LocalDateTime

object ViewModelUtil {
	var requestStatus = true

	suspend fun refreshToken(dataManager: DataManager) {
		val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
		val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

		if (accessDiff.toHours() in 1..335) {
			val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
			if(response.isSuccessful) {
				Log.d(TAG, "refreshToken: ${response.body()!!.accessToken}")
				dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
				getToken = dataManager.getToken()
			}else {
				Log.e(TAG, "refreshToken: $response")
			}
		}

		if(refreshDiff.toHours() >= 336) {
			when(getUser.type) {
				GOOGLE -> {
					val response = RetrofitAPI.api.loginWithGoogle(LoginDTO(getUser.idToken))
					if(response.isSuccessful) {
						Log.d(TAG, "googleLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					}else {
						Log.e(TAG, "googleLogin: $response")
					}
				}
				NAVER -> {
					val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(getUser.accessToken))
					if(response.isSuccessful) {
						Log.d(TAG, "naverLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					}else {
						Log.e(TAG, "naverLogin: $response")
					}
				}
				KAKAO -> {
					val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(getUser.accessToken, getUser.idToken))
					if(response.isSuccessful) {
						Log.d(TAG, "kakaoLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					}else {
						Log.e(TAG, "kakaoLogin: $response")
					}
				}
			}
		}

		delay(10000)
	}
}