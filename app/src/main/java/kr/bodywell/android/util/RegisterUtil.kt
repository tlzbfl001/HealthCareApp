package kr.bodywell.android.util

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Base64
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
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Drug
import kr.bodywell.android.model.DrugCheck
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Image
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.isoToDateTime
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermissions
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.init.InputActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object RegisterUtil {
	suspend fun googleLoginRequest(ctx: LoginActivity, dataManager: DataManager, user: User) {
		val getEmailResponse = RetrofitAPI.api.getUserEmail(user.email) // 이미 가입한 이메일인지 확인

		if(getEmailResponse.isSuccessful) {
			Log.d(TAG, "getUserEmail: ${getEmailResponse.body()}")
			if(getEmailResponse.body()!!.exists) { // 이미 가입한 이메일인 경우 서버에서 데이터 가져옴
				ctx.runOnUiThread {
					AlertDialog.Builder(ctx, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val loginResponse = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
								if(loginResponse.isSuccessful) {
									Log.d(TAG, "loginWithGoogle: ${loginResponse.body()}")
									getData(ctx, dataManager, user, loginResponse.body()!!.accessToken, loginResponse.body()!!.refreshToken) // 서버데이터 가져오기
								}else {
									Log.e(TAG, "loginWithGoogle: $loginResponse")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			}else { // 가입하지않은 이메일일 경우 새로 회원가입
				val intent = Intent(ctx, SignupActivity::class.java)
				intent.putExtra("user", user)
				ctx.startActivity(intent)
			}
		}else {
			Log.e(TAG, "getUserEmail: $getEmailResponse")
		}
	}

	suspend fun naverLoginRequest(ctx: LoginActivity, dataManager: DataManager, user: User) {
		val getEmailResponse = RetrofitAPI.api.getUserEmail(user.email)

		if(getEmailResponse.isSuccessful) {
			Log.d(TAG, "getUserEmail: ${getEmailResponse.body()}")
			if (getEmailResponse.body()!!.exists) {
				ctx.runOnUiThread {
					AlertDialog.Builder(ctx, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val loginResponse = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
								if (loginResponse.isSuccessful) {
									Log.d(TAG, "loginWithNaver: ${loginResponse.body()}")
									getData(ctx, dataManager, user, loginResponse.body()!!.accessToken, loginResponse.body()!!.refreshToken)
								} else {
									Log.e(TAG, "loginWithNaver: $loginResponse")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			} else {
				val intent = Intent(ctx, SignupActivity::class.java)
				intent.putExtra("user", user)
				ctx.startActivity(intent)
			}
		}
	}

	suspend fun kakaoLoginRequest(ctx: LoginActivity, dataManager: DataManager, user: User) {
		val getEmailResponse = RetrofitAPI.api.getUserEmail(user.email)

		if(getEmailResponse.isSuccessful) {
			Log.d(TAG, "getUserEmail: ${getEmailResponse.body()}")
			if (getEmailResponse.body()!!.exists) {
				ctx.runOnUiThread {
					AlertDialog.Builder(ctx, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val loginResponse = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
								if (loginResponse.isSuccessful) {
									Log.d(TAG, "loginWithKakao: ${loginResponse.body()}")
									getData(ctx, dataManager, user, loginResponse.body()!!.accessToken, loginResponse.body()!!.refreshToken)
								} else {
									Log.e(TAG, "loginWithKakao: $loginResponse")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			} else {
				val intent = Intent(ctx, SignupActivity::class.java)
				intent.putExtra("user", user)
				ctx.startActivity(intent)
			}
		}
	}

	suspend fun googleSignupRequest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		val loginResponse = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))

		if(loginResponse.isSuccessful) {
			Log.d(TAG, "loginWithGoogle: ${loginResponse.body()}")
			saveData(ctx, dataManager, user, Token(access = loginResponse.body()!!.accessToken, refresh = loginResponse.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithGoogle: $loginResponse")
			ctx.runOnUiThread {
				Toast.makeText(ctx, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun naverSignupRequest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		val loginResponse = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))

		if(loginResponse.isSuccessful) {
			Log.d(TAG, "loginWithNaver: ${loginResponse.body()}")
			saveData(ctx, dataManager, user, Token(access = loginResponse.body()!!.accessToken, refresh = loginResponse.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithNaver: $loginResponse")
			ctx.runOnUiThread {
				Toast.makeText(ctx, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun kakaoSignupRequest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		val loginResponse = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))

		if(loginResponse.isSuccessful) {
			Log.d(TAG, "loginWithKakao: ${loginResponse.body()}")
			saveData(ctx, dataManager, user, Token(access = loginResponse.body()!!.accessToken, refresh = loginResponse.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithKakao: $loginResponse")
			ctx.runOnUiThread {
				Toast.makeText(ctx, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private suspend fun getData(ctx: LoginActivity, dataManager: DataManager, user: User, access: String, refresh: String) {
		val getUserUid = RetrofitAPI.api.getUser("Bearer $access")
		val getProfile = RetrofitAPI.api.getProfile("Bearer $access")
		val getAllFood = RetrofitAPI.api.getAllFood("Bearer $access")
		val getAllDiet = RetrofitAPI.api.getAllDiet("Bearer $access")
		val getAllWater = RetrofitAPI.api.getAllWater("Bearer $access")
		val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer $access")
		val getAllWorkout = RetrofitAPI.api.getAllWorkout("Bearer $access")
		val getAllBody = RetrofitAPI.api.getAllBody("Bearer $access")
		val getAllSleep = RetrofitAPI.api.getAllSleep("Bearer $access")
		val getMedicine = RetrofitAPI.api.getMedicine("Bearer $access")
		val getAllGoal = RetrofitAPI.api.getAllGoal("Bearer $access")

		if(getUserUid.isSuccessful && getProfile.isSuccessful && getAllFood.isSuccessful && getAllDiet.isSuccessful && getAllWater.isSuccessful && getAllActivity.isSuccessful &&
			getAllWorkout.isSuccessful && getAllBody.isSuccessful && getAllSleep.isSuccessful && getMedicine.isSuccessful && getAllGoal.isSuccessful) {
			Log.d(TAG, "getUserUid: ${getUserUid.body()}")
			Log.d(TAG, "getAllFood: ${getAllFood.body()}")
			Log.d(TAG, "getAllDiet: ${getAllDiet.body()}")
			Log.d(TAG, "getAllWater: ${getAllWater.body()}")
			Log.d(TAG, "getAllActivity: ${getAllActivity.body()}")
			Log.d(TAG, "getAllWorkout: ${getAllWorkout.body()}")
			Log.d(TAG, "getAllBody: ${getAllBody.body()}")
			Log.d(TAG, "getAllSleep: ${getAllSleep.body()}")
			Log.d(TAG, "getMedicine: ${getMedicine.body()}")
			Log.d(TAG, "getGoal: ${getAllGoal.body()}")

			var getUser = dataManager.getUser(user.type, user.email)
			val getToken = dataManager.getToken()
			val gender = if(getProfile.body()!!.gender == null) Constant.Female.name else getProfile.body()!!.gender
			val birthday = if(getProfile.body()!!.birth == null) LocalDate.now().toString() else getProfile.body()!!.birth
			val height = if(getProfile.body()!!.height == null) 0.0 else getProfile.body()!!.height!!.toDouble()
			val weight = if(getProfile.body()!!.weight == null) 0.0 else getProfile.body()!!.weight!!.toDouble()

			// 사용자 정보 저장
			if(getUser.createdAt == "") {
				dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, accessToken = user.accessToken, uid=getUserUid.body()!!.uid,
					name = getProfile.body()!!.name, gender = gender, birthday = birthday, height = height, weight = weight, createdAt = getProfile.body()!!.createdAt.substring(0, 10)))
			}else {
				dataManager.updateUser2(User(type = user.type, email = user.email, idToken = user.idToken, accessToken = user.accessToken, uid=getUserUid.body()!!.uid,
					name = getProfile.body()!!.name,	gender = gender, birthday = birthday, height = height, weight = weight, createdAt = getProfile.body()!!.createdAt.substring(0, 10)))
			}

			getUser = dataManager.getUser(user.type, user.email)
			MyApp.prefs.setUserId(Constant.USER_PREFS.name, getUser.id)

			// 토큰 정보 저장
			if(getToken.accessCreated == "") {
				dataManager.insertToken(Token(access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			// 서버 데이터 저장
			for(i in 0 until getAllFood.body()!!.size) {
				var useCount = 0
				var useDate = ""

				if(getAllFood.body()!![i].usages!!.size > 0) {
					useCount = getAllFood.body()!![i].usages!![0].usageCount
					useDate = isoToDateTime(getAllFood.body()!![i].usages!![0].updatedAt).toString()
				}

				val type = if(getAllFood.body()!![i].registerType == Constant.Admin.name) 1 else 0
				dataManager.insertFood(Food(admin = type, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].name, unit = getAllFood.body()!![i].quantityUnit,
					amount = getAllFood.body()!![i].quantity, kcal = getAllFood.body()!![i].calorie, carbohydrate = getAllFood.body()!![i].carbohydrate,
					protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useCount = useCount, useDate = useDate))
			}

			for(i in 0 until getAllDiet.body()!!.size) {
				dataManager.insertDailyFood(Food(uid = getAllDiet.body()!![i].uid, type = getAllDiet.body()!![i].mealTime, name = getAllDiet.body()!![i].name,
					unit = getAllDiet.body()!![i].volumeUnit, amount = getAllDiet.body()!![i].volume, kcal = getAllDiet.body()!![i].calorie,
					carbohydrate = getAllDiet.body()!![i].carbohydrate, protein = getAllDiet.body()!![i].protein, fat = getAllDiet.body()!![i].fat,
					count = getAllDiet.body()!![i].quantity, createdAt = getAllDiet.body()!![i].date.substring(0, 10)))

				val getFood = dataManager.getFood("name", getAllDiet.body()!![i].name)
				if(getAllDiet.body()!![i].photos.size > 0 && getFood.id > 0) {
					for(j in 0 until getAllDiet.body()!![i].photos.size) {
						dataManager.insertImage(Image(type = getAllDiet.body()!![i].mealTime, dataId = getFood.id, imageUri = getAllDiet.body()!![i].photos[j],
							createdAt = getAllDiet.body()!![i].date.substring(0, 10)))
					}
				}
			}

			for(i in 0 until getAllWater.body()!!.size) {
				dataManager.insertWater(Water(uid = getAllWater.body()!![i].uid, count = getAllWater.body()!![i].count, volume = getAllWater.body()!![i].mL,
					createdAt = getAllWater.body()!![i].date))
			}

			for(i in 0 until getAllActivity.body()!!.size) {
				var useCount = 0
				var useDate = ""

				if(getAllActivity.body()!![i].usages!!.size > 0) {
					useCount = getAllActivity.body()!![i].usages!![0].usageCount
					useDate = isoToDateTime(getAllActivity.body()!![i].usages!![0].updatedAt).toString()
				}

				val type = if(getAllActivity.body()!![i].registerType == Constant.Admin.name) 1 else 0
				dataManager.insertExercise(Exercise(admin = type, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name, useCount = useCount, useDate = useDate))
			}

			for(i in 0 until getAllWorkout.body()!!.size) {
				dataManager.insertDailyExercise(Exercise(uid = getAllWorkout.body()!![i].uid, name = getAllWorkout.body()!![i].name, intensity = getAllWorkout.body()!![i].intensity,
					workoutTime = getAllWorkout.body()!![i].time, kcal = getAllWorkout.body()!![i].calorie, createdAt = getAllWorkout.body()!![i].date.substring(0, 10)))
			}

			for(i in 0 until getAllBody.body()!!.size) {
				dataManager.insertBody(Body(uid = getAllBody.body()!![i].uid, height = getAllBody.body()!![i].height, weight = getAllBody.body()!![i].weight,
					intensity = getAllBody.body()!![i].workoutIntensity, fat = getAllBody.body()!![i].bodyFatPercentage, muscle = getAllBody.body()!![i].skeletalMuscleMass,
					bmi = getAllBody.body()!![i].bodyMassIndex, bmr = getAllBody.body()!![i].basalMetabolicRate, createdAt = getAllBody.body()!![i].time.substring(0, 10)))
			}

			for(i in 0 until getAllSleep.body()!!.size) {
				val isoToStartTime = isoToDateTime(getAllSleep.body()!![i].starts)
				val isoToEndTime = isoToDateTime(getAllSleep.body()!![i].ends)
				dataManager.insertSleep(Sleep(uid = getAllSleep.body()!![i].uid, startTime = isoToStartTime.toString(), endTime = isoToEndTime.toString()))
			}

			val alarmReceiver = AlarmReceiver()
			val timeList = ArrayList<DrugTime>()

			for(i in 0 until getMedicine.body()!!.size) {
				val startDate = LocalDate.parse(getMedicine.body()!![i].starts.substring(0, 10))
				val endDate = LocalDate.parse(getMedicine.body()!![i].ends.substring(0, 10))
				val count = startDate.until(endDate, ChronoUnit.DAYS) + 1

				val drug = Drug(uid = getMedicine.body()!![i].uid, type = getMedicine.body()!![i].category, name = getMedicine.body()!![i].name,
					amount = getMedicine.body()!![i].amount, unit = getMedicine.body()!![i].unit, count = count.toInt(),
					startDate = startDate.toString(), endDate = endDate.toString())

				val getMedicineTime = RetrofitAPI.api.getMedicineTime("Bearer $access", drug.uid)
				if(getMedicineTime.isSuccessful) {
					Log.d(TAG, "getMedicineTime: ${getMedicineTime.body()}")

					dataManager.insertDrug(drug) // drug 데이터 저장
					val drugId = dataManager.getData(DRUG, "startDate", drug.startDate) // drug id 가져오기

					for(j in 0 until getMedicineTime.body()!!.size) {
						val drugTime = DrugTime(uid = getMedicineTime.body()!![j].uid, drugId = drugId.id, time = getMedicineTime.body()!![j].time)
						dataManager.insertDrugTime(drugTime)

						timeList.add(DrugTime(time = drugTime.time))

						val getMedicineIntake = RetrofitAPI.api.getMedicineIntake("Bearer $access", drug.uid, drugTime.uid)
						if(getMedicineIntake.isSuccessful) {
							Log.d(TAG, "getMedicineIntake: ${getMedicineIntake.body()}")

							val drugTimeId = dataManager.getData(DRUG_TIME, "uid", drugTime.uid)
							for(k in 0 until getMedicineIntake.body()!!.size) {
								dataManager.insertDrugCheck(DrugCheck(uid = getMedicineIntake.body()!![k].uid, drugId = drugId.id, drugTimeId = drugTimeId.id,
									time = drugTime.time, createdAt = getMedicineIntake.body()!![k].intakeAt.substring(0, 10)))
							}
						}else {
							Log.e(TAG, "getMedicineIntake: $getMedicineIntake")
						}
					}

					if(!checkAlarmPermissions(ctx)) { // 알람 등록
						alarmReceiver.setAlarm(ctx, drugId.id, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}")
					}
				}else {
					Log.e(TAG, "getMedicineTime: $getMedicineTime")
				}
			}

			for(i in 0 until getAllGoal.body()!!.size) {
				dataManager.insertGoal(Goal(uid = getAllGoal.body()!![i].uid, food = getAllGoal.body()!![i].kcalOfDiet, waterVolume=getAllGoal.body()!![i].waterAmountOfCup,
					water=getAllGoal.body()!![i].waterIntake, exercise=getAllGoal.body()!![i].kcalOfWorkout, body=getAllGoal.body()!![i].weight,
					sleep=getAllGoal.body()!![i].sleep, drug=getAllGoal.body()!![i].medicineIntake, createdAt = getAllGoal.body()!![i].date.substring(0, 10)))
			}

			val getSync = dataManager.getSynced()
			if(getSync == "") dataManager.insertSync(LocalDateTime.now().toString()) else dataManager.updateSync(LocalDateTime.now().toString())

			ctx.startActivity(Intent(ctx, MainActivity::class.java))
		}else {
			Toast.makeText(ctx, "로그인 실패", Toast.LENGTH_SHORT).show()
		}
	}

	private fun saveData(ctx: SignupActivity, dataManager: DataManager, user: User, token: Token) {
		var getUser = dataManager.getUser(user.type, user.email)

		// 사용자 정보 저장
		if(getUser.createdAt == "") {
			dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, accessToken = user.accessToken, createdAt = LocalDate.now().toString(), isUpdated = 1))
		}else {
			dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, accessToken = user.accessToken, createdAt = LocalDate.now().toString(), isUpdated = 1))
		}

		getUser = dataManager.getUser(user.type, user.email)

		CoroutineScope(Dispatchers.IO).launch {
			val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
			val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
			val hardwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
			val softwareVer = if(ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName == null || ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName == "") {
				"" }else ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
			val data = DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer)
			val getUserUid = RetrofitAPI.api.getUser("Bearer ${token.access}")
			val createDevice = RetrofitAPI.api.createDevice("Bearer ${token.access}", data)
			val getAllFood = RetrofitAPI.api.getAllFood("Bearer ${token.access}")
			val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer ${token.access}")
			val getGoal = RetrofitAPI.api.getAllGoal("Bearer ${token.access}")

			Log.d(TAG, "createDevice: ${createDevice.isSuccessful}/${createDevice.body()}")
			Log.d(TAG, "getAllFood: ${getAllFood.isSuccessful}/${getAllFood.body()}")
			Log.d(TAG, "getAllActivity: ${getAllActivity.isSuccessful}/${getAllActivity.body()}")
			Log.d(TAG, "getGoal: ${getGoal.isSuccessful}/${getGoal.body()}")

			if(getUserUid.isSuccessful && createDevice.isSuccessful && getAllFood.isSuccessful && getAllActivity.isSuccessful && getGoal.isSuccessful) {
				MyApp.prefs.setUserId(Constant.USER_PREFS.name, getUser.id) // 사용자 Id 저장
				dataManager.updateUserStr("uid", getUserUid.body()!!.uid)

				// 토큰 정보 저장
				val getToken = dataManager.getToken()
				if(getToken.accessCreated == "") {
					dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}else {
					dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}

				// 서버 데이터 저장
				for(i in 0 until getAllFood.body()!!.size) {
					dataManager.insertFood(Food(admin = 1, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].name, unit = getAllFood.body()!![i].volumeUnit,
						amount = getAllFood.body()!![i].volume, kcal = getAllFood.body()!![i].calorie, carbohydrate = getAllFood.body()!![i].carbohydrate,
						protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString())
					)
				}

				for(i in 0 until getAllActivity.body()!!.size) {
					dataManager.insertExercise(Exercise(admin = 1, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name, intensity = Constant.HIGH.name,
						useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString()))
				}

				for(i in 0 until getAllActivity.body()!!.size) {
					dataManager.insertGoal(Goal(uid = getGoal.body()!![i].uid, createdAt = LocalDate.now().toString()))
				}

				val getSync = dataManager.getSynced()
				if(getSync == "") dataManager.insertSync(LocalDateTime.now().toString()) else dataManager.updateSync(LocalDateTime.now().toString())

				ctx.runOnUiThread{
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
		}
	}

	fun registerTest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		val getUser = dataManager.getUser(user.type, user.email)

		// 사용자 정보 저장
		if(getUser.createdAt == "") {
			dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, createdAt = LocalDate.now().toString()))
		}else {
			dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, createdAt = LocalDate.now().toString()))
		}

		val getUser2 = dataManager.getUser(user.type, user.email)

		MyApp.prefs.setUserId(Constant.USER_PREFS.name, getUser2.id) // 사용자 Id 저장

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

	private fun decodeToken(token: String): String {
		val decodeData = String(Base64.decode(token.split(".")[1], Base64.URL_SAFE), charset("UTF-8"))
		val obj = JSONObject(decodeData)
		return obj.get("sub").toString()
	}
}