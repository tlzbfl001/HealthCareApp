package kr.bodywell.android.api

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodUpdateDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.GoalUpdateDTO
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DBHelper.Companion.BODY
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
import kr.bodywell.android.database.DBHelper.Companion.DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE_TIME
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DBHelper.Companion.SYNC_TIME
import kr.bodywell.android.database.DBHelper.Companion.UNUSED
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.FoodInit
import kr.bodywell.android.model.GoalInit
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.InitWater
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.dateToIso
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.getUser
import kr.bodywell.android.util.CustomUtil.isoToDateTime
import java.time.Duration
import java.time.LocalDateTime

object ViewModelUtil {
	var requestStatus = true

	suspend fun createRequest(context: Context, dataManager: DataManager) {
		refreshToken(dataManager)
		createSync(context, dataManager)

		val getUnused = dataManager.getUnused()
		val getUserUpdated = dataManager.getUserUpdated()
		val getFoodUid = dataManager.getFoodUid()
		val getFoodUpdated = dataManager.getFoodUpdated()
		val getDailyFoodUid = dataManager.getDailyFoodUid()
		val getDailyFoodUpdated = dataManager.getDailyFoodUpdated()
		val getWaterUid = dataManager.getWaterUid()
		val getWaterUpdated = dataManager.getWaterUpdated()
		val getExUid = dataManager.getExerciseUid()
		val getExUpdated = dataManager.getExerciseUpdated()
		val getDailyExUid = dataManager.getDailyExerciseUid()
		val getDailyExUpdated = dataManager.getDailyExerciseUpdated()
		val getBodyUid = dataManager.getBodyUid()
		val getBodyUpdated = dataManager.getBodyUpdated()
		val getSleepUid = dataManager.getSleepUid()
		val getSleepUpdated = dataManager.getSleepUpdated()
		val getDrugUid = dataManager.getDrugUid()
		val getDrugUpdated = dataManager.getDrugUpdated()
		val getDrugCheckUid = dataManager.getDrugCheckUid()
		val getImageUid = dataManager.getImageUid()
		val getGoalUid = dataManager.getGoalUid()
		val getGoalUpdated = dataManager.getGoalUpdated()

		for(i in 0 until getUnused.size) {
			when(getUnused[i].type) {
				FOOD -> {
					val response = RetrofitAPI.api.getFood("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getFood: $response")
					if(response.isSuccessful) {
						val deleteFood = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteFood: $deleteFood")
						if(deleteFood.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}


					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				DAILY_FOOD -> {
					val response = RetrofitAPI.api.getDiet("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getDiet: $response")
					if(response.isSuccessful) {
						val deleteDiet = RetrofitAPI.api.deleteDiet("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteDiet: $deleteDiet")
						if(deleteDiet.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				WATER -> {
					val response = RetrofitAPI.api.getWater("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getWater: $response")
					if(response.isSuccessful) {
						val deleteWater = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteWater: $deleteWater")
						if(deleteWater.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				EXERCISE -> {
					val response = RetrofitAPI.api.getActivity("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getActivity: $response")
					if(response.isSuccessful) {
						val deleteActivity = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteActivity: $deleteActivity")
						if(deleteActivity.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				DAILY_EXERCISE -> {
					val response = RetrofitAPI.api.getWorkout("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getWorkout: $response")
					if(response.isSuccessful) {
						val deleteWorkout = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteWorkout: $deleteWorkout")
						if(deleteWorkout.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				MEDICINE_TIME -> {
					val response = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].value)
					Log.d(TAG, "getMedicineTime: $response")
					if(response.isSuccessful) {
						val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].value)
						Log.d(TAG, "deleteMedicineTime: $deleteMedicineTime")
						if(deleteMedicineTime.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
				DRUG -> {
					val response = RetrofitAPI.api.getMedicine("Bearer ${getToken.access}", getUnused[i].value)
					Log.d(TAG, "getMedicine: $response")
					if(response.isSuccessful) {
						val deleteMedicine = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)
						Log.d(TAG, "deleteMedicine: $deleteMedicine")
						if(deleteMedicine.isSuccessful) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}
				}
			}
		}

		if(getUserUpdated.id > 0) {
			val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", ProfileDTO(getUserUpdated.name!!, getUserUpdated.birthday!!,
				getUserUpdated.gender!!, getUserUpdated.height!!, getUserUpdated.weight!!, "Asia/Seoul"))
			if(response.isSuccessful) {
				Log.d(TAG, "updateProfile: ${response.body()}")
				dataManager.updateUserInt(IS_UPDATED, 0)
			}else {
				Log.e(TAG, "updateProfile: $response")
			}
		}

		/*for(i in 0 until getFoodUid.size) {
			val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", FoodDTO(getFoodUid[i].name, getFoodUid[i].calorie, getFoodUid[i].carbohydrate,
				getFoodUid[i].protein, getFoodUid[i].fat, getFoodUid[i].count, "개", getFoodUid[i].amount, getFoodUid[i].unit))
			if(response.isSuccessful) {
				Log.d(TAG, "createFood: ${response.body()}")
				dataManager.updateStr(FOOD, "uid", response.body()!!.id, "id", getFoodUid[i].id)
			}else {
				Log.e(TAG, "createFood: $response")
			}
		}*/

		for(i in 0 until getFoodUpdated.size) {
			val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated[i].uid, FoodUpdateDTO(getFoodUpdated[i].calorie, getFoodUpdated[i].carbohydrate,
				getFoodUpdated[i].protein, getFoodUpdated[i].fat, getFoodUpdated[i].amount, getFoodUpdated[i].unit))
			if(response.isSuccessful) {
				Log.d(TAG, "updateFood: ${response.body()}")
				dataManager.updateInt(FOOD, IS_UPDATED, 0, "id", getFoodUpdated[i].id)
			}else {
				Log.e(TAG, "updateFood: $response")
			}
		}

		for(i in 0 until getDailyFoodUid.size) {
			val photos = ArrayList<String>()
			photos.add("https://example.com/picture.jpg")
			val getData = dataManager.getData(FOOD, "name", getDailyFoodUid[i].name)
			val foodId = if(getData.uid != "") getData.uid else null
			val dateToIso = dateToIso(getDailyFoodUid[i].createdAt)
			val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", DietDTO(getDailyFoodUid[i].type, getDailyFoodUid[i].name, getDailyFoodUid[i].calorie, getDailyFoodUid[i].carbohydrate,
				getDailyFoodUid[i].protein, getDailyFoodUid[i].fat, getDailyFoodUid[i].count, "개", getDailyFoodUid[i].amount, getDailyFoodUid[i].unit, photos, dateToIso, foodId))

			if(response.isSuccessful) {
				Log.d(TAG, "createDiets: ${response.body()}")
				dataManager.updateStr(DAILY_FOOD, "uid", response.body()!!.id, "id", getDailyFoodUid[i].id)
			}else {
				Log.e(TAG, "createDiets: $response")
			}
		}

		for(i in 0 until getDailyFoodUpdated.size) {
			val photos = ArrayList<String>()
			photos.add("https://example.com/picture.jpg")
			val dateToIso = dateToIso(getDailyFoodUpdated[i].createdAt)
			val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated[i].uid, DietUpdateDTO(getDailyFoodUpdated[i].calorie,
				getDailyFoodUpdated[i].carbohydrate, getDailyFoodUpdated[i].protein, getDailyFoodUpdated[i].fat,
				getDailyFoodUpdated[i].count, getDailyFoodUpdated[i].amount))

			if(response.isSuccessful) {
				Log.d(TAG, "updateDiets: ${response.body()}")
				dataManager.updateInt(DAILY_FOOD, IS_UPDATED, 0, "id", getDailyFoodUpdated[i].id)
			}else {
				Log.e(TAG, "updateDiets: $response")
			}
		}

		/*for(i in 0 until getWaterUid.size) {
			val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", WaterDTO(getWaterUid[i].volume, getWaterUid[i].count, getWaterUid[i].createdAt))
			if(response.isSuccessful) {
				Log.d(TAG, "createWater: ${response.body()}")
				dataManager.updateStr(WATER, "uid", response.body()!!.id, "id", getWaterUid[i].id)
			}else {
				Log.e(TAG, "createWater: $response")
			}
		}

		for(i in 0 until getWaterUpdated.size) {
			val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, WaterDTO(getWaterUpdated[i].volume,
				getWaterUpdated[i].count, getWaterUpdated[i].createdAt))
			if(response.isSuccessful) {
				Log.d(TAG, "updateWater: ${response.body()}")
				dataManager.updateInt(WATER, IS_UPDATED, 0, "id", getWaterUpdated[i].id)
			}else {
				Log.e(TAG, "updateWater: $response")
			}
		}*/

		for(i in 0 until getExUid.size) {
			val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", ActivityDTO(getExUid[i].name))
			if(response.isSuccessful) {
				Log.d(TAG, "createActivity: ${response.body()}")
				dataManager.updateStr(EXERCISE, "uid", response.body()!!.id, "id", getExUid[i].id)
			}else {
				Log.e(TAG, "createActivity: $response")
			}
		}

		for(i in 0 until getExUpdated.size) {
			val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExUpdated[i].uid, ActivityDTO(getExUpdated[i].name))
			if(response.isSuccessful) {
				Log.d(TAG, "updateActivity: ${response.body()}")
				dataManager.updateInt(EXERCISE, IS_UPDATED, 0, "id", getExUpdated[i].id)
			}else {
				Log.e(TAG, "updateActivity: $response")
			}
		}

		for(i in 0 until getDailyExUid.size) {
			val dateToIso = dateToIso(getDailyExUid[i].createdAt)
			val getData = dataManager.getData(EXERCISE, "name", getDailyExUid[i].name)
			val activityId = if(getData.uid != "") getData.uid else null
			val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", WorkoutDTO(getDailyExUid[i].name, getDailyExUid[i].kcal,
				getDailyExUid[i].intensity, getDailyExUid[i].workoutTime, dateToIso, activityId))

			if(response.isSuccessful) {
				Log.d(TAG, "createWorkout: ${response.body()}")
				dataManager.updateStr(DAILY_EXERCISE, "uid", response.body()!!.id, "id", getDailyExUid[i].id)
			}else {
				Log.e(TAG, "createWorkout: $response")
			}
		}

		for(i in 0 until getDailyExUpdated.size) {
			val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExUpdated[i].uid, WorkoutUpdateDTO(getDailyExUpdated[i].kcal,
				getDailyExUpdated[i].intensity, getDailyExUpdated[i].workoutTime))
			if(response.isSuccessful) {
				Log.d(TAG, "updateWorkout: ${response.body()}")
				dataManager.updateInt(DAILY_EXERCISE, IS_UPDATED, 0, "id", getDailyExUpdated[i].id)
			}else {
				Log.e(TAG, "updateWorkout: $response")
			}
		}

		for(i in 0 until getBodyUid.size) {
			val dateToIso = dateToIso(getBodyUid[i].createdAt)
			val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi,
				getBodyUid[i].fat, getBodyUid[i].muscle, getBodyUid[i].bmr, getBodyUid[i].intensity, dateToIso))
			if(response.isSuccessful) {
				Log.d(TAG, "createBody: ${response.body()}")
				dataManager.updateStr(BODY, "uid", response.body()!!.id, "id", getBodyUid[i].id)
			}else {
				Log.e(TAG, "createBody: $response")
			}
		}

		for(i in 0 until getBodyUpdated.size) {
			val dateToIso = dateToIso(getBodyUpdated[i].createdAt)
			val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated[i].uid!!, BodyDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight,
				getBodyUpdated[i].bmi, getBodyUpdated[i].fat, getBodyUpdated[i].muscle, getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, dateToIso))
			if(response.isSuccessful) {
				Log.d(TAG, "updateBody: ${response.body()}")
				dataManager.updateInt(BODY, IS_UPDATED, 0, "id", getBodyUpdated[i].id)
			}else {
				Log.e(TAG, "updateBody: $response")
			}
		}

		for(i in 0 until getSleepUid.size) {
			val startToIso = dateTimeToIso(LocalDateTime.parse(getSleepUid[i].startTime))
			val endToIso = dateTimeToIso(LocalDateTime.parse(getSleepUid[i].endTime))
			val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", SleepDTO(startToIso, endToIso))
			if(response.isSuccessful) {
				Log.d(TAG, "createSleep: ${response.body()}")
				dataManager.updateStr(SLEEP, "uid", response.body()!!.id, "id", getSleepUid[i].id)
			}else {
				Log.e(TAG, "createSleep: $response")
			}
		}

		for(i in 0 until getSleepUpdated.size) {
			val startToIso = dateTimeToIso(LocalDateTime.parse(getSleepUpdated[i].startTime))
			val endToIso = dateTimeToIso(LocalDateTime.parse(getSleepUpdated[i].endTime))
			val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid, SleepDTO(startToIso, endToIso))
			if(response.isSuccessful) {
				Log.d(TAG, "updateSleep: ${response.body()}")
				dataManager.updateInt(SLEEP, IS_UPDATED, 0, "id", getSleepUpdated[i].id)
			}else {
				Log.e(TAG, "updateSleep: $response")
			}
		}

		for(i in 0 until getDrugUid.size) {
			val startDate = dateToIso(getDrugUid[i].startDate)
			val endDate = dateToIso(getDrugUid[i].endDate)
			val createMedicine = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", MedicineDTO(getDrugUid[i].type, getDrugUid[i].name,
				getDrugUid[i].amount, getDrugUid[i].unit, startDate, endDate))

			if(createMedicine.isSuccessful) {
				Log.d(TAG, "createMedicine: ${createMedicine.body()}")
				dataManager.updateStr(DRUG, "uid", createMedicine.body()!!.id, "id", getDrugUid[i].id)
				val getDrugTime = dataManager.getDrugTime(getDrugUid[i].id)
				for(j in 0 until getDrugTime.size) {
					val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", createMedicine.body()!!.id, MedicineTimeDTO(getDrugTime[j].time))
					if(response.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response.body()}")
						dataManager.updateStr(MEDICINE_TIME, "uid", response.body()!!.id, "id", getDrugTime[j].id)
					}else {
						Log.e(TAG, "createMedicineTime: $response")
					}
				}
			}else {
				Log.e(TAG, "createMedicine: $createMedicine")
			}
		}

		for(i in 0 until getDrugUpdated.size) {
			val startDate = dateToIso(getDrugUpdated[i].startDate)
			val endDate = dateToIso(getDrugUpdated[i].endDate)
			val updateMedicine = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getDrugUpdated[i].uid,
				MedicineDTO(getDrugUpdated[i].type, getDrugUpdated[i].name, getDrugUpdated[i].amount, getDrugUpdated[i].unit, startDate, endDate))

			if(updateMedicine.isSuccessful) {
				Log.d(TAG, "updateMedicine: ${updateMedicine.body()}")

				var flag = true
				val getUnusedTime = dataManager.getUnusedTime(getDrugUpdated[i].uid)
				for(j in 0 until getUnusedTime.size) {
					val response = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[i].value)
					Log.d(TAG, "getMedicineTime: $response")
					if(response.isSuccessful) {
						val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[i].value)
						Log.d(TAG, "deleteMedicineTime: $deleteMedicineTime")
						if(deleteMedicineTime.isSuccessful){
							dataManager.deleteItem(UNUSED, "id", getUnusedTime[i].id)
						}
					}else {
						if(response.code() == 404) dataManager.deleteItem(UNUSED, "id", getUnused[i].id) else flag = false
					}
				}

				if(flag) {
					val getDrugTime = dataManager.getDrugTimeData(getDrugUpdated[i].id)
					for(j in 0 until getDrugTime.size) {
						val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, MedicineTimeDTO(getDrugTime[j].time))
						if(response.isSuccessful) {
							Log.d(TAG, "createMedicineTime: ${response.body()}")
							dataManager.updateStr(MEDICINE_TIME, "uid", response.body()!!.id, "id", getDrugTime[j].id)
						}else {
							Log.e(TAG, "createMedicineTime: $response")
						}
					}

					dataManager.updateInt(DRUG, IS_UPDATED, 0, "id", getDrugUpdated[i].id)
				}
			}else {
				Log.e(TAG, "updateMedicine: $updateMedicine")
			}
		}

		/*for(i in 0 until getDrugCheckUid.size) {
			val getDrug = dataManager.getUid(DRUG, getDrugCheckUid[i].drugId)
			val getDrugTime = dataManager.getUid(DRUG_TIME, getDrugCheckUid[i].drugTimeId)
			val getIntake = RetrofitAPI.api.getMedicineIntake("Bearer ${getToken.access}", getDrug.uid, getDrugTime.uid)

			if(getIntake.isSuccessful) {
				dataManager.updateStr(DRUG_CHECK, "uid", getIntake.body()!!.id, "id", getDrugCheckUid[i].id)
			}else {
				if(getIntake.code() == 404) {
					val dateToIso = dateToIso(getDrugCheckUid[i].createdAt)
					val dto = MedicineIntakeDTO(dateToIso, getUser.deviceUid)
					val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", getDrug.uid, getDrugTime.uid, dto)

					if(response.isSuccessful) {
						dataManager.updateStr(DRUG_CHECK, "uid", getIntake.body()!!.id, "id", getDrugCheckUid[i].id)
					}
				}
			}
		}*/

		for(i in 0 until getGoalUid.size) {
			val dateToIso = dateToIso(getGoalUid[i].createdAt)
			val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", GoalDTO(getGoalUid[i].body, getGoalUid[i].food, getGoalUid[i].exercise,
				getGoalUid[i].waterVolume, getGoalUid[i].water, getGoalUid[i].sleep, getGoalUid[i].drug, dateToIso))
			if(response.isSuccessful) {
				Log.d(TAG, "createGoal: ${response.body()}")
				dataManager.updateStr(GOAL, "uid", response.body()!!.id, "id", getGoalUid[i].id)
			}else {
				Log.e(TAG, "createGoal: $response")
			}
		}

		for(i in 0 until getGoalUpdated.size) {
			val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", getGoalUpdated[i].uid, GoalUpdateDTO(getGoalUpdated[i].body, getGoalUpdated[i].food,
				getGoalUpdated[i].exercise, getGoalUpdated[i].waterVolume, getGoalUpdated[i].water, getGoalUpdated[i].sleep, getGoalUpdated[i].drug))
			if(response.isSuccessful) {
				Log.d(TAG, "updateGoal: ${response.body()}")
				dataManager.updateInt(GOAL, IS_UPDATED, 0, "id", getGoalUpdated[i].id)
			}else {
				Log.e(TAG, "updateGoal: $response")
			}
		}

		delay(15000)
	}

	private suspend fun createSync(context: Context, dataManager: DataManager) {
		val syncedAt = dateTimeToIso(LocalDateTime.parse(dataManager.getSynced()))
		val now = LocalDateTime.now().toString()
		Log.d(TAG, "syncedAt: $syncedAt / ${dataManager.getSynced()}")

		val syncProfile = RetrofitAPI.api.syncProfile("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncProfile.isSuccessful) {
			Log.d(TAG, "syncProfile: ${syncProfile.body()}")
			if(syncProfile.body()?.data != null) {
				dataManager.updateProfile(User(name=syncProfile.body()?.data!!.name, gender=syncProfile.body()?.data!!.gender!!, birthday=syncProfile.body()?.data!!.birth!!,
					height=syncProfile.body()?.data!!.height!!, weight=syncProfile.body()?.data!!.weight!!))
			}
		}else {
			Log.e(TAG, "syncProfile: $syncProfile")
			requestStatus = false
		}

		val syncDiets = RetrofitAPI.api.syncDiets("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncDiets.isSuccessful) {
			Log.d(TAG, "syncDiets: ${syncDiets.body()}")

			for(i in 0 until syncDiets.body()?.data!!.size) {
				val date = syncDiets.body()?.data!![i].date.substring(0, 10)
				val getDailyFood = dataManager.getDailyFood(syncDiets.body()?.data!![i].mealTime, syncDiets.body()?.data!![i].name, date)

				if(getDailyFood.id > 0) {
					if(syncDiets.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DAILY_FOOD, "id", getDailyFood.id)
					}else {
						dataManager.updateDailyFood(FoodInit(id = getDailyFood.id, unit = syncDiets.body()?.data!![i].volumeUnit, amount = syncDiets.body()?.data!![i].volume,
							calorie = syncDiets.body()?.data!![i].calorie, carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein,
							fat = syncDiets.body()?.data!![i].fat, count = syncDiets.body()?.data!![i].quantity))
						dataManager.updateStr(DAILY_FOOD, "uid", syncDiets.body()?.data!![i].id, "id", getDailyFood.id)
					}
				}else if(syncDiets.body()?.data!![i].createdAt != null && syncDiets.body()?.data!![i].deletedAt == null) {
					dataManager.insertDailyFood(FoodInit(uid = syncDiets.body()?.data!![i].id, type = syncDiets.body()?.data!![i].mealTime, name = syncDiets.body()?.data!![i].name,
						unit = syncDiets.body()?.data!![i].volumeUnit, amount = syncDiets.body()?.data!![i].volume, calorie = syncDiets.body()?.data!![i].calorie,
						carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein, fat = syncDiets.body()?.data!![i].fat,
						count = syncDiets.body()?.data!![i].quantity, createdAt = date))
				}
			}
		}else {
			Log.e(TAG, "syncDiets: $syncDiets")
			requestStatus = false
		}

		val syncWater = RetrofitAPI.api.syncWater("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncWater.isSuccessful) {
			Log.d(TAG, "syncWater: ${syncWater.body()}")

			for(i in 0 until syncWater.body()?.data!!.size) {
				val getData = dataManager.getData(WATER, CREATED_AT, syncWater.body()?.data!![i].date)
				if(getData.id > 0) {
					if(syncWater.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(WATER, "id", getData.id)
					}else {
						dataManager.updateInt(WATER, "count", syncWater.body()?.data!![i].count, "id", getData.id)
						dataManager.updateStr(WATER, "uid", syncWater.body()?.data!![i].id, "id", getData.id)
					}
				}else if(syncWater.body()?.data!![i].createdAt != null && syncWater.body()?.data!![i].deletedAt == null) {
					dataManager.insertWater(InitWater(uid = syncWater.body()?.data!![i].id, count = syncWater.body()?.data!![i].count,
						volume = syncWater.body()?.data!![i].mL, createdAt = syncWater.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncWater: $syncWater")
			requestStatus = false
		}

		val syncActivity = RetrofitAPI.api.syncActivity("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncActivity.isSuccessful) {
			Log.d(TAG, "syncActivity: ${syncActivity.body()}")

			for(i in 0 until syncActivity.body()?.data!!.size) {
				val getData = dataManager.getData(EXERCISE, "name", syncActivity.body()?.data!![i].name)
				if(getData.id > 0) {
					if(syncActivity.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(EXERCISE, "uid", syncActivity.body()?.data!![i].id)
					}else {
						dataManager.updateData1(EXERCISE, syncActivity.body()?.data!![i].registerType, syncActivity.body()?.data!![i].id, getData.id)
						if(syncActivity.body()?.data!![i].usages != null) {
							val updatedAt = isoToDateTime(syncActivity.body()?.data!![i].usages!![0].updatedAt).toString()
							dataManager.updateData2(EXERCISE, syncActivity.body()?.data!![i].usages!![0].usageCount, updatedAt, 0, getData.id)
						}
					}
				}else if(syncActivity.body()?.data!![i].createdAt != null && syncActivity.body()?.data!![i].deletedAt == null) {
					var useCount = 0
					var useDate = ""
					if(syncActivity.body()?.data!![i].usages != null) {
						useCount = syncActivity.body()?.data!![i].usages!![0].usageCount
						useDate = isoToDateTime(syncActivity.body()?.data!![i].usages!![0].updatedAt).toString()
					}

					dataManager.insertExercise(Exercise(registerType = syncActivity.body()?.data!![i].registerType, uid = syncActivity.body()?.data!![i].id,
						name = syncActivity.body()?.data!![i].name, useCount = useCount, useDate = useDate))
				}
			}
		}else {
			Log.e(TAG, "syncActivity: $syncActivity")
			requestStatus = false
		}

		val syncWorkout = RetrofitAPI.api.syncWorkout("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncWorkout.isSuccessful) {
			Log.d(TAG, "syncWorkout: ${syncWorkout.body()}")

			for(i in 0 until syncWorkout.body()?.data!!.size) {
				val getData = dataManager.getData(DAILY_EXERCISE, "uid", syncWorkout.body()?.data!![i].id)

				if(getData.id > 0) {
					if(syncWorkout.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DAILY_EXERCISE, "id", getData.id)
					}else {
						dataManager.updateDailyExercise(Exercise(id = getData.id, intensity = syncWorkout.body()?.data!![i].intensity,
							workoutTime = syncWorkout.body()?.data!![i].time, kcal = syncWorkout.body()?.data!![i].calorie))
						dataManager.updateStr(DAILY_EXERCISE, "uid", syncWorkout.body()?.data!![i].id, "id", getData.id)
					}
				}else if(syncWorkout.body()?.data!![i].createdAt != null && syncWorkout.body()?.data!![i].deletedAt == null) {
					dataManager.insertDailyExercise(Exercise(uid = syncWorkout.body()?.data!![i].id, name = syncWorkout.body()?.data!![i].name,
						intensity = syncWorkout.body()?.data!![i].intensity, workoutTime = syncWorkout.body()?.data!![i].time,
						kcal = syncWorkout.body()?.data!![i].calorie, createdAt = syncWorkout.body()?.data!![i].date.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncWorkout: $syncWorkout")
			requestStatus = false
		}

		val syncBody = RetrofitAPI.api.syncBody("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncBody.isSuccessful) {
			Log.d(TAG, "syncBody: ${syncBody.body()}")

			for(i in 0 until syncBody.body()?.data!!.size) {
				val getData = dataManager.getData(BODY, CREATED_AT, syncBody.body()?.data!![i].time.substring(0, 10))
				if(getData.id > 0) {
					if(syncBody.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(BODY, "id", getData.id)
					}else {
						dataManager.updateBody(Body(id = getData.id, height = syncBody.body()?.data!![i].height, weight = syncBody.body()?.data!![i].weight,
							intensity = syncBody.body()?.data!![i].workoutIntensity, fat = syncBody.body()?.data!![i].bodyFatPercentage,
							muscle = syncBody.body()?.data!![i].skeletalMuscleMass, bmi = syncBody.body()?.data!![i].bodyMassIndex, bmr = syncBody.body()?.data!![i].basalMetabolicRate))
						dataManager.updateStr(BODY, "uid", syncBody.body()?.data!![i].id, "id", getData.id)
					}
				}else if(syncBody.body()?.data!![i].createdAt != null && syncBody.body()?.data!![i].deletedAt == null) {
					dataManager.insertBody(Body(uid = syncBody.body()?.data!![i].id, height = syncBody.body()?.data!![i].height, weight = syncBody.body()?.data!![i].weight,
						intensity = syncBody.body()?.data!![i].workoutIntensity, fat = syncBody.body()?.data!![i].bodyFatPercentage,
						muscle = syncBody.body()?.data!![i].skeletalMuscleMass, bmi = syncBody.body()?.data!![i].bodyMassIndex, bmr = syncBody.body()?.data!![i].basalMetabolicRate,
						createdAt = syncBody.body()?.data!![i].time.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncBody: $syncBody")
			requestStatus = false
		}

		val syncSleep = RetrofitAPI.api.syncSleep("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncSleep.isSuccessful) {
			Log.d(TAG, "syncSleep: ${syncSleep.body()}")
			for(i in 0 until syncSleep.body()?.data!!.size) {
				val startTime = isoToDateTime(syncSleep.body()?.data!![i].starts).toString()
				val endTime = isoToDateTime(syncSleep.body()?.data!![i].ends).toString()
				val getData = dataManager.getSleep(startTime)
				if(getData.id > 0) {
					if(syncSleep.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(SLEEP, "id", getData.id)
					}else {
						dataManager.updateSleep(Sleep(id = getData.id, startTime = startTime, endTime = endTime))
						dataManager.updateStr(SLEEP, "uid", syncSleep.body()?.data!![i].id, "id", getData.id)
					}
				}else if(syncSleep.body()?.data!![i].createdAt != null && syncSleep.body()?.data!![i].deletedAt == null) {
					dataManager.insertSleep(Sleep(uid = syncSleep.body()?.data!![i].id, startTime = startTime, endTime = endTime))
				}
			}
		}else {
			Log.e(TAG, "syncSleep: $syncSleep")
			requestStatus = false
		}

		/*val syncMedicine = RetrofitAPI.api.syncMedicine("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncMedicine.isSuccessful) {
			Log.d(TAG, "syncMedicine: ${syncMedicine.body()}")
			val alarmReceiver = AlarmReceiver()
			val timeList = ArrayList<DrugTime>()

			for(i in 0 until syncMedicine.body()?.data!!.size) {
				val startDate = LocalDate.parse(syncMedicine.body()?.data!![i].starts.substring(0, 10))
				val endDate = LocalDate.parse(syncMedicine.body()?.data!![i].ends.substring(0, 10))
				val count = startDate.until(endDate, ChronoUnit.DAYS) + 1 // 약복용 날짜 횟수
				val getData = dataManager.getData(DRUG, "startDate", startDate.toString())

				val drug = Drug(id = getData.id, uid = syncMedicine.body()?.data!![i].id, type = syncMedicine.body()?.data!![i].category,
					name = syncMedicine.body()?.data!![i].name, amount = syncMedicine.body()?.data!![i].amount, unit = syncMedicine.body()?.data!![i].unit,
					count = count.toInt(), startDate = startDate.toString(), endDate = endDate.toString())

				if(getData.id > 0) {
					if(syncMedicine.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DRUG, "id", getData.id)
						dataManager.deleteItem(DRUG_TIME, "drugId", getData.id)
						dataManager.deleteItem(DRUG_CHECK, "drugId", getData.id)
						alarmReceiver.cancelAlarm(context, getData.id)
					}else {
						val response = RetrofitAPI.api.getAllMedicineTime("Bearer ${getToken.access}", drug.uid)
						if(response.isSuccessful) {
							Log.d(TAG, "getAllMedicineTime: ${response.body()}")
							dataManager.updateDrug(drug) // 약복용 업데이트
							dataManager.deleteItem(DRUG_TIME, "drugId", getData.id) // 약복용 시간 데이터 삭제
							dataManager.deleteItem(DRUG_CHECK, "drugId", getData.id) // 약복용 체크 데이터 삭제

							for(j in 0 until response.body()!!.size) {
								dataManager.insertDrugTime(DrugTime(uid = response.body()!![j].id, drugId = getData.id, time = response.body()!![j].time)) // 약복용 시간 데이터 저장
								timeList.add(DrugTime(time = response.body()!![j].time))
							}

							alarmReceiver.setAlarm(context, getData.id, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}") // 알람 재등록
						}else {
							Log.e(TAG, "getAllMedicineTime: $response")
						}
					}
				}else if(syncMedicine.body()?.data!![i].createdAt != null && syncMedicine.body()?.data!![i].deletedAt == null) {
					val response = RetrofitAPI.api.getAllMedicineTime("Bearer ${getToken.access}", drug.uid)

					if(response.isSuccessful) {
						Log.d(TAG, "getAllMedicineTime: ${response.body()}")
						dataManager.insertDrug(drug)
						val drugId = dataManager.getData(DRUG, "startDate", drug.startDate).id

						for(j in 0 until response.body()!!.size) {
							dataManager.insertDrugTime(DrugTime(uid = response.body()!![j].id, drugId = drugId, time = response.body()!![j].time))
							timeList.add(DrugTime(time = response.body()!![j].time))
						}

						alarmReceiver.setAlarm(context, drugId, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}") // 알람 등록
					}else {
						Log.e(TAG, "getAllMedicineTime: $response")
					}
				}
			}
		}else {
			Log.e(TAG, "syncMedicine: $syncMedicine")
			requestStatus = false
		}*/

		val syncGoal = RetrofitAPI.api.syncGoal("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncGoal.isSuccessful) {
			Log.d(TAG, "syncGoal: ${syncGoal.body()}")
			for(i in 0 until syncGoal.body()?.data!!.size) {
				val getData = dataManager.getData(GOAL, CREATED_AT, syncGoal.body()?.data!![i].date.substring(0, 10))
				if(getData.id > 0) {
					if(syncGoal.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(GOAL, "id", getData.id)
					}else {
						dataManager.updateGoal(GoalInit(id=getData.id, uid = syncGoal.body()?.data!![i].id, food = syncGoal.body()?.data!![i].kcalOfDiet,
							waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
							exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight,
							sleep = syncGoal.body()?.data!![i].sleep, drug = syncGoal.body()?.data!![i].medicineIntake))
					}
				}else if(syncGoal.body()?.data!![i].createdAt != null && syncGoal.body()?.data!![i].deletedAt == null) {
					dataManager.insertGoal(GoalInit(uid = syncGoal.body()?.data!![i].id, food = syncGoal.body()?.data!![i].kcalOfDiet,
						waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
						exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight, sleep = syncGoal.body()?.data!![i].sleep,
						drug = syncGoal.body()?.data!![i].medicineIntake, createdAt = syncGoal.body()?.data!![i].date.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncGoal: $syncGoal")
			requestStatus = false
		}

		if(requestStatus) dataManager.updateUserStr(SYNC_TIME, "syncedAt", now, "id")
	}

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
				Constant.GOOGLE.name -> {
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
				Constant.NAVER.name -> {
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
				Constant.KAKAO.name -> {
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

		delay(15000)
	}
}