package kr.bodywell.android.util

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Medicine
import kr.bodywell.android.model.MedicineIntake
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.init.InputActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import java.time.LocalDateTime

object RegisterUtil {
	suspend fun googleLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
		CoroutineScope(Dispatchers.IO).launch {
			val loginWithGoogle = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
			if(loginWithGoogle.isSuccessful) {
				val access = loginWithGoogle.body()!!.accessToken
				val refresh = loginWithGoogle.body()!!.refreshToken

				val getUser = RetrofitAPI.api.getUser("Bearer $access")
				if(getUser.isSuccessful) {
					val newUser = User(type = user.type, idToken = user.idToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)

					val response = RetrofitAPI.api.getUsername(access, getUser.body()!!.username)
					if(response.isSuccessful) {
						if(response.body()!!) {
							context.runOnUiThread {
								AlertDialog.Builder(context, R.style.AlertDialogStyle).setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?").setPositiveButton("확인") { _, _ ->
									CoroutineScope(Dispatchers.IO).launch {
										getData(context, dataManager, newUser, Token(access = access, refresh = refresh))
									}
								}.setNegativeButton("취소", null).create().show()
							}
						}else {
							val intent = Intent(context, SignupActivity::class.java)
							intent.putExtra("user", newUser)
							intent.putExtra("token", Token(access = access, refresh = refresh))
							context.startActivity(intent)
						}
					}else Log.e(TAG, "getUsername: $response")
				}else Log.e(TAG, "getUserAPI: $getUser")
			}else Log.e(TAG, "loginWithGoogle: $loginWithGoogle")
		}
	}

	suspend fun naverLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
//		val getUsername = RetrofitAPI.api.getUsername(user.email)
//		if(getUsername.isSuccessful) {
//			if(getUsername.body()!!) {
//				context.runOnUiThread {
//					AlertDialog.Builder(context, R.style.AlertDialogStyle)
//						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
//						.setPositiveButton("확인") { _, _ ->
//							CoroutineScope(Dispatchers.IO).launch {
//								val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
//								if(response.isSuccessful) {
//									getData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
//								}else Log.e(TAG, "loginWithNaver: $response")
//							}
//						}.setNegativeButton("취소", null).create().show()
//				}
//			}else {
//				val intent = Intent(context, SignupActivity::class.java)
//				intent.putExtra("user", user)
//				context.startActivity(intent)
//			}
//		}
	}

	suspend fun kakaoLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
//		val getUsername = RetrofitAPI.api.getUsername(user.email)
//		if(getUsername.isSuccessful) {
//			if (getUsername.body()!!) {
//				context.runOnUiThread {
//					AlertDialog.Builder(context, R.style.AlertDialogStyle)
//						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
//						.setPositiveButton("확인") { _, _ ->
//							CoroutineScope(Dispatchers.IO).launch {
//								val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
//								if(response.isSuccessful) {
//									getData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
//								}else Log.e(TAG, "loginWithKakao: $response")
//							}
//						}.setNegativeButton("취소", null).create().show()
//				}
//			}else {
//				val intent = Intent(context, SignupActivity::class.java)
//				intent.putExtra("user", user)
//				context.startActivity(intent)
//			}
//		}
	}

	suspend fun googleSignupRequest(context: SignupActivity, user: User, token: Token) {
		saveData(context, user, token)
	}

	suspend fun naverSignupRequest(context: SignupActivity, user: User) {
		val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
		if(response.isSuccessful) {
			saveData(context, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithNaver: $response")
			context.runOnUiThread {
				Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun kakaoSignupRequest(context: SignupActivity, user: User) {
		val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
		if(response.isSuccessful) {
			saveData(context, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithKakao: $response")
			context.runOnUiThread {
				Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private suspend fun getData(context: LoginActivity, dataManager: DataManager, user: User, token: Token) {
		val getMedicine = RetrofitAPI.api.getAllMedicine("Bearer ${token.access}")
		Log.d(TAG, "getMedicine: ${getMedicine.isSuccessful}/${getMedicine.body()}")

		if(getMedicine.isSuccessful) {
			var getUser = dataManager.getUser(user.type, user.email)
			val getToken = dataManager.getToken()

			// 사용자 정보 저장
			if(getUser.email == "") {
				dataManager.insertUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}else {
				dataManager.updateUser2(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}

			// 사용자ID 저장
			getUser = dataManager.getUser(user.type, user.email)
			MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser.id)

			// 토큰 정보 저장
			if(getToken.access == "") {
				dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			// 약복용 정보 저장
			val alarmReceiver = AlarmReceiver()
			val timeList = ArrayList<MedicineTime>()

			for(i in 0 until getMedicine.body()!!.size) {
				val split = getMedicine.body()!![i].name.split("/", limit = 4)
				val medicine = Medicine(id = getMedicine.body()!![i].id, category = getMedicine.body()!![i].category, name = split[1], amount = getMedicine.body()!![i].amount,
					unit = getMedicine.body()!![i].unit, starts = getMedicine.body()!![i].starts.substring(0, 10), ends = getMedicine.body()!![i].ends.substring(0, 10))
				dataManager.insertMedicine(medicine.id, medicine.category.toInt())

				val getMedicineTime = RetrofitAPI.api.getAllMedicineTime("Bearer ${token.access}", medicine.id)
				if(getMedicineTime.isSuccessful) {
					Log.d(TAG, "getMedicineTime: ${getMedicineTime.body()}")
					for(j in 0 until getMedicineTime.body()!!.size) {
						val medicineTimeId = getMedicineTime.body()!![j].id
						timeList.add(MedicineTime(time = getMedicineTime.body()!![j].time))
						dataManager.insertMedicineTime(MedicineTime(id = medicineTimeId, medicineId = medicine.id))

						val getMedicineIntake = RetrofitAPI.api.getMedicineIntake("Bearer ${token.access}", medicine.id, medicineTimeId)
						if(getMedicineIntake.isSuccessful) {
							Log.d(TAG, "getMedicineIntake: ${getMedicineIntake.body()}")
							for(k in 0 until getMedicineIntake.body()!!.size) {
								dataManager.insertMedicineIntake(MedicineIntake(id = getMedicineIntake.body()!![k].id, medicineId = medicine.id, medicineTimeId = medicineTimeId))
							}
						}else Log.e(TAG, "getMedicineIntake: $getMedicineIntake")
					}

					alarmReceiver.setAlarm(context, medicine.category.toInt(), medicine.starts, medicine.ends, timeList, "${medicine.name} ${medicine.amount}${medicine.unit}")
				}else Log.e(TAG, "getMedicineTime: $getMedicineTime")
			}

			context.startActivity(Intent(context, MainActivity::class.java))
		}else {
			context.runOnUiThread {
				Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun saveData(context: SignupActivity, user: User, token: Token) {
		val dataManager = DataManager(context)

		val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
		val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
		val hardwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
		val softwareVer = if(context.packageManager.getPackageInfo(context.packageName, 0).versionName == null || context.packageManager.getPackageInfo(context.packageName, 0).versionName == "") {
			"" } else context.packageManager.getPackageInfo(context.packageName, 0).versionName

		val createDevice = RetrofitAPI.api.createDevice("Bearer ${token.access}", DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer))
		val getDevice = RetrofitAPI.api.getDevice("Bearer ${token.access}")

		Log.d(TAG, "createDevice: ${createDevice.isSuccessful}/${createDevice.body()}")
		Log.d(TAG, "getDevice: ${getDevice.isSuccessful}/${getDevice.body()}")

		if(getDevice.isSuccessful && getDevice.body()!![0].id != "") {
			var getUser = dataManager.getUser(user.type, user.email)
			val getToken = dataManager.getToken()

			// 사용자 정보 저장
			if(getUser.email == "") {
				dataManager.insertUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}else {
				dataManager.updateUser2(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
			}

			// 사용자ID 저장
			getUser = dataManager.getUser(user.type, user.email)
			MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser.id)

			// 토큰 정보 저장
			if(getToken.accessCreated == "") {
				dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			context.runOnUiThread{
				val dialog = Dialog(context)
				dialog.setContentView(R.layout.dialog_signup)
				dialog.setCancelable(false)
				dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
				val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

				btnConfirm.setOnClickListener {
					val intent = Intent(context, InputActivity::class.java)
					intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
					context.startActivity(intent)
					dialog.dismiss()
				}

				dialog.show()
			}
		}
	}

	fun registerTest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		var getUser = dataManager.getUser(user.type, user.email)

		// 사용자 정보 저장
		if(getUser.email == "") {
			dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken))
		}else {
			dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken))
		}

		getUser = dataManager.getUser(user.type, user.email)

		MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser.id) // 사용자 Id 저장

		val dialog = Dialog(ctx)
		dialog.setContentView(R.layout.dialog_signup)
		dialog.setCancelable(false)
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

		btnConfirm.setOnClickListener {
			val intent = Intent(ctx, InputActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
			ctx.startActivity(intent)
			dialog.dismiss()
		}

		dialog.show()
	}
}