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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
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
import kr.bodywell.android.service.MyApp
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.isoFormatter
import kr.bodywell.android.util.CustomUtil.Companion.isoToDateTime
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.init.InputActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime

object RegisterUtil {
	suspend fun googleLoginRequest(ctx: LoginActivity, dataManager: DataManager, task: Task<GoogleSignInAccount>) {
		val response = RetrofitAPI.api.getUserEmail(task.result.email!!)
		if(response.isSuccessful) {
			Log.d(TAG, "getUserEmail: ${response.body()}")
			if(response.body()!!.exists) {
				ctx.runOnUiThread {
					AlertDialog.Builder(ctx, R.style.AlertDialogStyle)
						.setTitle("회원가입").setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							CoroutineScope(Dispatchers.IO).launch {
								val loginDTO = LoginDTO(task.result.idToken!!)
								val response2 = RetrofitAPI.api.loginWithGoogle(loginDTO)
								if(response2.isSuccessful) {
									Log.d(TAG, "loginWithGoogle: ${response2.body()}")
									val user = User(type = "google", email = task.result.email!!, idToken = task.result.idToken!!)
									getData(ctx, dataManager, user, response2.body()!!.accessToken, response2.body()!!.refreshToken) // 서버데이터 가져오기
								}else {
									Log.e(TAG, "loginWithGoogle: $response2")
								}
							}
						}.setNegativeButton("취소", null).create().show()
				}
			}else {
				val intent = Intent(ctx, SignupActivity::class.java)
				intent.putExtra("user", User(type = "google", email = task.result.email!!, idToken = task.result.idToken!!))
				ctx.startActivity(intent)
			}
		}else {
			Log.e(TAG, "getUserEmail: $response")
		}
	}

	private suspend fun getData(ctx: LoginActivity, dataManager: DataManager, user: User, access: String, refresh: String) {
		val getProfile = RetrofitAPI.api.getProfile("Bearer $access")

		if(getProfile.isSuccessful) {
			var getUser = dataManager.getUser(user.type, user.email)
			val getToken = dataManager.getToken()
			val gender = if(getProfile.body()!!.gender == null) "Female" else getProfile.body()!!.gender
			val birthday = if(getProfile.body()!!.birth == null) LocalDate.now().toString() else getProfile.body()!!.birth
			val height = if(getProfile.body()!!.height == null) 0.0 else getProfile.body()!!.height!!.toDouble()
			val weight = if(getProfile.body()!!.weight == null) 0.0 else getProfile.body()!!.weight!!.toDouble()

			// 사용자 정보 저장
			if(getUser.createdAt == "") {
				dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, name = getProfile.body()!!.name, gender = gender, birthday = birthday,
					height = height, weight = weight, createdAt = getProfile.body()!!.createdAt.substring(0, 10)))
			}else {
				dataManager.updateUser2(User(type = user.type, email = user.email, idToken = user.idToken, name = getProfile.body()!!.name, gender = gender, birthday = birthday,
					height = height, weight = weight, createdAt = getProfile.body()!!.createdAt.substring(0, 10)))
			}

			getUser = dataManager.getUser(user.type, user.email)

			MyApp.prefs.setUserId("userId", getUser.id)

			// 토큰 정보 저장
			if(getToken.accessCreated == "") {
				dataManager.insertToken(Token(userId = getUser.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}else {
				dataManager.updateToken(Token(userId = getUser.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
			}

			// 서버 데이터 저장
			val getAllFood = RetrofitAPI.api.getAllFood("Bearer $access", "")
			if(getAllFood.isSuccessful) {
				Log.d(TAG, "getAllFood: ${getAllFood.body()}")

				for(i in 0 until getAllFood.body()!!.size) {
					var useCount = 0
					var useDate = ""

					if(getAllFood.body()!![i].usages!!.size > 0) {
						useCount = getAllFood.body()!![i].usages!![0].usageCount
						useDate = isoToDateTime(getAllFood.body()!![i].usages!![0].updatedAt).toString()
					}

					if(getAllFood.body()!![i].registerType == "Admin") {
						dataManager.insertFood(Food(userId = getUser.id, admin = 1, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
							unit = getAllFood.body()!![i].quantityUnit, amount = getAllFood.body()!![i].quantity, kcal = getAllFood.body()!![i].calories,
							protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useCount = useCount, useDate = useDate))
					}else {
						dataManager.insertFood(Food(userId = getUser.id, admin = 0, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
							unit = getAllFood.body()!![i].quantityUnit, amount = getAllFood.body()!![i].quantity, kcal = getAllFood.body()!![i].calories,
							protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useCount = useCount, useDate = useDate))
					}
				}
			}else {
				Log.e(TAG, "getAllFood: $getAllFood")
			}

			val getAllDiet = RetrofitAPI.api.getAllDiet("Bearer $access")
			if(getAllDiet.isSuccessful) {
				Log.d(TAG, "getAllDiet: ${getAllDiet.body()}")

				for(i in 0 until getAllDiet.body()!!.size) {
					dataManager.insertDailyFood(Food(userId = getUser.id, uid = getAllDiet.body()!![i].uid, type = getAllDiet.body()!![i].mealTime,
						name = getAllDiet.body()!![i].foodName, unit = getAllDiet.body()!![i].volumeUnit, amount = getAllDiet.body()!![i].volume,
						kcal = getAllDiet.body()!![i].calories, carbohydrate = getAllDiet.body()!![i].carbohydrate, protein = getAllDiet.body()!![i].protein,
						fat = getAllDiet.body()!![i].fat, count = getAllDiet.body()!![i].quantity, createdAt = getAllDiet.body()!![i].date.substring(0, 10)))

					val getFood = dataManager.getFood("name", getAllDiet.body()!![i].foodName)
					if(getAllDiet.body()!![i].photos.size > 0 && getFood.id > 0) {
						for(j in 0 until getAllDiet.body()!![i].photos.size) {
							dataManager.insertImage(Image(userId = getUser.id, type = getAllDiet.body()!![i].mealTime, dataId = getFood.id,
								imageUri = getAllDiet.body()!![i].photos[j], createdAt = getAllDiet.body()!![i].date.substring(0, 10)))
						}
					}
				}
			}else {
				Log.e(TAG, "getAllDiet: $getAllDiet")
			}

			val getAllWater = RetrofitAPI.api.getAllWater("Bearer $access")
			if(getAllWater.isSuccessful) {
				Log.d(TAG, "getAllWater: ${getAllWater.body()}")

				for(i in 0 until getAllWater.body()!!.size) {
					dataManager.insertWater(Water(userId = getUser.id, uid = getAllWater.body()!![i].uid, count = getAllWater.body()!![i].count,
						volume = getAllWater.body()!![i].mL, createdAt = getAllWater.body()!![i].date))
				}
			}else {
				Log.e(TAG, "getAllWater: $getAllWater")
			}

			val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer $access")
			if(getAllActivity.isSuccessful) {
				Log.d(TAG, "getAllActivity: ${getAllActivity.body()}")

				for(i in 0 until getAllActivity.body()!!.size) {
					var useCount = 0
					var useDate = ""

					if(getAllActivity.body()!![i].usages!!.size > 0) {
						useCount = getAllActivity.body()!![i].usages!![0].usageCount
						useDate = isoToDateTime(getAllActivity.body()!![i].usages!![0].updatedAt).toString()
					}

					if(getAllActivity.body()!![i].registerType == "Admin") {
						dataManager.insertExercise(Exercise(userId = getUser.id, admin = 1, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name,
							useCount = useCount, useDate = useDate))
					}else {
						dataManager.insertExercise(Exercise(userId = getUser.id, admin = 0, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name,
							useCount = useCount, useDate = useDate))
					}
				}
			}else {
				Log.e(TAG, "getAllActivity: $getAllActivity")
			}

			val getAllWorkout = RetrofitAPI.api.getAllWorkout("Bearer $access")
			if(getAllWorkout.isSuccessful) {
				Log.d(TAG, "getAllWorkout: ${getAllWorkout.body()}")

				for(i in 0 until getAllWorkout.body()!!.size) {
					dataManager.insertDailyExercise(Exercise(uid = getAllWorkout.body()!![i].uid, name = getAllWorkout.body()!![i].name,
						intensity = getAllWorkout.body()!![i].intensity, workoutTime = getAllWorkout.body()!![i].time,
						kcal = getAllWorkout.body()!![i].calories, createdAt = getAllWorkout.body()!![i].date.substring(0, 10)))
				}
			}else {
				Log.e(TAG, "getAllWorkout: $getAllWorkout")
			}

			val getAllBody = RetrofitAPI.api.getAllBody("Bearer $access")
			if(getAllBody.isSuccessful) {
				Log.d(TAG, "getAllBody: ${getAllBody.body()}")

				for(i in 0 until getAllBody.body()!!.size) {
					dataManager.insertBody(Body(userId = getUser.id, uid = getAllBody.body()!![i].uid, height = getAllBody.body()!![i].height, weight = getAllBody.body()!![i].weight,
						intensity = getAllBody.body()!![i].workoutIntensity, fat = getAllBody.body()!![i].bodyFatPercentage, muscle = getAllBody.body()!![i].skeletalMuscleMass,
						bmi = getAllBody.body()!![i].bodyMassIndex, bmr = getAllBody.body()!![i].basalMetabolicRate, createdAt = getAllBody.body()!![i].createdAt!!.substring(0, 10)))
				}
			}else {
				Log.e(TAG, "getAllBody: $getAllBody")
			}

			val getAllSleep = RetrofitAPI.api.getAllSleep("Bearer $access")
			if(getAllSleep.isSuccessful) {
				Log.d(TAG, "getAllSleep: ${getAllSleep.body()}")

				for(i in 0 until getAllSleep.body()!!.size) {
					dataManager.insertSleep(Sleep(uid = getAllSleep.body()!![i].uid, startTime = getAllSleep.body()!![i].starts, endTime = getAllSleep.body()!![i].ends,
						createdAt = getAllSleep.body()!![i].starts.substring(0, 10)))
				}
			}else {
				Log.e(TAG, "getAllSleep: $getAllSleep")
			}

			val getMedicine = RetrofitAPI.api.getMedicine("Bearer $access")
			if(getMedicine.isSuccessful) {
				Log.d(TAG, "getMedicine: ${getMedicine.body()}")

				for(i in 0 until getMedicine.body()!!.size) {
					val drug = Drug(uid = getMedicine.body()!![i].uid, type = getMedicine.body()!![i].category,
						name = getMedicine.body()!![i].name, amount = getMedicine.body()!![i].amount, unit = getMedicine.body()!![i].unit,
						startDate = getMedicine.body()!![i].starts.substring(0, 10), endDate = getMedicine.body()!![i].ends.substring(0, 10))

					dataManager.insertDrug(drug)

					val response1 = RetrofitAPI.api.getMedicineTime("Bearer $access", drug.uid)
					if(response1.isSuccessful) {
						Log.d(TAG, "getMedicineTime: ${response1.body()}")

						val drugId = dataManager.getDrugId(DBHelper.DRUG, "startDate", drug.startDate)
						for(j in 0 until response1.body()!!.size) {
							val drugTime = DrugTime(uid = response1.body()!![j].uid, drugId = drugId, time = response1.body()!![j].time)
							dataManager.insertDrugTime(drugTime)

							val response2 = RetrofitAPI.api.getMedicineIntake("Bearer $access", drug.uid, drugTime.uid)
							if(response2.isSuccessful) {
								Log.d(TAG, "getMedicineIntake: ${response2.body()}")

								val drugTimeId = dataManager.getDrugId(DBHelper.DRUG_TIME, "uid", drugTime.uid)
								for(k in 0 until response2.body()!!.size) {
									dataManager.insertDrugCheck(DrugCheck(uid = response2.body()!![k].uid, drugId = drugTimeId, drugTimeId = 2,
										checkedAt = response2.body()!![k].intakeAt.substring(0, 10)))
								}
							}else {
								Log.e(TAG, "getMedicineIntake: $response2")
							}
						}
					}else {
						Log.e(TAG, "getMedicineTime: $response1")
					}
				}
			}else {
				Log.e(TAG, "getMedicine: $getMedicine")
			}

			val getGoal = RetrofitAPI.api.getGoal("Bearer $access")
			if(getGoal.isSuccessful) {
				Log.d(TAG, "getGoal: ${getGoal.body()}")
				dataManager.insertGoal(Goal(uid = getGoal.body()!!.uid, createdAt = getGoal.body()!!.date.substring(0, 10)))
			}else {
				Log.e(TAG, "getGoal: $getGoal")
			}

			val getSync = dataManager.getSynced()
			if(getSync == "") dataManager.insertSync(LocalDateTime.now().toString()) else dataManager.updateSync(LocalDateTime.now().toString())

			ctx.startActivity(Intent(ctx, MainActivity::class.java))
		}else {
			Toast.makeText(ctx, "로그인 실패", Toast.LENGTH_SHORT).show()
		}
	}

	suspend fun googleSignupRequest(ctx: SignupActivity, dataManager: DataManager, user: User) {
		val data = LoginDTO(user.idToken)
		val googleLogin = RetrofitAPI.api.loginWithGoogle(data)

		if(googleLogin.isSuccessful) {
			Log.d(TAG, "googleLogin: ${googleLogin.body()}")
			val token = Token(access = googleLogin.body()!!.accessToken, refresh = googleLogin.body()!!.refreshToken)
			saveData(ctx, dataManager, user, token)
		}else {
			Log.e(TAG, "googleLogin: $googleLogin")
			ctx.runOnUiThread {
				Toast.makeText(ctx, "회원가입 실패", Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun saveData(ctx: SignupActivity, dataManager: DataManager, user: User, token: Token) {
		var getUser = dataManager.getUser(user.type, user.email)
		val updated = LocalDateTime.now().format(isoFormatter)

		// 사용자 정보 저장
		if(getUser.createdAt == "") {
			dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, createdAt = LocalDate.now().toString(), isUpdated = 1))
		}else {
			dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, createdAt = LocalDate.now().toString(), isUpdated = 1))
		}

		getUser = dataManager.getUser(user.type, user.email)

		CoroutineScope(Dispatchers.IO).launch {
			val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
			val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
			val hardwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
			val softwareVer = if(ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName == null || ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName == "") {
				"" }else ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
			val data = DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer)
			val createDevice = RetrofitAPI.api.createDevice("Bearer ${token.access}", data)
//         val getDevice = RetrofitAPI.api.getDevice("Bearer $access")
			val getAllFood = RetrofitAPI.api.getAllFood("Bearer ${token.access}", "")
			val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer ${token.access}")
			val getGoal = RetrofitAPI.api.getGoal("Bearer ${token.access}")

			Log.d(TAG, "createDevice: ${createDevice.isSuccessful}/${createDevice.body()}")
			Log.d(TAG, "getAllFood: ${getAllFood.isSuccessful}/${getAllFood.body()}")
			Log.d(TAG, "getAllActivity: ${getAllActivity.isSuccessful}/${getAllActivity.body()}")
			Log.d(TAG, "getGoal: ${getGoal.isSuccessful}/${getGoal.body()}")

			if(createDevice.isSuccessful && getAllFood.isSuccessful && getAllActivity.isSuccessful && getGoal.isSuccessful) {
				MyApp.prefs.setUserId("userId", getUser.id) // 사용자 Id 저장

				// 토큰 정보 저장
				val getToken = dataManager.getToken()
				if(getToken.accessCreated == "") {
					dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}else {
					dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
				}

				// 서버 데이터 저장
				for(i in 0 until getAllFood.body()!!.size) {
					dataManager.insertFood(Food(admin = 1, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
						unit = getAllFood.body()!![i].volumeUnit, amount = getAllFood.body()!![i].volume, kcal = getAllFood.body()!![i].calories,
						carbohydrate = getAllFood.body()!![i].carbohydrate, protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat,
						useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString())
					)
				}

				for(i in 0 until getAllActivity.body()!!.size) {
					dataManager.insertExercise(Exercise(admin = 1, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name, intensity = "HIGH",
						useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
						createdAt = LocalDate.now().toString()))
				}

				dataManager.insertGoal(Goal(uid = getGoal.body()!!.uid, createdAt = LocalDate.now().toString()))

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

		MyApp.prefs.setUserId("userId", getUser2.id) // 사용자 Id 저장

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