package kr.bodywell.android.util

import android.util.Log
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.Activity
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.MedicineUpdateDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DBHelper.Companion.DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.IS_UPDATED
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DBHelper.Companion.SYNC_TIME
import kr.bodywell.android.database.DBHelper.Companion.UNUSED
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.service.MyApp
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.Companion.dateToIso
import java.time.Duration
import java.time.LocalDateTime

object ViewModelUtil {
	private var refreshStatus: Boolean = false
	private var loginStatus: Boolean = false
	var syncedStatus: Boolean = false
	var requestStatus: Boolean = true
	var getUser = User()
	var getToken = Token()

	suspend fun createApiRequest(dataManager: DataManager) {
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
		val getDrugCheckUid = dataManager.getDrugCheckUid()
		val getDrugUpdated = dataManager.getDrugUpdated()
		val getGoalUid = dataManager.getGoalUid()
		val getGoalUpdated = dataManager.getGoalUpdated()

		for(i in 0 until getUnused.size) {
			when(getUnused[i].type) {
				FOOD -> {
					val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteFood: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteFood: $response")
						requestStatus = false
					}
				}
				WATER -> {
					val response = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteWater: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteWater: $response")
						requestStatus = false
					}
				}
				DAILY_FOOD -> {
					val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteDiets: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteDiets: $response")
						requestStatus = false
					}
				}
				EXERCISE -> {
					val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteActivity: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteActivity: $response")
						requestStatus = false
					}
				}
				DAILY_EXERCISE -> {
					val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteWorkout: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteWorkout: $response")
						requestStatus = false
					}
				}
				DRUG_CHECK -> {
					val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].drugTimeUid, getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteMedicineIntake: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteMedicineIntake: $response")
						requestStatus = false
					}
				}
				DRUG -> {
					val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteMedicine: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)

						val getTime = dataManager.getUnused("drugTime", getUnused[i].createdAt)
						for(j in 0 until getTime.size) {
							val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getTime[j].drugUid, getTime[j].value)
							if(response2.isSuccessful) {
								Log.d(TAG, "deleteMedicineTime: ${response2.body()}")
								dataManager.deleteItem(UNUSED, "id", getTime[j].id)
							}else {
								Log.e(TAG, "deleteMedicineTime: $response2")
							}
						}
					}else {
						Log.e(TAG, "deleteMedicine: $response")
						requestStatus = false
					}
				}
			}
		}

		if(getUserUpdated.createdAt != "") {
			val data = ProfileDTO(getUserUpdated.name!!, getUserUpdated.birthday!!, getUserUpdated.gender!!,
				getUserUpdated.height!!, getUserUpdated.weight!!, "Asia/Seoul")
			val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateProfile: ${response.body()}")
				dataManager.updateUserInt(IS_UPDATED, 0)
			}else {
				Log.d(TAG, "updateProfile: $response")
				requestStatus = false
			 }
		}

		/*for(i in 0 until getFoodUid.size) {
			val data = FoodDTO("null", getFoodUid[i].name, getFoodUid[i].kcal, getFoodUid[i].carbohydrate, getFoodUid[i].protein,
				getFoodUid[i].fat, getFoodUid[i].count, "개", getFoodUid[i].amount, getFoodUid[i].unit)
			val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createFood: ${response.body()}")
				dataManager.updateStr(TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid[i].id)
			}else {
				Log.d(TAG, "createFood: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getFoodUpdated.size) {
			val data = FoodDTO("null", getFoodUpdated[i].name, getFoodUpdated[i].kcal, getFoodUpdated[i].carbohydrate, getFoodUpdated[i].protein,
				getFoodUpdated[i].fat, getFoodUid[i].count, "개", getFoodUpdated[i].amount, getFoodUpdated[i].unit)
			val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateFood: ${response.body()}")
				dataManager.updateInt(TABLE_FOOD, IS_UPDATED, 0, "id", getFoodUpdated[i].id)
			}else {
				Log.d(TAG, "updateFood: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getDailyFoodUid.size) {
			val photos = ArrayList<String>()
			val getImage = dataManager.getImage(getDailyFoodUid[i].type, getDailyFoodUid[i].createdAt)

//          for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
			for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

			val getFood = dataManager.getFood("name", getDailyFoodUid[i].name)

			if(getFood.uid != "") {
				val dateToIso = dateToIso(getDailyFoodUid[i].createdAt)
				val data = DietDTO(getDailyFoodUid[i].type, "null", getDailyFoodUid[i].name, getDailyFoodUid[i].kcal, getDailyFoodUid[i].carbohydrate,
					getDailyFoodUid[i].protein, getDailyFoodUid[i].fat, getDailyFoodUid[i].count,"개", getDailyFoodUid[i].amount, getDailyFoodUid[i].unit,
					photos, dateToIso, Food(getFood.uid))
				val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", data)

				if(response.isSuccessful) {
					Log.d(TAG, "createDiets: ${response.body()}")
					dataManager.updateStr(TABLE_DAILY_FOOD, "uid", response.body()!!.uid, "id", getDailyFoodUid[i].id)
				}else {
					Log.d(TAG, "createDiets: $response")
					requestST = false
					return
				}
			}
		}

		for(i in 0 until getDailyFoodUpdated.size) {
			val photos = ArrayList<String>()
			val getImage = dataManager.getImage(getDailyFoodUpdated[i].type, getDailyFoodUpdated[i].createdAt)

//                  for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
			for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

			val dateToIso = dateToIso(getDailyFoodUpdated[i].createdAt)
			val data = DietUpdateDTO(getDailyFoodUpdated[i].type, "null", getDailyFoodUpdated[i].name, getDailyFoodUpdated[i].kcal,
				getDailyFoodUpdated[i].carbohydrate, getDailyFoodUpdated[i].protein, getDailyFoodUpdated[i].fat, getDailyFoodUpdated[i].count,
				"개", getDailyFoodUpdated[i].amount, getDailyFoodUpdated[i].unit, photos, dateToIso)
			val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateDiets: ${response.body()}")
				dataManager.updateInt(TABLE_DAILY_FOOD, IS_UPDATED, 0, "id", getDailyFoodUpdated[i].id)
			}else {
				Log.d(TAG, "updateDiets: $response")
				requestST = false
				return
			}
		}*/

		for(i in 0 until getWaterUid.size) {
			val data = WaterDTO(getWaterUid[i].volume, getWaterUid[i].count, getWaterUid[i].createdAt)
			val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createWater: ${response.body()}")
				dataManager.updateStr(WATER, "uid", response.body()!!.uid, "id", getWaterUid[i].id)
				dataManager.updateStr(SYNC_TIME, WATER, dateTimeToIso(LocalDateTime.now()), "userId", MyApp.prefs.getId())
			}else {
				Log.d(TAG, "createWater: $response")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getWaterUpdated.size) {
			if(getWaterUpdated[i].uid != "") {
				val data = WaterDTO(getWaterUpdated[i].volume, getWaterUpdated[i].count, getWaterUpdated[i].createdAt)
				val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, data)

				if(response.isSuccessful) {
					Log.d(TAG, "updateWater: ${response.body()}")
					dataManager.updateInt(WATER, IS_UPDATED, 0, "id", getWaterUpdated[i].id)
					dataManager.updateStr(SYNC_TIME, WATER, dateTimeToIso(LocalDateTime.now()), "userId", MyApp.prefs.getId())
				}else {
					Log.d(TAG, "updateWater: $response")
					requestStatus = false
					return
				}
			}
		}

		for(i in 0 until getExUid.size) {
			val data1 = ActivityDTO(getExUid[i].name)
			val response1 = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", data1)

			if(response1.isSuccessful) {
				Log.d(TAG, "createActivity: ${response1.body()}")
				dataManager.updateStr(EXERCISE, "uid", response1.body()!!.uid, "id", getExUid[i].id)
			}else {
				Log.e(TAG, "createActivity: $response1")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getExUpdated.size) {
			val data1 = ActivityDTO(getExUpdated[i].name)
			val response1 = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExUpdated[i].uid, data1)

			if(response1.isSuccessful) {
				Log.d(TAG, "updateActivity: ${response1.body()}")
				dataManager.updateInt(EXERCISE, IS_UPDATED, 0, "id", getExUpdated[i].id)
			}else {
				Log.d(TAG, "updateActivity: $response1")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getDailyExUid.size) {
			val getExercise = dataManager.getExercise("name", getDailyExUid[i].name)

			if(getExercise.uid != "") {
				val dateToIso = dateToIso(getDailyExUid[i].createdAt)
				val data = WorkoutDTO(getDailyExUid[i].name, getDailyExUid[i].kcal, getDailyExUid[i].intensity, getDailyExUid[i].workoutTime,
					dateToIso, true, Activity(getExercise.uid))
				val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)

				if(response.isSuccessful) {
					Log.d(TAG, "createWorkout: ${response.body()}")
					dataManager.updateStr(DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExUid[i].id)
				}else {
					Log.d(TAG, "createWorkout: $response")
					requestStatus = false
					return
				}
			}
		}

		for(i in 0 until getDailyExUpdated.size) {
			val data = WorkoutUpdateDTO(getDailyExUpdated[i].kcal, getDailyExUpdated[i].intensity, getDailyExUpdated[i].workoutTime)
			val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateWorkout: ${response.body()}")
				dataManager.updateInt(DAILY_EXERCISE, IS_UPDATED, 0, "id", getDailyExUpdated[i].id)
			}else {
				Log.d(TAG, "updateWorkout: $response")
				requestStatus = false
				return
			}
		}

		/*for(i in 0 until getBodyUid.size) {
			val dateToIso = dateToIso(getBodyUid[i].createdAt)
			val data = BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat, getBodyUid[i].muscle,
				getBodyUid[i].bmr, getBodyUid[i].intensity, dateToIso)
			val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createBody: ${response.body()}")
				dataManager.updateStr(DBHelper.TABLE_BODY, "uid", response.body()!!.uid, "id", getBodyUid[i].id)
			}else {
				Log.d(TAG, "createBody: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getBodyUpdated.size) {
			val dateToIso = dateToIso(getBodyUpdated[i].createdAt)
			val data = BodyDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight, getBodyUpdated[i].bmi, getBodyUpdated[i].fat,
				getBodyUpdated[i].muscle, getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, dateToIso)
			val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated[i].uid!!, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateBody: ${response.body()}")
				dataManager.updateInt(DBHelper.TABLE_BODY, IS_UPDATED, 0, "id", getBodyUpdated[i].id)
			}else {
				Log.d(TAG, "updateBody: $response")
				requestST = false
				return
			}
		}*/

		for(i in 0 until getSleepUid.size) {
			val data = SleepDTO(getSleepUid[i].startTime, getSleepUid[i].endTime)
			val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createSleep: ${response.body()}")
				dataManager.updateStr(SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
			}else {
				Log.d(TAG, "createSleep: $response")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getSleepUpdated.size) {
			val data = SleepUpdateDTO(getSleepUpdated[i].startTime, getSleepUpdated[i].endTime)
			val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateSleep: ${response.body()}")
				dataManager.updateInt(SLEEP, IS_UPDATED, 0, "id", getSleepUpdated[i].id)
			}else {
				Log.e(TAG, "updateSleep: $response")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getDrugUid.size) {
			val startDate = dateToIso(getDrugUid[i].startDate)
			val endDate = dateToIso(getDrugUid[i].endDate)
			val data = MedicineDTO(getDrugUid[i].type, getDrugUid[i].name, getDrugUid[i].amount, getDrugUid[i].unit, startDate, endDate)
			val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createMedicine: ${response.body()}")
				dataManager.updateStr(DRUG, "uid", response.body()!!.uid, "id", getDrugUid[i].id)

				val getDrugTime = dataManager.getDrugTime(getDrugUid[i].id)

				for(j in 0 until getDrugTime.size) {
					val response2 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response.body()!!.uid, MedicineTimeDTO(getDrugTime[j].time))

					if(response2.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response2.body()}")
						dataManager.updateStr(DRUG_TIME, "uid", response2.body()!!.uid, "id", getDrugTime[j].id)
					}else Log.d(TAG, "createMedicineTime: $response2")
				}
			}else {
				Log.d(TAG, "createMedicine: $response")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getDrugUpdated.size) {
			val startDate = dateToIso(getDrugUpdated[i].startDate)
			val endDate = dateToIso(getDrugUpdated[i].endDate)
			val data = MedicineUpdateDTO(getDrugUpdated[i].type, getDrugUpdated[i].name, getDrugUpdated[i].amount, getDrugUpdated[i].unit, startDate, endDate)
			val response1 = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getDrugUpdated[i].uid, data)

			if(response1.isSuccessful) {
				Log.d(TAG, "updateMedicine: ${response1.body()}")
				dataManager.updateStr(DRUG, "uid", response1.body()!!.uid, "id", getDrugUpdated[i].id)
				dataManager.updateInt(DRUG, IS_UPDATED, 0, "id", getDrugUpdated[i].id)

				val getUnusedTime = dataManager.getUnused(DRUG_TIME, getDrugUpdated[i].startDate)
				for(j in 0 until getUnusedTime.size) {
					val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[j].value)

					if(response2.isSuccessful) {
						Log.d(TAG, "deleteMedicineTime: ${response2.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnusedTime[i].id)
					}else Log.e(TAG, "deleteMedicineTime: $response2")
				}

				val getDrugTime = dataManager.getDrugTimeData(getDrugUpdated[i].id)
				for(j in 0 until getDrugTime.size) {
					val timeDTO = MedicineTimeDTO(getDrugTime[j].time)
					val response3 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response1.body()!!.uid, timeDTO)

					if(response3.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response3.body()}")
						dataManager.updateStr(DRUG_TIME, "uid", response3.body()!!.uid, "id", getDrugTime[j].id)
					}else Log.e(TAG, "createMedicineTime: $response3")
				}
			}else {
				Log.e(TAG, "updateMedicine: $response1")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getDrugCheckUid.size) {
			val drugUid = dataManager.getUid(DRUG, getDrugCheckUid[i].drugId)
			val drugTimeUid = dataManager.getUid(DRUG_TIME, getDrugCheckUid[i].drugTimeId)

			if(drugUid != "" && drugTimeUid != "") {
				val isoFormat = dateToIso(getDrugCheckUid[i].createdAt)
				val data = MedicineIntakeDTO(isoFormat)
				val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", drugUid, drugTimeUid, data)

				if(response.isSuccessful) {
					Log.d(TAG, "createMedicineIntake: ${response.body()}")
					dataManager.updateStr(DRUG_CHECK, "uid", response.body()!!.uid, "id", getDrugCheckUid[i].id)
				}else {
					Log.e(TAG, "createMedicineIntake: $response")
					requestStatus = false
					return
				}
			}
		}

		for(i in 0 until getGoalUid.size) {
			val getGoal = dataManager.getGoal(getGoalUid[i].id)

			if(getGoal.uid == "") {
				val dateToIso = dateToIso(getGoalUid[i].createdAt)
				val data = GoalDTO(getGoalUid[i].body, getGoalUid[i].food, getGoalUid[i].exercise, getGoalUid[i].waterVolume, getGoalUid[i].water,
					getGoalUid[i].sleep, getGoalUid[i].drug, dateToIso)
				val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", data)

				if(response.isSuccessful) {
					Log.d(TAG, "createGoal: ${response.body()}")
					dataManager.updateStr(GOAL, "uid", response.body()!!.uid, "id", getGoalUid[i].id)
				}else {
					Log.d(TAG, "createGoal: $response")
					requestStatus = false
					return
				}
			}
		}

		for(i in 0 until getGoalUpdated.size) {
			val data = GoalDTO(getGoalUpdated[i].body, getGoalUpdated[i].food, getGoalUpdated[i].exercise, getGoalUpdated[i].waterVolume,
				getGoalUpdated[i].water, getGoalUpdated[i].sleep, getGoalUpdated[i].drug)
			Log.d(TAG, "data: $data")
			val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateGoal: ${response.body()}")
				dataManager.updateInt(GOAL, IS_UPDATED, 0, "id", getGoalUpdated[i].id)
			}else {
				Log.d(TAG, "updateGoal: $response")
				requestStatus = false
				return
			}
		}
	}

	suspend fun createSync(dataManager: DataManager):Boolean {
		val getSynced = dataManager.getSynced()

		if(getSynced.water != "") {
			val response = RetrofitAPI.api.syncWater("Bearer ${getToken.access}", SyncDTO(getSynced.water))
			if(response.isSuccessful) {
				Log.d(TAG, "syncWater: ${response.body()}")

				for(i in 0 until response.body()?.data!!.size) {
					val waterId = dataManager.getWaterId(response.body()?.data!![i].uid)
					if(waterId > 0) {
						if(response.body()?.data!![i].deletedAt != "") dataManager.deleteItem(WATER, "id", waterId)
						if(response.body()?.data!![i].createdAt == response.body()?.data!![i].updatedAt) {
							dataManager.insertWater(Water(uid = response.body()?.data!![i].uid, count = response.body()?.data!![i].count,
								volume = response.body()?.data!![i].mL, createdAt = response.body()?.data!![i].date))
						}else {
							dataManager.updateInt(WATER, "count", response.body()?.data!![i].count, "id", waterId)
						}
					}
				}
			}else {
				Log.e(TAG, "syncWater: $response")
			}
		}

		return true
	}

	suspend fun refreshToken(dataManager: DataManager) {
		val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
		val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

		if ((accessDiff.toHours() in 1..335) && !refreshStatus) {
			val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
			if(response.isSuccessful) {
				Log.d(TAG, "refreshToken: ${response.body()}")
				dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
				getToken = dataManager.getToken()
				refreshStatus = true
			}else {
				Log.d(TAG, "refreshToken: $response")
				refreshStatus = false
			}
		}

		if(refreshDiff.toHours() >= 336 && !loginStatus) {
			val data = LoginDTO(getUser.idToken)
			when (getUser.type) {
				"google" -> {
					val response = RetrofitAPI.api.loginWithGoogle(data)
					if (response.isSuccessful) {
						Log.d(TAG, "googleLogin: ${response.body()}")
						dataManager.updateToken(
							Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString())
						)
						getToken = dataManager.getToken()
						loginStatus = true
					} else {
						Log.e(TAG, "googleLogin: $response")
						loginStatus = false
					}
				}
			}
		}
	}
}