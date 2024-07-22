package kr.bodywell.android.util

import android.util.Log
import kr.bodywell.android.api.RetrofitAPI
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
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SYNC_DATA
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.isoFormat1
import kr.bodywell.android.util.CustomUtil.Companion.isoFormatter
import java.time.Duration
import java.time.LocalDateTime

object VMUtil {
	private var refreshST: Boolean = false
	private var loginST: Boolean = false
	var syncedST: Boolean = false
	var requestST: Boolean = true

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
				"food" -> {
					val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteFood: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteFood: $response")
						requestST = false
					}
				}
				"dailyFood" -> {
					val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteDiets: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteDiets: $response")
						requestST = false
					}
				}
				"exercise" -> {
					val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteActivity: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteActivity: $response")
						requestST = false
					}
				}
				"dailyExercise" -> {
					val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteWorkout: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteWorkout: $response")
						requestST = false
					}
				}
				"drugCheck" -> {
					val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].drugTimeUid, getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteMedicineIntake: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteMedicineIntake: $response")
						requestST = false
					}
				}
				"drug" -> {
					val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteMedicine: $response")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnused[i].id)

						val getTime = dataManager.getUnused("drugTime", getUnused[i].createdAt)
						for(j in 0 until getTime.size) {
							val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getTime[j].drugUid, getTime[j].value)
							if(response2.isSuccessful) {
								Log.d(TAG, "deleteMedicineTime: $response2")
								dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getTime[j].id)
							}else Log.e(TAG, "deleteMedicineTime: $response2")
						}
					}else {
						Log.e(TAG, "deleteMedicine: $response")
						requestST = false
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
				dataManager.updateUserInt("isUpdated", 0)
			}else {
				Log.d(TAG, "updateProfile: $response")
				requestST = false
			 }
		}

		/*for(i in 0 until getFoodUid.size) {
			val data = FoodDTO("null", getFoodUid[i].name, getFoodUid[i].kcal, getFoodUid[i].carbohydrate, getFoodUid[i].protein,
				getFoodUid[i].fat, getFoodUid[i].count, "개", getFoodUid[i].amount, getFoodUid[i].unit)
			val response = RetrofitAPI.api.createFood("Bearer ${VMData.getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createFood: ${response.body()}")
				dataManager.updateStr(DBHelper.TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid[i].id)
			}else {
				Log.d(TAG, "createFood: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getFoodUpdated.size) {
			val data = FoodDTO("", getFoodUpdated[i].name, getFoodUpdated[i].kcal, getFoodUpdated[i].carbohydrate, getFoodUpdated[i].protein,
				getFoodUpdated[i].fat, 0, "", getFoodUpdated[i].amount, getFoodUpdated[i].unit)
			val response = RetrofitAPI.api.updateFood("Bearer ${VMData.getToken.access}", getFoodUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateFood: ${response.body()}")
				dataManager.updateInt(DBHelper.TABLE_FOOD, "isUpdated", 0, "id", getFoodUpdated[i].id)
			}else {
				Log.d(TAG, "updateFood: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getDailyFoodUid.size) {
			val photos = ArrayList<String>()
			val getImage = dataManager.getImage(getDailyFoodUid[i].type, getDailyFoodUid[i].created)

//          for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
			for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

			val getFood = dataManager.getFood("name", getDailyFoodUid[i].name)

			if(getFood.uid != "") {
				val isoFormat = isoFormat1(getDailyFoodUid[i].created)
				val data = DietDTO(getDailyFoodUid[i].type, "null", getDailyFoodUid[i].name, getDailyFoodUid[i].kcal, getDailyFoodUid[i].carbohydrate,
					getDailyFoodUid[i].protein, getDailyFoodUid[i].fat, getDailyFoodUid[i].count,"개", getDailyFoodUid[i].amount, getDailyFoodUid[i].unit,
					photos, isoFormat, Food(getFood.uid))
				val response = RetrofitAPI.api.createDiets("Bearer ${VMData.getToken.access}", data)

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
			val getImage = dataManager.getImage(getDailyFoodUpdated[i].type, getDailyFoodUpdated[i].created)

//                  for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
			for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

			val isoFormat = isoFormat1(getDailyFoodUpdated[i].created)
			val data = DietUpdateDTO(getDailyFoodUpdated[i].type, "null", getDailyFoodUpdated[i].name, getDailyFoodUpdated[i].kcal,
				getDailyFoodUpdated[i].carbohydrate, getDailyFoodUpdated[i].protein, getDailyFoodUpdated[i].fat, getDailyFoodUpdated[i].count,
				"null", getDailyFoodUpdated[i].amount, getDailyFoodUpdated[i].unit, photos, isoFormat)
			val response = RetrofitAPI.api.updateDiets("Bearer ${VMData.getToken.access}", getDailyFoodUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateDiets: ${response.body()}")
				dataManager.updateInt(TABLE_DAILY_FOOD, "isUpdated", 0, "id", getDailyFoodUpdated[i].id)
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
				dataManager.updateStr(TABLE_WATER, "uid", response.body()!!.uid, "id", getWaterUid[i].id)
			}else {
				Log.d(TAG, "createWater: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getWaterUpdated.size) {
			if(getWaterUpdated[i].uid != "") {
				val data = WaterDTO(getWaterUpdated[i].volume, getWaterUpdated[i].count, getWaterUpdated[i].createdAt)
				val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, data)

				if(response.isSuccessful) {
					Log.d(TAG, "updateWater: ${response.body()}")
					dataManager.updateInt(TABLE_WATER, "isUpdated", 0, "id", getWaterUpdated[i].id)
				}else {
					Log.d(TAG, "updateWater: $response")
					requestST = false
					return
				}
			}
		}

		/*for(i in 0 until getExUid.size) {
			val data1 = ActivityDTO(getExUid[i].name)
			val response1 = RetrofitAPI.api.createActivity("Bearer ${VMData.getToken.access}", data1)

			if(response1.isSuccessful) {
				Log.d(TAG, "createActivity: ${response1.body()}")
				dataManager.updateStr(DBHelper.TABLE_EXERCISE, "uid", response1.body()!!.uid, "id", getExUid[i].id)

				val data2 = SyncDTO(SyncData(getExUid[i].name, "User", getExUid[i].created, getExUid[i].created), getExUid[i].created)
				val response2 = RetrofitAPI.api.syncCreateActivity("Bearer ${VMData.getToken.access}", data2)
				if(response2.isSuccessful) Log.d(CustomUtil.TAG, "SyncActivity: ${response2.body()}") else Log.e(TAG, "SyncActivity: $response2")
			}else {
				Log.e(TAG, "createActivity: $response1")
				requestST = false
				return
			}
		}

		for(i in 0 until getExUpdated.size) {
			val data1 = ActivityDTO(getExUpdated[i].name)
			val response1 = RetrofitAPI.api.updateActivity("Bearer ${VMData.getToken.access}", getExUpdated[i].uid, data1)

			if(response1.isSuccessful) {
				Log.d(TAG, "updateActivity: ${response1.body()}")
				dataManager.updateInt(DBHelper.TABLE_EXERCISE, "isUpdated", 0, "id", getExUpdated[i].id)

				val data2 = SyncUpdateDTO(SyncUpdateData(getExUpdated[i].uid, getExUpdated[i].name, "User", getExUpdated[i].created, getExUpdated[i].updated), getExUpdated[i].created)
				val response2 = RetrofitAPI.api.syncUpdateActivity("Bearer ${VMData.getToken.access}", data2)
				if(response2.isSuccessful) Log.d(TAG, "SyncActivity: ${response2.body()}") else Log.e(
					TAG, "SyncActivity: $response2")
			}else {
				Log.d(TAG, "updateActivity: $response1")
				requestST = false
				return
			}
		}

		for(i in 0 until getDailyExUid.size) {
			val getExercise = dataManager.getExercise("name", getDailyExUid[i].name)

			if(getExercise.uid != "") {
				val isoFormat = isoFormat1(getDailyExUid[i].created)
				val data = WorkoutDTO(getDailyExUid[i].name, getDailyExUid[i].kcal, getDailyExUid[i].intensity,
					getDailyExUid[i].workoutTime, isoFormat, true, Activity(getExercise.uid))
				val response = RetrofitAPI.api.createWorkout("Bearer ${VMData.getToken.access}", data)

				if(response.isSuccessful) {
					Log.d(TAG, "createWorkout: ${response.body()}")
					dataManager.updateStr(TABLE_DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExUid[i].id)
				}else {
					Log.d(TAG, "createWorkout: $response")
					requestST = false
					return
				}
			}
		}

		for(i in 0 until getDailyExUpdated.size) {
			val data = WorkoutUpdateDTO(getDailyExUpdated[i].kcal, getDailyExUpdated[i].intensity, getDailyExUpdated[i].workoutTime)
			val response = RetrofitAPI.api.updateWorkout("Bearer ${VMData.getToken.access}", getDailyExUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateWorkout: ${response.body()}")
				dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 0, "id", getDailyExUpdated[i].id)
			}else {
				Log.d(TAG, "updateWorkout: $response")
				requestST = false
				return
			}
		}*/

		/*for(i in 0 until getBodyUid.size) {
			val isoFormat = isoFormat1(getBodyUid[i].created)
			val data = BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat, getBodyUid[i].muscle,
				getBodyUid[i].bmr, getBodyUid[i].intensity, isoFormat)
			val response = RetrofitAPI.api.createBody("Bearer ${VMData.getToken.access}", data)

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
			val isoFormat = isoFormat1(getBodyUpdated[i].created)
			val data = BodyDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight, getBodyUpdated[i].bmi, getBodyUpdated[i].fat,
				getBodyUpdated[i].muscle, getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, isoFormat)
			val response = RetrofitAPI.api.updateBody("Bearer ${VMData.getToken.access}", getBodyUpdated[i].uid!!, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateBody: ${response.body()}")
				dataManager.updateInt(DBHelper.TABLE_BODY, "isUpdated", 0, "id", getBodyUpdated[i].id)
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
				dataManager.updateStr(TABLE_SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
			}else {
				Log.d(TAG, "createSleep: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getSleepUpdated.size) {
			val data = SleepUpdateDTO(getSleepUpdated[i].startTime, getSleepUpdated[i].endTime)
			val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid, data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateSleep: ${response.body()}")
				dataManager.updateInt(TABLE_SLEEP, "isUpdated", 0, "id", getSleepUpdated[i].id)
			}else {
				Log.e(TAG, "updateSleep: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getDrugUid.size) {
			val startDate = isoFormat1(getDrugUid[i].startDate)
			val endDate = isoFormat1(getDrugUid[i].endDate)
			val data = MedicineDTO(getDrugUid[i].type, getDrugUid[i].name, getDrugUid[i].amount, getDrugUid[i].unit, startDate, endDate)
			val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createMedicine: ${response.body()}")
				dataManager.updateStr(DBHelper.TABLE_DRUG, "uid", response.body()!!.uid, "id", getDrugUid[i].id)

				val getDrugTime = dataManager.getDrugTime(getDrugUid[i].id)

				for(j in 0 until getDrugTime.size) {
					val response2 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response.body()!!.uid, MedicineTimeDTO(getDrugTime[j].time))

					if(response2.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response2.body()}")
						dataManager.updateStr(DBHelper.TABLE_DRUG_TIME, "uid", response2.body()!!.uid, "id", getDrugTime[j].id)
					}else Log.d(TAG, "createMedicineTime: $response2")
				}
			}else {
				Log.d(TAG, "createMedicine: $response")
				requestST = false
				return
			}
		}

		for(i in 0 until getDrugUpdated.size) {
			val startDate = isoFormat1(getDrugUpdated[i].startDate)
			val endDate = isoFormat1(getDrugUpdated[i].endDate)
			val data = MedicineUpdateDTO(getDrugUpdated[i].type, getDrugUpdated[i].name, getDrugUpdated[i].amount, getDrugUpdated[i].unit, startDate, endDate)
			val response1 = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getDrugUpdated[i].uid, data)

			if(response1.isSuccessful) {
				Log.d(TAG, "updateMedicine: ${response1.body()}")
				dataManager.updateStr(DBHelper.TABLE_DRUG, "uid", response1.body()!!.uid, "id", getDrugUpdated[i].id)
				dataManager.updateInt(DBHelper.TABLE_DRUG, "isUpdated", 0, "id", getDrugUpdated[i].id)

				val getUnusedTime = dataManager.getUnused("drugTime", getDrugUpdated[i].startDate)
				for(j in 0 until getUnusedTime.size) {
					val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[j].value)

					if(response2.isSuccessful) {
						Log.d(TAG, "deleteMedicineTime: ${response2.body()}")
						dataManager.deleteItem(DBHelper.TABLE_UNUSED, "id", getUnusedTime[i].id)
					}else Log.e(TAG, "deleteMedicineTime: $response2")
				}

				val getDrugTime = dataManager.getDrugTimeData(getDrugUpdated[i].id)
				for(j in 0 until getDrugTime.size) {
					val timeDTO = MedicineTimeDTO(getDrugTime[j].time)
					val response3 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response1.body()!!.uid, timeDTO)

					if(response3.isSuccessful) {
						Log.d(TAG, "createMedicineTime: ${response3.body()}")
						dataManager.updateStr(DBHelper.TABLE_DRUG_TIME, "uid", response3.body()!!.uid, "id", getDrugTime[j].id)
					}else Log.e(TAG, "createMedicineTime: $response3")
				}
			}else {
				Log.e(TAG, "updateMedicine: $response1")
				requestST = false
				return
			}
		}

		for(i in 0 until getDrugCheckUid.size) {
			val drugUid = dataManager.getUid(DBHelper.TABLE_DRUG, getDrugCheckUid[i].drugId)
			val drugTimeUid = dataManager.getUid(DBHelper.TABLE_DRUG_TIME, getDrugCheckUid[i].drugTimeId)

			if(drugUid != "" && drugTimeUid != "") {
				val isoFormat = isoFormat1(getDrugCheckUid[i].createdAt)
				val data = MedicineIntakeDTO(isoFormat)
				val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", drugUid, drugTimeUid, data)

				if(response.isSuccessful) {
					Log.d(TAG, "createMedicineIntake: ${response.body()}")
					dataManager.updateStr(DBHelper.TABLE_DRUG_CHECK, "uid", response.body()!!.uid, "id", getDrugCheckUid[i].id)
				}else {
					Log.e(TAG, "createMedicineIntake: $response")
					requestST = false
					return
				}
			}
		}

		/*for(i in 0 until getGoalUid.size) {
			val getGoal = dataManager.getGoal(getGoalUid[i].id)

			if(getGoal.uid == "") {
				val isoFormat = isoFormat1(getGoalUid[i].created)
				val data = GoalDTO(getGoalUid[i].body, getGoalUid[i].food, getGoalUid[i].exercise, getGoalUid[i].waterVolume, getGoalUid[i].water,
					getGoalUid[i].sleep, getGoalUid[i].drug, isoFormat)
				val response = RetrofitAPI.api.createGoal("Bearer ${VMData.getToken.access}", data)

				if(response.isSuccessful) {
					Log.d(TAG, "createGoal: ${response.body()}")
					dataManager.updateStr(TABLE_GOAL, "uid", response.body()!!.uid, "id", getGoalUid[i].id)
				}else {
					Log.d(TAG, "createGoal: $response")
					requestST = false
					return
				}
			}
		}

		for(i in 0 until getGoalUpdated.size) {
			val data = GoalDTO(getGoalUpdated[i].body, getGoalUpdated[i].food, getGoalUpdated[i].exercise, getGoalUpdated[i].waterVolume,
				getGoalUpdated[i].water, getGoalUpdated[i].sleep, getGoalUpdated[i].drug)
			Log.d(TAG, "data: $data")
			val response = RetrofitAPI.api.updateGoal("Bearer ${VMData.getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateGoal: ${response.body()}")
				dataManager.updateInt(TABLE_GOAL, "isUpdated", 0, "id", getGoalUpdated[i].id)
			}else {
				Log.d(TAG, "updateGoal: $response")
				requestST = false
				return
			}
		}*/
	}

	suspend fun createSync(dataManager: DataManager):Boolean {
		val getSynced = dataManager.getSynced()
		val isoFormat = LocalDateTime.parse(getSynced.string1).format(isoFormatter)

		val response = RetrofitAPI.api.syncWater("Bearer ${getToken.access}", SyncDTO(isoFormat))
		if(response.isSuccessful) {
			Log.d(TAG, "syncWater: ${response.body()}")

			for(i in 0 until response.body()?.data!!.size) {
				val waterId = dataManager.getWaterId(response.body()?.data!![i].uid)
				if(waterId > 0) {
					if(response.body()?.data!![i].deletedAt != "") dataManager.deleteItem(TABLE_WATER, "id", waterId)
					if(response.body()?.data!![i].createdAt == response.body()?.data!![i].updatedAt) {
						dataManager.insertWater(Water(uid = response.body()?.data!![i].uid, count = response.body()?.data!![i].count,
							volume = response.body()?.data!![i].mL, createdAt = response.body()?.data!![i].date, updatedAt = response.body()?.data!![i].updatedAt))
					}else {
						dataManager.updateWater(Water(id = waterId, count = response.body()?.data!![i].count, updatedAt = response.body()?.data!![i].updatedAt))
					}
				}
			}
		}else {
			Log.e(TAG, "syncWater: $response")
		}

		dataManager.updateStr(TABLE_SYNC_DATA, "syncedAt", LocalDateTime.now().toString(), "id", getSynced.int1)

		return true
	}

	suspend fun refreshToken(dataManager: DataManager) {
		val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
		val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

		if ((accessDiff.toHours() in 1..335) && !refreshST) {
			val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
			if(response.isSuccessful) {
				Log.d(TAG, "refreshToken: ${response.body()}")
				dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
				getToken = dataManager.getToken()
				refreshST = true
			}else {
				Log.d(TAG, "refreshToken: $response")
				refreshST = false
			}
		}

		if(refreshDiff.toHours() >= 336 && !loginST) {
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
						loginST = true
					} else {
						Log.e(TAG, "googleLogin: $response")
						loginST = false
					}
				}
			}
		}
	}
}