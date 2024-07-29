package kr.bodywell.android.util

import android.util.Log
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.Activity
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodData
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.ProfileDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.SyncDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DBHelper.Companion.BODY
import kr.bodywell.android.database.DBHelper.Companion.CREATED_AT
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
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Drug
import kr.bodywell.android.model.DrugCheck
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.service.MyApp
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.Companion.dateToIso
import kr.bodywell.android.util.CustomUtil.Companion.isoToDateTime
import java.time.Duration
import java.time.LocalDateTime

object ViewModelUtil {
	var getUser = User()
	var getToken = Token()
	var syncCheck: Boolean = false
	private var syncedAt = ""

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
						return
					}
				}
				WATER -> {
					val response = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteWater: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteWater: $response")
						return
					}
				}
				DAILY_FOOD -> {
					val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteDiets: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteDiets: $response")
						return
					}
				}
				EXERCISE -> {
					val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteActivity: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteActivity: $response")
						return
					}
				}
				DAILY_EXERCISE -> {
					val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteWorkout: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteWorkout: $response")
						return
					}
				}
				DRUG_CHECK -> {
					val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].drugTimeUid, getUnused[i].value)
					if(response.isSuccessful) {
						Log.d(TAG, "deleteMedicineIntake: ${response.body()}")
						dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
					}else {
						Log.e(TAG, "deleteMedicineIntake: $response")
						return
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
								return
							}
						}
					}else {
						Log.e(TAG, "deleteMedicine: $response")
						return
					}
				}
			}
		}

		if(getUserUpdated.createdAt != "") {
			val data = ProfileDTO(getUserUpdated.name!!, getUserUpdated.birthday!!, getUserUpdated.gender!!, getUserUpdated.height!!, getUserUpdated.weight!!, "Asia/Seoul")
			val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateProfile: ${response.body()}")
				dataManager.updateUserInt(IS_UPDATED, 0)
			}else {
				Log.d(TAG, "updateProfile: $response")
				return
			 }
		}

		requestFood(dataManager, getFoodUid)

		requestFood(dataManager, getFoodUpdated)

		requestDiet(dataManager, getDailyFoodUid)

		requestDiet(dataManager, getDailyFoodUpdated)

		requestWater(dataManager, getWaterUid)

		requestWater(dataManager, getWaterUpdated)

		requestActivity(dataManager, getExUid)

		requestActivity(dataManager, getExUpdated)

		requestWorkout(dataManager, getDailyExUid)

		requestWorkout(dataManager, getDailyExUpdated)

		for(i in 0 until getBodyUid.size) {
			val dateToIso = dateToIso(getBodyUid[i].createdAt)
			val dto = BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat, getBodyUid[i].muscle,
				getBodyUid[i].bmr, getBodyUid[i].intensity, dateToIso)
			val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", dto)

			if(response.isSuccessful) {
				Log.d(TAG, "createBody: ${response.body()}")
				dataManager.updateStr(BODY, "uid", response.body()!!.uid, "id", getBodyUid[i].id)
			}else {
				Log.d(TAG, "createBody: $response")
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
				dataManager.updateInt(BODY, IS_UPDATED, 0, "id", getBodyUpdated[i].id)
			}else {
				Log.d(TAG, "updateBody: $response")
				return
			}
		}

		for(i in 0 until getSleepUid.size) {
			val data = SleepDTO(getSleepUid[i].startTime, getSleepUid[i].endTime)
			val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "createSleep: ${response.body()}")
				dataManager.updateStr(SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
			}else {
				Log.d(TAG, "createSleep: $response")
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
				return
			}
		}

		requestMedicine(dataManager, getDrugUid)

		requestMedicine(dataManager, getDrugUpdated)

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
					return
				}
			}
		}

		requestGoal(dataManager, getGoalUid)

		requestGoal(dataManager, getGoalUpdated)
	}

	private suspend fun requestFood(dataManager: DataManager, data: ArrayList<Food>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncFood = RetrofitAPI.api.syncFood("Bearer ${getToken.access}", SyncDTO(syncedAt)) // 데이터 동기화
			if(syncFood.isSuccessful) {
				Log.d(TAG, "syncFood: ${syncFood.body()}")

				for(j in 0 until syncFood.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(FOOD, "name", syncFood.body()?.data!![j].foodName) // 같은 이름 존재 확인
					if(getDataId > 0) getUid = syncFood.body()?.data!![j].uid
				}

				if(getUid != "") { // 같은 이름 존재하면 업데이트
					val dto = FoodDTO("null", data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat,
						data[i].count, "개", data[i].amount, data[i].unit)
					val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getUid, dto)

					if(response.isSuccessful) {
						Log.d(TAG, "updateFood: ${response.body()}")
						dataManager.updateStr(FOOD, "uid", getUid, "id", data[i].id) // uid 업데이트
						dataManager.updateInt(FOOD, IS_UPDATED, 0, "id", data[i].id) // 업데이트 체크 0
					}else {
						Log.d(TAG, "updateFood: $response")
						return
					}
				}else { // 같은 이름 존재하지 않으면 저장
					val dto = FoodDTO("null", data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat,
						data[i].count, "개", data[i].amount, data[i].unit)
					val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", dto)

					if(response.isSuccessful) {
						Log.d(TAG, "createFood: ${response.body()}")
						dataManager.updateStr(FOOD, "uid", response.body()!!.uid, "id", data[i].id)
					}else {
						Log.d(TAG, "createFood: $response")
						return
					}
				}
			}else {
				Log.e(TAG, "syncFood: $syncFood")
			}
		}
	}

	private suspend fun requestDiet(dataManager: DataManager, data: ArrayList<Food>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncDiets = RetrofitAPI.api.syncDiets("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncDiets.isSuccessful) {
				Log.d(TAG, "syncDiets: ${syncDiets.body()}")

				for(j in 0 until syncDiets.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(DAILY_FOOD, "name", syncDiets.body()?.data!![j].foodName)
					if(getDataId > 0) getUid = syncDiets.body()?.data!![j].uid
				}

				if(getUid != "") {
					val photos = ArrayList<String>()
					val getImage = dataManager.getImage(data[i].type, data[i].createdAt)

//                  for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
					for(k in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

					val dateToIso = dateToIso(data[i].createdAt)
					val dto = DietUpdateDTO(data[i].type, "null", data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat,
						data[i].count, "개", data[i].amount, data[i].unit, photos, dateToIso)
					val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getUid, dto)

					if(response.isSuccessful) {
						Log.d(TAG, "updateDiets: ${response.body()}")
						dataManager.updateStr(DAILY_FOOD, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(DAILY_FOOD, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.d(TAG, "updateDiets: $response")
						return
					}
				}else {
					val getFood = dataManager.getFood("name", data[i].name)
					if(getFood.uid != "") {
						val photos = ArrayList<String>()
						val getImage = dataManager.getImage(data[i].type, data[i].createdAt)

//          			for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
						for(k in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

						val dateToIso = dateToIso(data[i].createdAt)
						val dto = DietDTO(data[i].type, "null", data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat,
							data[i].count,"개", data[i].amount, data[i].unit, photos, dateToIso, FoodData(getFood.uid))
						val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", dto)

						if(response.isSuccessful) {
							Log.d(TAG, "createDiets: ${response.body()}")
							dataManager.updateStr(DAILY_FOOD, "uid", response.body()!!.uid, "id", data[i].id)
						}else {
							Log.d(TAG, "createDiets: $response")
							return
						}
					}
				}
			}else {
				Log.e(TAG, "syncFood: $syncDiets")
			}
		}
	}

	private suspend fun requestWater(dataManager: DataManager, data: ArrayList<Water>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncWater = RetrofitAPI.api.syncWater("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncWater.isSuccessful) {
				Log.d(TAG, "syncWater: ${syncWater.body()}")

				for(j in 0 until syncWater.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(WATER, CREATED_AT, syncWater.body()?.data!![j].date)
					if(getDataId > 0) getUid = syncWater.body()?.data!![j].uid
				}

				if(getUid != "") {
					val dto = WaterDTO(data[i].volume, data[i].count, data[i].createdAt)
					val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getUid, dto)

					if(response.isSuccessful) {
						Log.d(TAG, "updateWater: ${response.body()}")
						dataManager.updateStr(WATER, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(WATER, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.d(TAG, "updateWater: $response")
						return
					}
				}else {
					val dto = WaterDTO(data[i].volume, data[i].count, data[i].createdAt)
					val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", dto)

					if(response.isSuccessful) {
						Log.d(TAG, "createWater: ${response.body()}")
						dataManager.updateStr(WATER, "uid", response.body()!!.uid, "id", data[i].id)
					}else {
						Log.d(TAG, "createWater: $response")
						return
					}
				}
			}else {
				Log.e(TAG, "syncWater: $syncWater")
			}
		}
	}

	private suspend fun requestActivity(dataManager: DataManager, data: ArrayList<Exercise>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncActivity = RetrofitAPI.api.syncActivity("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncActivity.isSuccessful) {
				Log.d(TAG, "syncActivity: ${syncActivity.body()}")

				for (j in 0 until syncActivity.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(EXERCISE, CREATED_AT, syncActivity.body()?.data!![j].name)
					if (getDataId > 0) getUid = syncActivity.body()?.data!![j].uid
				}

				val dto = ActivityDTO(data[i].name)
				if(getUid != "") {
					val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getUid, dto)

					if(response.isSuccessful) {
						Log.d(TAG, "updateActivity: ${response.body()}")
						dataManager.updateStr(EXERCISE, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(EXERCISE, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.d(TAG, "updateActivity: $response")
						return
					}
				}else {
					val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", dto)

					if(response.isSuccessful) {
						Log.d(TAG, "createActivity: ${response.body()}")
						dataManager.updateStr(EXERCISE, "uid", response.body()!!.uid, "id", data[i].id)
					}else {
						Log.e(TAG, "createActivity: $response")
						return
					}
				}
			}else {
				Log.e(TAG, "syncWater: $syncActivity")
			}
		}
	}

	private suspend fun requestWorkout(dataManager: DataManager, data: ArrayList<Exercise>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncWorkout = RetrofitAPI.api.syncWorkout("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncWorkout.isSuccessful) {
				Log.d(TAG, "syncWorkout: ${syncWorkout.body()}")

				for (j in 0 until syncWorkout.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(DAILY_EXERCISE, CREATED_AT, syncWorkout.body()?.data!![j].name)
					if (getDataId > 0) getUid = syncWorkout.body()?.data!![j].uid
				}

				if(getUid != "") {
					val dto = WorkoutUpdateDTO(data[i].kcal, data[i].intensity, data[i].workoutTime)
					val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", data[i].uid, dto)

					if(response.isSuccessful) {
						Log.d(TAG, "updateWorkout: ${response.body()}")
						dataManager.updateStr(DAILY_EXERCISE, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(DAILY_EXERCISE, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.d(TAG, "updateWorkout: $response")
						return
					}
				}else {
					val getExercise = dataManager.getExercise("name", data[i].name)
					if(getExercise.uid != "") {
						val dateToIso = dateToIso(data[i].createdAt)
						val dto = WorkoutDTO(data[i].name, data[i].kcal, data[i].intensity, data[i].workoutTime, dateToIso, true, Activity(getExercise.uid))
						val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", dto)

						if(response.isSuccessful) {
							Log.d(TAG, "createWorkout: ${response.body()}")
							dataManager.updateStr(DAILY_EXERCISE, "uid", response.body()!!.uid, "id", data[i].id)
						}else {
							Log.d(TAG, "createWorkout: $response")
							return
						}
					}
				}
			}else {
				Log.e(TAG, "syncWorkout: $syncWorkout")
			}
		}
	}

	private suspend fun requestMedicine(dataManager: DataManager, data: ArrayList<Drug>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncMedicine = RetrofitAPI.api.syncMedicine("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncMedicine.isSuccessful) {
				Log.d(TAG, "syncMedicine: ${syncMedicine.body()}")

				for (j in 0 until syncMedicine.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(DRUG, "starts", syncMedicine.body()?.data!![j].starts)
					if (getDataId > 0) getUid = syncMedicine.body()?.data!![j].uid
				}

				val startDate = dateToIso(data[i].startDate)
				val endDate = dateToIso(data[i].endDate)
				val dto = MedicineDTO(data[i].type, data[i].name, data[i].amount, data[i].unit, startDate, endDate)

				if(getUid != "") {
					val updateMedicine = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getUid, dto)

					if(updateMedicine.isSuccessful) {
						Log.d(TAG, "updateMedicine: ${updateMedicine.body()}")
						dataManager.updateStr(DRUG, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(DRUG, IS_UPDATED, 0, "id", data[i].id)

						val getDrugTime = dataManager.getDrugTimeData(data[i].id)
						for(j in 0 until getDrugTime.size) {
							val timeDTO = MedicineTimeDTO(getDrugTime[j].time)
							val createMedicineTime = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", updateMedicine.body()!!.uid, timeDTO)

							if(createMedicineTime.isSuccessful) {
								Log.d(TAG, "createMedicineTime: ${createMedicineTime.body()}")
								dataManager.updateStr(DRUG_TIME, "uid", createMedicineTime.body()!!.uid, "id", getDrugTime[j].id)
							}else Log.e(TAG, "createMedicineTime: $createMedicineTime")
						}
					}else {
						Log.e(TAG, "updateMedicine: $updateMedicine")
						return
					}
				}else {
					val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", dto)

					if(response.isSuccessful) {
						Log.d(TAG, "createMedicine: ${response.body()}")
						dataManager.updateStr(DRUG, "uid", response.body()!!.uid, "id", data[i].id)

						val getDrugTime = dataManager.getDrugTime(data[i].id)

						for(j in 0 until getDrugTime.size) {
							val createMedicineTime = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response.body()!!.uid, MedicineTimeDTO(getDrugTime[j].time))

							if(createMedicineTime.isSuccessful) {
								Log.d(TAG, "createMedicineTime: ${createMedicineTime.body()}")
								dataManager.updateStr(DRUG_TIME, "uid", createMedicineTime.body()!!.uid, "id", getDrugTime[j].id)
							}else Log.d(TAG, "createMedicineTime: $createMedicineTime")
						}
					}else {
						Log.d(TAG, "createMedicine: $response")
						return
					}
				}
			}else {
				Log.e(TAG, "syncMedicine: $syncMedicine")
			}
		}
	}

	private suspend fun requestGoal(dataManager: DataManager, data: ArrayList<Goal>) {
		for(i in 0 until data.size) {
			var getUid = ""
			val syncGoal = RetrofitAPI.api.syncGoal("Bearer ${getToken.access}", SyncDTO(syncedAt))
			if(syncGoal.isSuccessful) {
				Log.d(TAG, "syncGoal: ${syncGoal.body()}")

				for (j in 0 until syncGoal.body()?.data!!.size) {
					val getDataId = dataManager.getDataId(DRUG, "starts", syncGoal.body()?.data!![j].date)
					if (getDataId > 0) getUid = syncGoal.body()?.data!![j].uid
				}

				if(getUid != "") {
					val dto = GoalDTO(data[i].body, data[i].food, data[i].exercise, data[i].waterVolume, data[i].water, data[i].sleep, data[i].drug)
					val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", dto) //uid있어야되는데 서버api에 없음(서버수정필요)

					if(response.isSuccessful) {
						Log.d(TAG, "updateGoal: ${response.body()}")
						dataManager.updateStr(GOAL, "uid", getUid, "id", data[i].id)
						dataManager.updateInt(GOAL, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.d(TAG, "updateGoal: $response")
						return
					}
				}else {
					val dateToIso = dateToIso(data[i].createdAt)
					val dto = GoalDTO(data[i].body, data[i].food, data[i].exercise, data[i].waterVolume, data[i].water, data[i].sleep, data[i].drug, dateToIso)
					val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", dto)

					if(response.isSuccessful) {
						Log.d(TAG, "createGoal: ${response.body()}")
						dataManager.updateStr(GOAL, "uid", response.body()!!.uid, "id", data[i].id)
					}else {
						Log.d(TAG, "createGoal: $response")
						return
					}
				}
			}else {
				Log.e(TAG, "syncGoal: $syncGoal")
			}
		}
	}

	suspend fun createSync(dataManager: DataManager):Boolean {
		syncedAt = dataManager.getSynced()

		val syncFood = RetrofitAPI.api.syncFood("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncFood.isSuccessful) {
			Log.d(TAG, "syncFood: ${syncFood.body()}")

			for(i in 0 until syncFood.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(FOOD, "name", syncFood.body()?.data!![i].foodName)
				if(getDataId > 0) {
					if(syncFood.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(FOOD, "id", getDataId)
					}else {
						dataManager.updateFood(Food(name = syncFood.body()?.data!![i].foodName,
							unit = syncFood.body()?.data!![i].volumeUnit, amount = syncFood.body()?.data!![i].volume, kcal = syncFood.body()?.data!![i].calories,
							carbohydrate = syncFood.body()?.data!![i].carbohydrate, protein = syncFood.body()?.data!![i].protein, fat = syncFood.body()?.data!![i].fat))
						dataManager.updateStr(FOOD, "uid", syncFood.body()?.data!![i].uid, "id", getDataId)
					}
				}else {
					val useDate = isoToDateTime(syncFood.body()?.data!![i].usages[0].updatedAt).toString()
					dataManager.insertFood(Food(uid = syncFood.body()?.data!![i].uid, name = syncFood.body()?.data!![i].foodName,
						unit = syncFood.body()?.data!![i].volumeUnit, amount = syncFood.body()?.data!![i].volume, kcal = syncFood.body()?.data!![i].calories,
						carbohydrate = syncFood.body()?.data!![i].carbohydrate, protein = syncFood.body()?.data!![i].protein, fat = syncFood.body()?.data!![i].fat,
						useCount = syncFood.body()?.data!![i].usages[0].usageCount, useDate = useDate))
				}
			}
		}else {
			Log.e(TAG, "syncFood: $syncFood")
		}

		val syncDiets = RetrofitAPI.api.syncDiets("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncDiets.isSuccessful) {
			Log.d(TAG, "syncDiets: ${syncDiets.body()}")

			for(i in 0 until syncDiets.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(DAILY_FOOD, "date", syncDiets.body()?.data!![i].date)
				if(getDataId > 0) {
					if(syncDiets.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(DAILY_FOOD, "id", getDataId)
					}else {
						dataManager.updateDailyFood(Food(amount = syncDiets.body()?.data!![i].volume, kcal = syncDiets.body()?.data!![i].calories,
							carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein, fat = syncDiets.body()?.data!![i].fat,
							count = syncDiets.body()?.data!![i].quantity))
						dataManager.updateStr(DAILY_FOOD, "uid", syncDiets.body()?.data!![i].uid, "id", getDataId)
					}
				}else {
					dataManager.insertDailyFood(Food(uid = syncDiets.body()?.data!![i].uid, type = syncDiets.body()?.data!![i].mealTime, name = syncDiets.body()?.data!![i].foodName,
						unit = syncDiets.body()?.data!![i].volumeUnit, amount = syncDiets.body()?.data!![i].volume, kcal = syncDiets.body()?.data!![i].calories,
						carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein, fat = syncDiets.body()?.data!![i].fat,
						count = syncDiets.body()?.data!![i].quantity, createdAt = syncDiets.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncDiets: $syncDiets")
		}

		val syncWater = RetrofitAPI.api.syncWater("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncWater.isSuccessful) {
			Log.d(TAG, "syncWater: ${syncWater.body()}")

			for(i in 0 until syncWater.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(WATER, "date", syncWater.body()?.data!![i].date)
				if(getDataId > 0) {
					if(syncWater.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(WATER, "id", getDataId)
					}else {
						dataManager.updateInt(WATER, "count", syncWater.body()?.data!![i].count, "id", getDataId)
						dataManager.updateStr(WATER, "uid", syncWater.body()?.data!![i].uid, "id", getDataId)
					}
				}else {
					dataManager.insertWater(Water(uid = syncWater.body()?.data!![i].uid, count = syncWater.body()?.data!![i].count,
						volume = syncWater.body()?.data!![i].mL, createdAt = syncWater.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncWater: $syncWater")
		}

		val syncActivity = RetrofitAPI.api.syncActivity("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncActivity.isSuccessful) {
			Log.d(TAG, "syncActivity: ${syncActivity.body()}")

			for(i in 0 until syncActivity.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(EXERCISE, "name", syncActivity.body()?.data!![i].name)
				if(getDataId > 0) {
					if(syncActivity.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(EXERCISE, "id", getDataId)
					}else {
						dataManager.updateExercise(EXERCISE, Exercise(id = getDataId, name = syncActivity.body()?.data!![i].name))
						dataManager.updateStr(EXERCISE, "uid", syncActivity.body()?.data!![i].uid, "id", getDataId)
					}
				}else {
					val useDate = isoToDateTime(syncActivity.body()?.data!![i].usages[0].updatedAt).toString()
					dataManager.insertExercise(Exercise(uid = syncActivity.body()?.data!![i].uid, name = syncActivity.body()?.data!![i].name,
						useCount = syncActivity.body()?.data!![i].usages[0].usageCount, useDate = useDate))
				}
			}
		}else {
			Log.e(TAG, "syncActivity: $syncActivity")
		}

		val syncWorkout = RetrofitAPI.api.syncWorkout("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncWorkout.isSuccessful) {
			Log.d(TAG, "syncWorkout: ${syncWorkout.body()}")

			for(i in 0 until syncWorkout.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(DAILY_EXERCISE, CREATED_AT, syncWorkout.body()?.data!![i].date)
				if(getDataId > 0) {
					if(syncWorkout.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(DAILY_EXERCISE, "id", getDataId)
					}else {
						dataManager.updateExercise(DAILY_EXERCISE, Exercise(id = getDataId, uid = syncWorkout.body()?.data!![i].uid, name = syncWorkout.body()?.data!![i].name,
							intensity = syncWorkout.body()?.data!![i].intensity, workoutTime = syncWorkout.body()?.data!![i].time, kcal = syncWorkout.body()?.data!![i].calories))
					}
				}else {
					dataManager.insertDailyExercise(Exercise(uid = syncWorkout.body()?.data!![i].uid, name = syncWorkout.body()?.data!![i].name,
						intensity = syncWorkout.body()?.data!![i].intensity, workoutTime = syncWorkout.body()?.data!![i].time,
						kcal = syncWorkout.body()?.data!![i].calories, createdAt = syncWorkout.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncWorkout: $syncWorkout")
		}

		val syncBody = RetrofitAPI.api.syncBody("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncBody.isSuccessful) { // 서버 workout 등록은 time밖에없음 (서버수정 필요)
			Log.d(TAG, "syncBody: ${syncBody.body()}")

			for(i in 0 until syncBody.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(BODY, CREATED_AT, syncBody.body()?.data!![i].uid) // uid 대신 date 로 변경해야함
				if(getDataId > 0) {
					if(syncBody.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(BODY, "id", getDataId)
					}else {
						dataManager.updateBody(Body(id = getDataId, uid = syncBody.body()?.data!![i].uid, height = syncBody.body()?.data!![i].height,
							weight = syncBody.body()?.data!![i].weight, intensity = syncBody.body()?.data!![i].workoutIntensity, fat = syncBody.body()?.data!![i].bodyFatPercentage,
							muscle = syncBody.body()?.data!![i].skeletalMuscleMass, bmi = syncBody.body()?.data!![i].bodyMassIndex, bmr = syncBody.body()?.data!![i].basalMetabolicRate))
					}
				}else {
					dataManager.insertBody(Body(uid = syncBody.body()?.data!![i].uid, height = syncBody.body()?.data!![i].height, weight = syncBody.body()?.data!![i].weight,
						intensity = syncBody.body()?.data!![i].workoutIntensity, fat = syncBody.body()?.data!![i].bodyFatPercentage,
						muscle = syncBody.body()?.data!![i].skeletalMuscleMass, bmi = syncBody.body()?.data!![i].bodyMassIndex, bmr = syncBody.body()?.data!![i].basalMetabolicRate,
						createdAt = ""))
				}
			}
		}else {
			Log.e(TAG, "syncBody: $syncBody")
		}

		val syncSleep = RetrofitAPI.api.syncSleep("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncSleep.isSuccessful) {
			Log.d(TAG, "syncSleep: ${syncSleep.body()}")

			for(i in 0 until syncSleep.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(SLEEP, CREATED_AT, syncSleep.body()?.data!![i].createdAt)
				if(getDataId > 0) {
					if(syncSleep.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(SLEEP, "id", getDataId)
					}else {
						dataManager.updateSleep(Sleep(id = getDataId, uid = syncSleep.body()?.data!![i].uid, startTime = syncSleep.body()?.data!![i].starts,
							endTime = syncSleep.body()?.data!![i].ends))
						dataManager.updateStr(SLEEP, "uid", syncSleep.body()?.data!![i].uid, "id", getDataId)
					}
				}else {
					dataManager.insertSleep(Sleep(uid = syncSleep.body()?.data!![i].uid, startTime = syncSleep.body()?.data!![i].starts,
						endTime = syncSleep.body()?.data!![i].ends, createdAt = syncSleep.body()?.data!![i].starts.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncSleep: $syncSleep")
		}

		val syncMedicine = RetrofitAPI.api.syncMedicine("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncMedicine.isSuccessful) {
			Log.d(TAG, "syncMedicine: ${syncMedicine.body()}")

			for(i in 0 until syncMedicine.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(DRUG, "starts", syncMedicine.body()?.data!![i].starts)
				if(getDataId > 0) {
					if(syncMedicine.body()?.data!![i].deletedAt != "") {
						dataManager.deleteItem(DRUG, "id", getDataId)
					}else {
						val drug = Drug(id = getDataId, uid = syncMedicine.body()?.data!![i].uid, type = syncMedicine.body()?.data!![i].category,
							name = syncMedicine.body()?.data!![i].name, amount = syncMedicine.body()?.data!![i].amount, unit = syncMedicine.body()?.data!![i].unit,
							startDate = syncMedicine.body()?.data!![i].starts.substring(0, 10), endDate = syncMedicine.body()?.data!![i].ends.substring(0, 10))

						dataManager.updateDrug(drug)

						dataManager.deleteItem(DRUG_TIME, "id", getDataId)

						val getMedicineTime = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", drug.uid)
						if(getMedicineTime.isSuccessful) {
							Log.d(TAG, "getMedicineTime: ${getMedicineTime.body()}")

							for(j in 0 until getMedicineTime.body()!!.size) {
								val drugTime = DrugTime(uid = getMedicineTime.body()!![j].uid, drugId = getDataId, time = getMedicineTime.body()!![j].time)
								dataManager.insertDrugTime(drugTime)

								val getMedicineIntake = RetrofitAPI.api.getMedicineIntake("Bearer ${getToken.access}", drug.uid, drugTime.uid)
								if(getMedicineIntake.isSuccessful) {
									Log.d(TAG, "getMedicineIntake: ${getMedicineIntake.body()}")

									val drugTimeId = dataManager.getDrugId(DRUG_TIME, "uid", drugTime.uid)
									for(k in 0 until getMedicineIntake.body()!!.size) {
										dataManager.insertDrugCheck(DrugCheck(uid = getMedicineIntake.body()!![k].uid, drugId = getDataId, drugTimeId = drugTimeId,
											createdAt = getMedicineIntake.body()!![k].intakeAt.substring(0, 10)))
									}
								}else {
									Log.e(TAG, "getMedicineIntake: $getMedicineIntake")
								}
							}
						}else {
							Log.e(TAG, "getMedicineTime: $getMedicineTime")
						}
					}
				}else {
					val drug = Drug(uid = syncMedicine.body()?.data!![i].uid, type = syncMedicine.body()?.data!![i].category,
						name = syncMedicine.body()?.data!![i].name, amount = syncMedicine.body()?.data!![i].amount, unit = syncMedicine.body()?.data!![i].unit,
						startDate = syncMedicine.body()?.data!![i].starts.substring(0, 10), endDate = syncMedicine.body()?.data!![i].ends.substring(0, 10))

					dataManager.insertDrug(drug)

					val getMedicineTime = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", drug.uid)
					if(getMedicineTime.isSuccessful) {
						Log.d(TAG, "getMedicineTime: ${getMedicineTime.body()}")

						val drugId = dataManager.getDrugId(DRUG, "startDate", drug.startDate)

						for(j in 0 until getMedicineTime.body()!!.size) {
							val drugTime = DrugTime(uid = getMedicineTime.body()!![j].uid, drugId = drugId, time = getMedicineTime.body()!![j].time)
							dataManager.insertDrugTime(drugTime)

							val getMedicineIntake = RetrofitAPI.api.getMedicineIntake("Bearer ${getToken.access}", drug.uid, drugTime.uid)
							if(getMedicineIntake.isSuccessful) {
								Log.d(TAG, "getMedicineIntake: ${getMedicineIntake.body()}")

								val drugTimeId = dataManager.getDrugId(DRUG_TIME, "uid", drugTime.uid)
								for(k in 0 until getMedicineIntake.body()!!.size) {
									dataManager.insertDrugCheck(DrugCheck(uid = getMedicineIntake.body()!![k].uid, drugId = drugId, drugTimeId = drugTimeId,
										createdAt = getMedicineIntake.body()!![k].intakeAt.substring(0, 10)))
								}
							}else {
								Log.e(TAG, "getMedicineIntake: $getMedicineIntake")
							}
						}
					}else {
						Log.e(TAG, "getMedicineTime: $getMedicineTime")
					}
				}
			}
		}else {
			Log.e(TAG, "syncMedicine: $syncMedicine")
		}

		val syncGoal = RetrofitAPI.api.syncGoal("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncGoal.isSuccessful) {
			Log.d(TAG, "syncGoal: ${syncGoal.body()}")

			for(i in 0 until syncGoal.body()?.data!!.size) {
				val getDataId = dataManager.getDataId(GOAL, CREATED_AT, syncGoal.body()?.data!![i].date)
				if(getDataId > 0) {
					dataManager.updateGoal(Goal(uid = syncGoal.body()?.data!![i].uid, food = syncGoal.body()?.data!![i].kcalOfDiet,
						waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
						exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight,
						sleep = syncGoal.body()?.data!![i].sleep, drug = syncGoal.body()?.data!![i].medicineIntake))
				}else {
					dataManager.insertGoal(Goal(uid = syncGoal.body()?.data!![i].uid, food = syncGoal.body()?.data!![i].kcalOfDiet,
						waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
						exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight, sleep = syncGoal.body()?.data!![i].sleep,
						drug = syncGoal.body()?.data!![i].medicineIntake, createdAt = syncGoal.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncGoal: $syncGoal")
		}

		val updateSync = dateTimeToIso(LocalDateTime.parse(syncedAt).plusSeconds(1))
		dataManager.updateStr(SYNC_TIME, "syncedAt", updateSync, USER_ID, MyApp.prefs.getId())

		return true
	}

	suspend fun refreshToken(dataManager: DataManager) {
		val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
		val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

		if (accessDiff.toHours() in 1..335) {
			val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
			if(response.isSuccessful) {
				Log.d(TAG, "refreshToken: ${response.body()}")
				dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
				getToken = dataManager.getToken()
			}else {
				Log.d(TAG, "refreshToken: $response")
			}
		}

		if(refreshDiff.toHours() >= 336) {
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
					} else {
						Log.e(TAG, "googleLogin: $response")
					}
				}
			}
		}
	}
}