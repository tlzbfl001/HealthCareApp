package kr.bodywell.android.util

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.database.DBHelper.Companion.UPDATED_AT
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constants.PREFERENCE
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import java.time.LocalDateTime
import java.util.Calendar

object RegisterUtil {
	suspend fun googleLoginRequest(context: LoginActivity, user: User) {
		CoroutineScope(Dispatchers.IO).launch {
			val loginWithGoogle = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
			if(loginWithGoogle.isSuccessful) {
				val access = loginWithGoogle.body()!!.accessToken
				val refresh = loginWithGoogle.body()!!.refreshToken

				val getUser = RetrofitAPI.api.getUser("Bearer $access")
				if(getUser.isSuccessful) {
					val newUser = User(type = user.type, idToken = user.idToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
					val intent = Intent(context, SignupActivity::class.java)
					intent.putExtra("user", newUser)
					intent.putExtra("token", Token(access = access, refresh = refresh))
					context.startActivity(intent)
				}else {
					Log.e(TAG, "getUser: $getUser")
					Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
				}
			}else {
				Log.e(TAG, "loginWithGoogle: $loginWithGoogle")
				Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun naverLoginRequest(context: LoginActivity, user: User) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
			if(response.isSuccessful) {
				val access = response.body()!!.accessToken
				val refresh = response.body()!!.refreshToken

				val getUser = RetrofitAPI.api.getUser("Bearer $access")
				if(getUser.isSuccessful) {
					val newUser = User(type = user.type, accessToken = user.accessToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
					val intent = Intent(context, SignupActivity::class.java)
					intent.putExtra("user", newUser)
					intent.putExtra("token", Token(access = access, refresh = refresh))
					context.startActivity(intent)
				}else {
					Log.e(TAG, "getUser: $getUser")
					Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
				}
			}else {
				Log.e(TAG, "loginWithNaver: $response")
				Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun kakaoLoginRequest(context: LoginActivity, user: User) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
			if(response.isSuccessful) {
				val access = response.body()!!.accessToken
				val refresh = response.body()!!.refreshToken

				val getUser = RetrofitAPI.api.getUser("Bearer $access")
				if(getUser.isSuccessful) {
					val newUser = User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
					val intent = Intent(context, SignupActivity::class.java)
					intent.putExtra("user", newUser)
					intent.putExtra("token", Token(access = access, refresh = refresh))
					context.startActivity(intent)
				}else {
					Log.e(TAG, "getUser: $getUser")
					Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
				}
			}else {
				Log.e(TAG, "loginWithKakao: $response")
				Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun saveData(context: SignupActivity, user: User, token: Token) {
		val dataManager = DataManager(context)
		dataManager.open()

		var getDevice = RetrofitAPI.api.getDevice("Bearer ${token.access}")
		if(getDevice.body()!!.isEmpty()) {
			val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
			val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
			val hardwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
			val softwareVer = if(context.packageManager.getPackageInfo(context.packageName, 0).versionName == null || context.packageManager.getPackageInfo(context.packageName, 0).versionName == "") {
				"" } else context.packageManager.getPackageInfo(context.packageName, 0).versionName
			RetrofitAPI.api.createDevice("Bearer ${token.access}", DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer))
		}

		getDevice = RetrofitAPI.api.getDevice("Bearer ${token.access}")
		Log.d(TAG, "getDevice: ${getDevice.isSuccessful}/${getDevice.body()}")

		if(getDevice.isSuccessful && getDevice.body()!![0].id != "") {
			var getUser = dataManager.getUser(user.type, user.email)

			// 사용자 정보 저장
			if(getUser.email == "") {
				dataManager.insertUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}else {
				dataManager.updateUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}

			// 사용자ID 저장
			getUser = dataManager.getUser(user.type, user.email)
			MyApp.prefs.setUserId(PREFERENCE, getUser.id)

			// 토큰 정보 저장
			val getToken = dataManager.getToken()
			if(getToken.accessCreated == "") {
				dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			// 파일 정보 저장
			val getFileUpdated = dataManager.getUpdatedAt()
			val isoFormat = dateTimeToIso(Calendar.getInstance())
			if(getFileUpdated == "") dataManager.insertUpdatedAt(isoFormat) else dataManager.updateData(UPDATED_AT, isoFormat)

			val intent = Intent(context, MainActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
			context.startActivity(intent)
		}
	}
}