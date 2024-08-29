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
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.TYPE_ADMIN
import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Drug
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
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.init.InputActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object RegisterUtil {
	suspend fun googleLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
		val getUserEmail = RetrofitAPI.api.getUserEmail(user.email) // 이미 가입한 이메일인지 확인
		if(getUserEmail.isSuccessful) {
			if(getUserEmail.body()!!.exists) { // 이미 가입한 이메일인 경우 서버에서 데이터 가져옴
				context.runOnUiThread {
					AlertDialog.Builder(context, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val response = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
								if(response.isSuccessful) {
									getData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken)) // 서버데이터 가져오기
								}else {
									Log.e(TAG, "loginWithGoogle: $response")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			}else { // 가입하지않은 이메일일 경우 새로 회원가입
				val intent = Intent(context, SignupActivity::class.java)
				intent.putExtra("user", user)
				context.startActivity(intent)
			}
		}else {
			Log.e(TAG, "getUserEmail: $getUserEmail")
		}
	}

	suspend fun naverLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
		val getUserEmail = RetrofitAPI.api.getUserEmail(user.email)
		if(getUserEmail.isSuccessful) {
			if (getUserEmail.body()!!.exists) {
				context.runOnUiThread {
					AlertDialog.Builder(context, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
								if (response.isSuccessful) {
									getData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
								} else {
									Log.e(TAG, "loginWithNaver: $response")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			} else {
				val intent = Intent(context, SignupActivity::class.java)
				intent.putExtra("user", user)
				context.startActivity(intent)
			}
		}
	}

	suspend fun kakaoLoginRequest(context: LoginActivity, dataManager: DataManager, user: User) {
		val getUserEmail = RetrofitAPI.api.getUserEmail(user.email)
		if(getUserEmail.isSuccessful) {
			if (getUserEmail.body()!!.exists) {
				context.runOnUiThread {
					AlertDialog.Builder(context, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
								if (response.isSuccessful) {
									getData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
								} else {
									Log.e(TAG, "loginWithKakao: $response")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			} else {
				val intent = Intent(context, SignupActivity::class.java)
				intent.putExtra("user", user)
				context.startActivity(intent)
			}
		}
	}

	suspend fun googleSignupRequest(context: SignupActivity, dataManager: DataManager, user: User) {
		val response = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
		if(response.isSuccessful) {
			saveData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithGoogle: $response")
			context.runOnUiThread {
				Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun naverSignupRequest(context: SignupActivity, dataManager: DataManager, user: User) {
		val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
		if(response.isSuccessful) {
			saveData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithNaver: $response")
			context.runOnUiThread {
				Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	suspend fun kakaoSignupRequest(context: SignupActivity, dataManager: DataManager, user: User) {
		val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(user.accessToken, user.idToken))
		if(response.isSuccessful) {
			saveData(context, dataManager, user, Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken))
		}else {
			Log.e(TAG, "loginWithKakao: $response")
			context.runOnUiThread {
				Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private suspend fun getData(context: LoginActivity, dataManager: DataManager, user: User, token: Token) {
		val getUserUid = RetrofitAPI.api.getUser("Bearer ${token.access}")
		val getProfile = RetrofitAPI.api.getProfile("Bearer ${token.access}")
		val getAllFood = RetrofitAPI.api.getAllFood("Bearer ${token.access}")
		val getAllDiet = RetrofitAPI.api.getAllDiet("Bearer ${token.access}")
		val getAllWater = RetrofitAPI.api.getAllWater("Bearer ${token.access}")
		val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer ${token.access}")
		val getAllWorkout = RetrofitAPI.api.getAllWorkout("Bearer ${token.access}")
		val getAllBody = RetrofitAPI.api.getAllBody("Bearer ${token.access}")
		val getAllSleep = RetrofitAPI.api.getAllSleep("Bearer ${token.access}")
		val getMedicine = RetrofitAPI.api.getAllMedicine("Bearer ${token.access}")
		val getAllGoal = RetrofitAPI.api.getAllGoal("Bearer ${token.access}")

		Log.d(TAG, "getUserUid: ${getUserUid.isSuccessful} / ${getUserUid.body()}")
		Log.d(TAG, "getProfile: ${getProfile.isSuccessful} / ${getProfile.body()}")
		Log.d(TAG, "getAllFood: ${getAllFood.isSuccessful} / ${getAllFood.body()}")
		Log.d(TAG, "getAllDiet: ${getAllDiet.isSuccessful} / ${getAllDiet.body()}")
		Log.d(TAG, "getAllWater: ${getAllWater.isSuccessful} / ${getAllWater.body()}")
		Log.d(TAG, "getAllActivity: ${getAllActivity.isSuccessful} / ${getAllActivity.body()}")
		Log.d(TAG, "getAllWorkout: ${getAllWorkout.isSuccessful} / ${getAllWorkout.body()}")
		Log.d(TAG, "getAllBody: ${getAllBody.isSuccessful} / ${getAllBody.body()}")
		Log.d(TAG, "getAllSleep: ${getAllSleep.isSuccessful} / ${getAllSleep.body()}")
		Log.d(TAG, "getMedicine: ${getMedicine.isSuccessful} / ${getMedicine.body()}")
		Log.d(TAG, "getAllGoal: ${getAllGoal.isSuccessful} / ${getAllGoal.body()}")

		if(getUserUid.isSuccessful && getProfile.isSuccessful && getAllFood.isSuccessful && getAllDiet.isSuccessful && getAllWater.isSuccessful && getAllActivity.isSuccessful &&
			getAllWorkout.isSuccessful && getAllBody.isSuccessful && getAllSleep.isSuccessful && getMedicine.isSuccessful && getAllGoal.isSuccessful) {

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
			MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser.id)

			// 토큰 정보 저장
			if(getToken.accessCreated == "") {
				dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			// 서버 데이터 저장
			for(i in 0 until getAllFood.body()!!.size) {
				var useCount = 0
				var useDate = ""

				if(getAllFood.body()!![i].usages != null) { // usages가 empty인것도 있기때문에 useCount, useDate 값없을수있음.
					useCount = getAllFood.body()!![i].usages!![0].usageCount
					useDate = isoToDateTime(getAllFood.body()!![i].usages!![0].updatedAt).toString()
				}

				val type = if(getAllFood.body()!![i].registerType == TYPE_ADMIN) TYPE_ADMIN else TYPE_USER
				dataManager.insertFood(Food(registerType = type, uid = getAllFood.body()!![i].id, name = getAllFood.body()!![i].name, unit = getAllFood.body()!![i].quantityUnit,
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
						dataManager.insertImage(Image(type = getAllDiet.body()!![i].mealTime, imageUri = getAllDiet.body()!![i].photos[j],
							createdAt = getAllDiet.body()!![i].date.substring(0, 10)))
					}
				}
			}

			for(i in 0 until getAllWater.body()!!.size) {
				dataManager.insertWater(Water(uid = getAllWater.body()!![i].id, count = getAllWater.body()!![i].count, volume = getAllWater.body()!![i].mL,
					createdAt = getAllWater.body()!![i].date))
			}

			for(i in 0 until getAllActivity.body()!!.size) {
				var useCount = 0
				var useDate = ""

				if(getAllActivity.body()!![i].usages != null) {
					useCount = getAllActivity.body()!![i].usages!![0].usageCount
					useDate = isoToDateTime(getAllActivity.body()!![i].usages!![0].updatedAt).toString()
				}

				val type = if(getAllActivity.body()!![i].registerType == TYPE_ADMIN) TYPE_ADMIN else TYPE_USER
				dataManager.insertExercise(Exercise(registerType = type, uid = getAllActivity.body()!![i].id, name = getAllActivity.body()!![i].name, useCount = useCount, useDate = useDate))
			}

			for(i in 0 until getAllWorkout.body()!!.size) {
				dataManager.insertDailyExercise(Exercise(uid = getAllWorkout.body()!![i].id, name = getAllWorkout.body()!![i].name, intensity = getAllWorkout.body()!![i].intensity,
					workoutTime = getAllWorkout.body()!![i].time, kcal = getAllWorkout.body()!![i].calorie, createdAt = getAllWorkout.body()!![i].date.substring(0, 10)))
			}

			for(i in 0 until getAllBody.body()!!.size) {
				dataManager.insertBody(Body(uid = getAllBody.body()!![i].id, height = getAllBody.body()!![i].height, weight = getAllBody.body()!![i].weight,
					intensity = getAllBody.body()!![i].workoutIntensity, fat = getAllBody.body()!![i].bodyFatPercentage, muscle = getAllBody.body()!![i].skeletalMuscleMass,
					bmi = getAllBody.body()!![i].bodyMassIndex, bmr = getAllBody.body()!![i].basalMetabolicRate, createdAt = getAllBody.body()!![i].time.substring(0, 10)))
			}

			for(i in 0 until getAllSleep.body()!!.size) {
				val isoToStartTime = isoToDateTime(getAllSleep.body()!![i].starts)
				val isoToEndTime = isoToDateTime(getAllSleep.body()!![i].ends)
				dataManager.insertSleep(Sleep(uid = getAllSleep.body()!![i].id, startTime = isoToStartTime.toString(), endTime = isoToEndTime.toString()))
			}

			val alarmReceiver = AlarmReceiver()
			val timeList = ArrayList<DrugTime>()

			for(i in 0 until getMedicine.body()!!.size) {
				val startDate = LocalDate.parse(getMedicine.body()!![i].starts.substring(0, 10))
				val endDate = LocalDate.parse(getMedicine.body()!![i].ends.substring(0, 10))
				val count = startDate.until(endDate, ChronoUnit.DAYS) + 1

				val drug = Drug(uid = getMedicine.body()!![i].id, type = getMedicine.body()!![i].category, name = getMedicine.body()!![i].name,
					amount = getMedicine.body()!![i].amount, unit = getMedicine.body()!![i].unit, count = count.toInt(),
					startDate = startDate.toString(), endDate = endDate.toString())

				val getMedicineTime = RetrofitAPI.api.getAllMedicineTime("Bearer ${token.access}", drug.uid)
				if(getMedicineTime.isSuccessful) {
					dataManager.insertDrug(drug) // drug 데이터 저장
					val drugId = dataManager.getData(DRUG, "startDate", drug.startDate) // drug id 가져오기

					for(j in 0 until getMedicineTime.body()!!.size) {
						val drugTime = DrugTime(uid = getMedicineTime.body()!![j].id, drugId = drugId.id, time = getMedicineTime.body()!![j].time)
						dataManager.insertDrugTime(drugTime)
						timeList.add(DrugTime(time = drugTime.time))
					}

					alarmReceiver.setAlarm(context, drugId.id, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}")
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

			context.startActivity(Intent(context, MainActivity::class.java))
		}else {
			context.runOnUiThread {
				Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun saveData(context: SignupActivity, dataManager: DataManager, user: User, token: Token) {
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
			val softwareVer = if(context.packageManager.getPackageInfo(context.packageName, 0).versionName == null
				|| context.packageManager.getPackageInfo(context.packageName, 0).versionName == "") {
				""
			}else context.packageManager.getPackageInfo(context.packageName, 0).versionName

			val getUserUid = RetrofitAPI.api.getUser("Bearer ${token.access}")
			val createDevice = RetrofitAPI.api.createDevice("Bearer ${token.access}", DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer))
			val getAllFood = RetrofitAPI.api.getAllFood("Bearer ${token.access}")
			val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer ${token.access}")

			Log.d(TAG, "getUserUid: ${getUserUid.body()}")
			Log.d(TAG, "createDevice: ${createDevice.body()}")
			Log.d(TAG, "getAllFood: ${getAllFood.body()}")
			Log.d(TAG, "getAllActivity: ${getAllActivity.body()}")

			if(getUserUid.isSuccessful && createDevice.isSuccessful && getAllFood.isSuccessful && getAllActivity.isSuccessful) {
				MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser.id) // 사용자 Id 저장

				// 토큰 정보 저장
				val getToken = dataManager.getToken()
				if(getToken.accessCreated == "") {
					dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}else {
					dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}

				// 서버 데이터 저장
				for(i in 0 until getAllFood.body()!!.size) {
					dataManager.insertFood(Food(registerType = TYPE_ADMIN, uid = getAllFood.body()!![i].id, name = getAllFood.body()!![i].name, unit = getAllFood.body()!![i].volumeUnit,
						amount = getAllFood.body()!![i].volume, kcal = getAllFood.body()!![i].calorie, carbohydrate = getAllFood.body()!![i].carbohydrate,
						protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString())
					)
				}

				for(i in 0 until getAllActivity.body()!!.size) {
					dataManager.insertExercise(Exercise(registerType = TYPE_ADMIN, uid = getAllActivity.body()!![i].id, name = getAllActivity.body()!![i].name, intensity = Constant.HIGH.name,
						useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString()))
				}

				dataManager.updateUserStr(USER, "uid", getUserUid.body()!!.uid, "id")

				val getSync = dataManager.getSynced()
				if(getSync == "") dataManager.insertSync(LocalDateTime.now().toString()) else dataManager.updateSync(LocalDateTime.now().toString())

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

		MyApp.prefs.setUserId(Constant.USER_PREFERENCE.name, getUser2.id) // 사용자 Id 저장

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