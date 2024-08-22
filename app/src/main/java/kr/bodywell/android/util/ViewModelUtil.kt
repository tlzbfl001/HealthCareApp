package kr.bodywell.android.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.ActivityData
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.FoodData
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
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Drug
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Goal
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.dateToIso
import kr.bodywell.android.util.CustomUtil.isoToDateTime
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ViewModelUtil {
	var getUser = User()
	var getToken = Token()
	var syncCheck: Boolean = false
	var requestStatus: Boolean = true
	private var syncedAt = ""

	suspend fun createApiRequest(dataManager: DataManager) {
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
		val getGoalUid = dataManager.getGoalUid()
		val getGoalUpdated = dataManager.getGoalUpdated()

		val getUnused = dataManager.getUnused()
		for(i in 0 until getUnused.size) {
			when(getUnused[i].type) {
				FOOD -> {
					val getFood = RetrofitAPI.api.getFood("Bearer ${getToken.access}", getUnused[i].value)

					if(getFood.isSuccessful) {
						val deleteFood = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteFood.isSuccessful) {
							Log.d(TAG, "deleteFood: ${deleteFood.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteFood: $deleteFood")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getFood: $getFood")
						if(getFood.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				DAILY_FOOD -> {
					val getDiet = RetrofitAPI.api.getDiet("Bearer ${getToken.access}", getUnused[i].value)

					if(getDiet.isSuccessful) {
						val deleteDiets = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteDiets.isSuccessful) {
							Log.d(TAG, "deleteDiets: ${deleteDiets.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteDiets: $deleteDiets")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getDiet: $getDiet")
						if(getDiet.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				WATER -> {
					val getWater = RetrofitAPI.api.getWater("Bearer ${getToken.access}", getUnused[i].value)

					if(getWater.isSuccessful) {
						val deleteWater = RetrofitAPI.api.deleteWater("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteWater.isSuccessful) {
							Log.d(TAG, "deleteWater: ${deleteWater.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteWater: $deleteWater")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getWater: $getWater")
						if(getWater.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				EXERCISE -> {
					val getActivity = RetrofitAPI.api.getActivity("Bearer ${getToken.access}", getUnused[i].value)

					if(getActivity.isSuccessful) {
						val deleteActivity = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteActivity.isSuccessful) {
							Log.d(TAG, "deleteActivity: ${deleteActivity.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteActivity: $deleteActivity")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getActivity: $getActivity")
						if(getActivity.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				DAILY_EXERCISE -> {
					val getWorkout = RetrofitAPI.api.getWorkout("Bearer ${getToken.access}", getUnused[i].value)

					if(getWorkout.isSuccessful) {
						val deleteWorkout = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteWorkout.isSuccessful) {
							Log.d(TAG, "deleteWorkout: ${deleteWorkout.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteWorkout: $deleteWorkout")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getWorkout: $getWorkout")
						if(getWorkout.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				DRUG_TIME -> {
					val getMedicineTime = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].value)

					if(getMedicineTime.isSuccessful) {
						val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].value)

						if(deleteMedicineTime.isSuccessful) {
							Log.d(TAG, "deleteMedicineTime: ${deleteMedicineTime.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteMedicineTime: $deleteMedicineTime")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getMedicineTime: $getMedicineTime")
						if(getMedicineTime.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
					}
				}
				DRUG -> {
					val getMedicine = RetrofitAPI.api.getMedicine("Bearer ${getToken.access}", getUnused[i].value)

					if(getMedicine.isSuccessful) {
						val deleteMedicine = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)

						if(deleteMedicine.isSuccessful) {
							Log.d(TAG, "deleteMedicine: ${deleteMedicine.body()}")
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							Log.e(TAG, "deleteMedicine: $deleteMedicine")
							requestStatus = false
							return
						}
					}else {
						Log.e(TAG, "getMedicine: $getMedicine")
						if(getMedicine.code() == 404) {
							dataManager.deleteItem(UNUSED, "id", getUnused[i].id)
						}else {
							requestStatus = false
							return
						}
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
				Log.e(TAG, "updateProfile: $response")
			}
		}

		requestFood(dataManager, getFoodUid, 1)

		requestFood(dataManager, getFoodUpdated, 2)

		requestDiet(dataManager, getDailyFoodUid, 1)

		requestDiet(dataManager, getDailyFoodUpdated, 2)

		requestWater(dataManager, getWaterUid, 1)

		requestWater(dataManager, getWaterUpdated, 2)

		requestActivity(dataManager, getExUid)

		requestActivity(dataManager, getExUpdated)

		for(i in 0 until getDailyExUid.size) {
			val dateToIso = dateToIso(getDailyExUid[i].createdAt)
			val getData = dataManager.getData(EXERCISE, "name", getDailyExUid[i].name)
			val activity: ActivityData? = if(getData.uid != "") ActivityData(getData.uid) else null
			val dto = WorkoutDTO(getDailyExUid[i].name, getDailyExUid[i].kcal, getDailyExUid[i].intensity, getDailyExUid[i].workoutTime, dateToIso, activity)

			val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", dto)
			if(response.isSuccessful) {
				Log.d(TAG, "createWorkout: ${response.body()}")
				dataManager.updateStr(DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExUid[i].id)
			}else {
				Log.e(TAG, "createWorkout: $response")
				requestStatus = false
				return
			}
		}

		for(i in 0 until getDailyExUpdated.size) {
			val getWorkout = RetrofitAPI.api.getWorkout("Bearer ${getToken.access}", getDailyExUpdated[i].uid)

			if(getWorkout.isSuccessful) {
				val dto = WorkoutUpdateDTO(getDailyExUpdated[i].kcal, getDailyExUpdated[i].intensity, getDailyExUpdated[i].workoutTime)

				val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExUpdated[i].uid, dto)
				if(response.isSuccessful) {
					Log.d(TAG, "updateWorkout: ${response.body()}")
					dataManager.updateInt(DAILY_EXERCISE, IS_UPDATED, 0, "id", getDailyExUpdated[i].id)
				}else {
					Log.e(TAG, "updateWorkout: $response")
					requestStatus = false
					return
				}
			}else {
				Log.e(TAG, "getWorkout: $getWorkout")

				if(getWorkout.code() == 404) {
					val dateToIso = dateToIso(getDailyExUpdated[i].createdAt)
					val getData = dataManager.getData(EXERCISE, "name", getDailyExUpdated[i].name)
					val activity: ActivityData? = if(getData.uid != "") ActivityData(getData.uid) else null
					val dto = WorkoutDTO(getDailyExUpdated[i].name, getDailyExUpdated[i].kcal, getDailyExUpdated[i].intensity, getDailyExUpdated[i].workoutTime, dateToIso, activity)

					val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", dto)
					if(response.isSuccessful) {
						Log.d(TAG, "createWorkout: ${response.body()}")
						dataManager.updateStr(DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExUpdated[i].id)
					}else {
						Log.e(TAG, "createWorkout: $response")
						requestStatus = false
						return
					}
				}else {
					requestStatus = false
					return
				}
			}
		}

		requestBody(dataManager, getBodyUid, 1)

		requestBody(dataManager, getBodyUpdated, 2)

		requestSleep(dataManager, getSleepUid, 1)

		requestSleep(dataManager, getSleepUpdated, 2)

		requestMedicine(dataManager, getDrugUid)

		requestMedicine(dataManager, getDrugUpdated)

		requestGoal(dataManager, getGoalUid, 1)

		requestGoal(dataManager, getGoalUpdated, 2)

		delay(5000)
	}

	private suspend fun requestFood(dataManager: DataManager, data: ArrayList<Food>, type: Int) {
		for(i in 0 until data.size) {
			if(type == 1) {
				val dto = FoodDTO(data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat, data[i].count, "개", data[i].amount, data[i].unit)

				val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", dto)
				if(response.isSuccessful) {
					Log.d(TAG, "createFood: ${response.body()}")
					dataManager.updateStr(FOOD, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createFood: $response")
					requestStatus = false
					return
				}
			}else {
				val dto = FoodUpdateDTO(data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat, data[i].count, "개", data[i].amount, data[i].unit)

				val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", data[i].uid, dto) // data[i].uid = 서버에서가져온 uid로 수정
				if(response.isSuccessful) {
					Log.d(TAG, "updateFood: ${response.body()}")
					dataManager.updateStr(FOOD, "uid", data[i].uid, "id", data[i].id) // uid 업데이트, data[i].uid = 서버에서가져온 uid로 수정
					dataManager.updateInt(FOOD, IS_UPDATED, 0, "id", data[i].id) // 업데이트 체크 0
				}else {
					Log.e(TAG, "updateFood: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	private suspend fun requestDiet(dataManager: DataManager, data: ArrayList<Food>, type: Int) {
		for(i in 0 until data.size) {
			val photos = ArrayList<String>()
//				val getImage = dataManager.getImage(data[i].type, data[i].createdAt)
//          for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
			photos.add("https://example.com/picture.jpg")

			val dateToIso = dateToIso(data[i].createdAt)

			if(type == 1) {
				val getData = dataManager.getData(FOOD, "name", data[i].name)
				val food: FoodData? = if(getData.uid != "") FoodData(getData.uid) else null
				val dto = DietDTO(data[i].type, data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat, data[i].count, "개",
					data[i].amount, data[i].unit, photos, dateToIso, food)

				val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", dto)
				if(response.isSuccessful) {
					Log.d(TAG, "createDiets: ${response.body()}")
					dataManager.updateStr(DAILY_FOOD, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createDiets: $response")
					requestStatus = false
					return
				}
			}else {
				val dto = DietUpdateDTO(data[i].type, data[i].name, data[i].kcal, data[i].carbohydrate, data[i].protein, data[i].fat, data[i].count, "개",
					data[i].amount, data[i].unit, photos, dateToIso)

				val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", data[i].uid, dto)
				if(response.isSuccessful) {
					Log.d(TAG, "updateDiets: ${response.body()}")
					dataManager.updateStr(DAILY_FOOD, "uid", data[i].uid, "id", data[i].id)
					dataManager.updateInt(DAILY_FOOD, IS_UPDATED, 0, "id", data[i].id)
				}else {
					Log.e(TAG, "updateDiets: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	private suspend fun requestWater(dataManager: DataManager, data: ArrayList<Water>, type: Int) {
		for(i in 0 until data.size) {
			val dto = WaterDTO(data[i].volume, data[i].count, data[i].createdAt)

			if(type == 1) {
				val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", dto)
				if(response.isSuccessful) {
					Log.d(TAG, "createWater: ${response.body()}")
					dataManager.updateStr(WATER, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createWater: $response")
					requestStatus = false
					return
				}
			}else {
				val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", data[i].uid!!, dto)
				if(response.isSuccessful) {
					Log.d(TAG, "updateWater: ${response.body()}")
					dataManager.updateStr(WATER, "uid", data[i].uid!!, "id", data[i].id)
					dataManager.updateInt(WATER, IS_UPDATED, 0, "id", data[i].id)
				}else {
					Log.e(TAG, "updateWater: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	private suspend fun requestActivity(dataManager: DataManager, data: ArrayList<Exercise>) {
		for(i in 0 until data.size) {
			val getCheckActivity = RetrofitAPI.api.getCheckActivity("Bearer ${getToken.access}", data[i].name)

			if(getCheckActivity.isSuccessful) {
				if(getCheckActivity.body()!!.exists) {
					val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", data[i].uid, ActivityDTO(data[i].name)) // data[i].uid = 서버데이터 uid로
					if(response.isSuccessful) {
						Log.d(TAG, "updateActivity: ${response.body()}")
						dataManager.updateStr(EXERCISE, "uid", response.body()!!.uid, "id", data[i].id)
						dataManager.updateInt(EXERCISE, IS_UPDATED, 0, "id", data[i].id)
					}else {
						Log.e(TAG, "updateActivity: $response")
						requestStatus = false
						return
					}
				}else {
					val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", ActivityDTO(data[i].name))
					if(response.isSuccessful) {
						Log.d(TAG, "createActivity: ${response.body()}")
						dataManager.updateStr(EXERCISE, "uid", response.body()!!.uid, "id", data[i].id)
					}else {
						Log.e(TAG, "createActivity: $response")
						requestStatus = false
						return
					}
				}
			}else {
				Log.e(TAG, "getCheckActivity: $getCheckActivity")
				requestStatus = false
				return
			}
		}
	}

	private suspend fun requestBody(dataManager: DataManager, data: ArrayList<Body>, type: Int) {
		for(i in 0 until data.size) {
			val dateToIso = dateToIso(data[i].createdAt)
			val dto = BodyDTO(data[i].height, data[i].weight, data[i].bmi, data[i].fat, data[i].muscle, data[i].bmr, data[i].intensity, dateToIso)

			if(type == 1) {
				val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", dto)
				if(response.isSuccessful) {
					Log.d(TAG, "createBody: ${response.body()}")
					dataManager.updateStr(BODY, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createBody: $response")
					requestStatus = false
					return
				}
			}else {
				val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", data[i].uid!!, dto)
				if(response.isSuccessful) {
					Log.d(TAG, "updateBody: ${response.body()}")
					dataManager.updateInt(BODY, IS_UPDATED, 0, "id", data[i].id)
					dataManager.updateStr(BODY, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "updateBody: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	private suspend fun requestSleep(dataManager: DataManager, data: ArrayList<Sleep>, type: Int) {
		for(i in 0 until data.size) {
			val startTimeToIso = dateTimeToIso(LocalDateTime.parse(data[i].startTime))
			val endTimeToIso = dateTimeToIso(LocalDateTime.parse(data[i].endTime))

			if(type == 1) {
				val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", SleepDTO(startTimeToIso, endTimeToIso))
				if(response.isSuccessful) {
					Log.d(TAG, "createSleep: ${response.body()}")
					dataManager.updateStr(SLEEP, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createSleep: $response")
					requestStatus = false
					return
				}
			}else {
				val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", data[i].uid, SleepDTO(startTimeToIso, endTimeToIso))
				if(response.isSuccessful) {
					Log.d(TAG, "updateSleep: ${response.body()}")
					dataManager.updateStr(SLEEP, "uid", response.body()!!.uid, "id", data[i].id)
					dataManager.updateInt(SLEEP, IS_UPDATED, 0, "id", data[i].id)
				}else {
					Log.e(TAG, "updateSleep: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	private suspend fun requestMedicine(dataManager: DataManager, data: ArrayList<Drug>) {
		for(i in 0 until data.size) {
			val getCheckMedicine = RetrofitAPI.api.getCheckMedicine("Bearer ${getToken.access}", data[i].startDate)

			if(getCheckMedicine.isSuccessful) {
				val startDate = dateToIso(data[i].startDate)
				val endDate = dateToIso(data[i].endDate)
				val dto = MedicineDTO(data[i].type, data[i].name, data[i].amount, data[i].unit, startDate, endDate)

				if(getCheckMedicine.body()!!.exists) {
					val updateMedicine = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", data[i].uid, dto)
					if(updateMedicine.isSuccessful) {
						Log.d(TAG, "updateMedicine: ${updateMedicine.body()}")

						val getUnusedTime = dataManager.getUnusedTime()
						for(j in 0 until getUnusedTime.size) {
							val getMedicineTime = RetrofitAPI.api.getMedicineTime("Bearer ${getToken.access}", getUnusedTime[i].drugUid, getUnusedTime[i].value)
							if(getMedicineTime.isSuccessful) {
								val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getUnusedTime[i].drugUid, getUnusedTime[i].value)
								if(deleteMedicineTime.isSuccessful) {
									Log.d(TAG, "deleteMedicineTime: ${deleteMedicineTime.body()}")
									dataManager.deleteItem(UNUSED, "id", getUnusedTime[i].id)
								}else {
									Log.e(TAG, "deleteMedicineTime: $deleteMedicineTime")
									requestStatus = false
									return
								}
							}else {
								Log.e(TAG, "getMedicineTime: $getMedicineTime")
								if(getMedicineTime.code() == 404) {
									dataManager.deleteItem(UNUSED, "id", getUnusedTime[i].id)
								}else {
									requestStatus = false
									return
								}
							}
						}

						if(requestStatus) {
							dataManager.updateStr(DRUG, "uid", data[i].uid, "id", data[i].id)
							dataManager.updateInt(DRUG, IS_UPDATED, 0, "id", data[i].id)

							val getDrugTime = dataManager.getDrugTimeData(data[i].id)
							for(j in 0 until getDrugTime.size) {
								val createMedicineTime = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", data[i].uid, MedicineTimeDTO(getDrugTime[j].time))
								if(createMedicineTime.isSuccessful) {
									Log.d(TAG, "createMedicineTime: ${createMedicineTime.body()}")
									dataManager.updateStr(DRUG_TIME, "uid", createMedicineTime.body()!!.uid, "id", getDrugTime[j].id)
								}else {
									Log.e(TAG, "createMedicineTime: $createMedicineTime")
									requestStatus = false
									return
								}
							}
						}
					}else {
						Log.e(TAG, "updateMedicine: $updateMedicine")
						requestStatus = false
						return
					}
				}else {
					val createMedicine = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", dto)
					if(createMedicine.isSuccessful) {
						Log.d(TAG, "createMedicine: ${createMedicine.body()}")
						dataManager.updateStr(DRUG, "uid", createMedicine.body()!!.uid, "id", data[i].id)

						val getDrugTime = dataManager.getDrugTime(data[i].id)
						for(j in 0 until getDrugTime.size) {
							val createMedicineTime = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", createMedicine.body()!!.uid, MedicineTimeDTO(getDrugTime[j].time))
							if(createMedicineTime.isSuccessful) {
								Log.d(TAG, "createMedicineTime: ${createMedicineTime.body()}")
								dataManager.updateStr(DRUG_TIME, "uid", createMedicineTime.body()!!.uid, "id", getDrugTime[j].id)
							}else {
								Log.e(TAG, "createMedicineTime: $createMedicineTime")
								requestStatus = false
								return
							}
						}
					}else {
						Log.e(TAG, "createMedicine: $createMedicine")
						requestStatus = false
						return
					}
				}
			}else {
				Log.e(TAG, "getCheckMedicine: $getCheckMedicine")
				requestStatus = false
				return
			}
		}
	}

	private suspend fun requestGoal(dataManager: DataManager, data: ArrayList<Goal>, type: Int) {
		for(i in 0 until data.size) {
			if(type == 1) {
				val dateToIso = dateToIso(data[i].createdAt)
				val dto = GoalDTO(data[i].body, data[i].food, data[i].exercise, data[i].waterVolume, data[i].water, data[i].sleep, data[i].drug, dateToIso)

				val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", dto)
				if(response.isSuccessful) {
					Log.d(TAG, "createGoal: ${response.body()}")
					dataManager.updateStr(GOAL, "uid", response.body()!!.uid, "id", data[i].id)
				}else {
					Log.e(TAG, "createGoal: $response")
					requestStatus = false
					return
				}
			}else {
				val dto = GoalUpdateDTO(data[i].body, data[i].food, data[i].exercise, data[i].waterVolume, data[i].water, data[i].sleep, data[i].drug)

				val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", data[i].uid, dto)
				if(response.isSuccessful) {
					Log.d(TAG, "updateGoal: ${response.body()}")
					dataManager.updateStr(GOAL, "uid", data[i].uid, "id", data[i].id)
					dataManager.updateInt(GOAL, IS_UPDATED, 0, "id", data[i].id)
				}else {
					Log.e(TAG, "updateGoal: $response")
					requestStatus = false
					return
				}
			}
		}
	}

	suspend fun createSync(context: Context, dataManager: DataManager):Boolean {
		syncedAt = dateTimeToIso(LocalDateTime.parse(dataManager.getSynced()))
		Log.d(TAG, "syncedAt: $syncedAt / ${dataManager.getSynced()}")

		val syncProfile = RetrofitAPI.api.syncProfile("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncProfile.isSuccessful) {
			Log.d(TAG, "syncProfile: ${syncProfile.body()}")

			if(syncProfile.body()?.data != null) {
				dataManager.updateProfile(User(name=syncProfile.body()?.data!!.name, gender=syncProfile.body()?.data!!.gender, birthday=syncProfile.body()?.data!!.birth,
					height=syncProfile.body()?.data!!.height, weight=syncProfile.body()?.data!!.weight))
			}
		}else {
			Log.e(TAG, "syncProfile: $syncProfile")
		}

		val syncFood = RetrofitAPI.api.syncFood("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncFood.isSuccessful) {
			Log.d(TAG, "syncFood: ${syncFood.body()}")

			for(i in 0 until syncFood.body()?.data!!.size) {
				val getData = dataManager.getData(FOOD, "name", syncFood.body()?.data!![i].name)

				if(getData.id > 0) {
					if(syncFood.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(FOOD, "id", getData.id)
					}else {
						dataManager.updateFood(Food(id = getData.id, unit = syncFood.body()?.data!![i].volumeUnit, amount = syncFood.body()?.data!![i].volume,
							kcal = syncFood.body()?.data!![i].calorie, carbohydrate = syncFood.body()?.data!![i].carbohydrate, protein = syncFood.body()?.data!![i].protein,
							fat = syncFood.body()?.data!![i].fat))

						dataManager.updateData1(FOOD, syncFood.body()?.data!![i].registerType, syncFood.body()?.data!![i].uid, getData.id)

						if(syncFood.body()?.data!![i].usages != null) {
							val updatedAt = isoToDateTime(syncFood.body()?.data!![i].usages!![0].updatedAt).toString()
							dataManager.updateData2(FOOD, syncFood.body()?.data!![i].usages!![0].usageCount, updatedAt, getData.id)
						}
					}
				}else if(syncFood.body()?.data!![i].createdAt != null && syncFood.body()?.data!![i].deletedAt == null) {
					var useCount = 0
					var useDate = ""
					if(syncFood.body()?.data!![i].usages != null) {
						useCount = syncFood.body()?.data!![i].usages!![0].usageCount
						useDate = isoToDateTime(syncFood.body()?.data!![i].usages!![0].updatedAt).toString()
					}

					dataManager.insertFood(Food(registerType=syncFood.body()?.data!![i].registerType, uid = syncFood.body()?.data!![i].uid,
						name = syncFood.body()?.data!![i].name, unit = syncFood.body()?.data!![i].volumeUnit, amount = syncFood.body()?.data!![i].volume,
						kcal = syncFood.body()?.data!![i].calorie, carbohydrate = syncFood.body()?.data!![i].carbohydrate,
						protein = syncFood.body()?.data!![i].protein, fat = syncFood.body()?.data!![i].fat, useCount = useCount, useDate = useDate))
				}
			}
		}else {
			Log.e(TAG, "syncFood: ${syncFood.code()}")
		}

		val syncDiets = RetrofitAPI.api.syncDiets("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncDiets.isSuccessful) {
			Log.d(TAG, "syncDiets: ${syncDiets.body()}")

			for(i in 0 until syncDiets.body()?.data!!.size) {
				val getFood = dataManager.getData(FOOD, "name", syncDiets.body()?.data!![i].name)
				val getDailyFood = dataManager.getData(DAILY_FOOD, "name", syncDiets.body()?.data!![i].name)

				if(getFood.uid != "") {
					val response = RetrofitAPI.api.getFood("Bearer ${getToken.access}", getFood.uid)
					if(response.isSuccessful) {
						if(response.body()?.usages!!.isNotEmpty()) {
							val useDate = isoToDateTime(response.body()?.usages!![0].updatedAt).toString()
							dataManager.updateInt(FOOD, "useCount", response.body()?.usages!![0].usageCount, "id", getFood.id)
							dataManager.updateStr(FOOD, "useDate", useDate, "id", getFood.id)
						}
					}else {
						Log.e(TAG, "getFood: $response")
					}
				}

				if(getDailyFood.id > 0) {
					if(syncDiets.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DAILY_FOOD, "id", getDailyFood.id)
					}else {
						dataManager.updateDailyFood(Food(id = getDailyFood.id, unit = syncDiets.body()?.data!![i].volumeUnit, amount = syncDiets.body()?.data!![i].volume,
							kcal = syncDiets.body()?.data!![i].calorie, carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein,
							fat = syncDiets.body()?.data!![i].fat, count = syncDiets.body()?.data!![i].quantity))
						dataManager.updateStr(DAILY_FOOD, "uid", syncDiets.body()?.data!![i].uid, "id", getDailyFood.id)
					}
				}else if(syncDiets.body()?.data!![i].createdAt != null && syncDiets.body()?.data!![i].deletedAt == null) {
					dataManager.insertDailyFood(Food(uid = syncDiets.body()?.data!![i].uid, type = syncDiets.body()?.data!![i].mealTime, name = syncDiets.body()?.data!![i].name,
						unit = syncDiets.body()?.data!![i].volumeUnit, amount = syncDiets.body()?.data!![i].volume, kcal = syncDiets.body()?.data!![i].calorie,
						carbohydrate = syncDiets.body()?.data!![i].carbohydrate, protein = syncDiets.body()?.data!![i].protein, fat = syncDiets.body()?.data!![i].fat,
						count = syncDiets.body()?.data!![i].quantity, createdAt = syncDiets.body()?.data!![i].date.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncDiets: ${syncDiets.code()}")
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
						dataManager.updateStr(WATER, "uid", syncWater.body()?.data!![i].uid, "id", getData.id)
					}
				}else if(syncWater.body()?.data!![i].createdAt != null && syncWater.body()?.data!![i].deletedAt == null) {
					dataManager.insertWater(Water(uid = syncWater.body()?.data!![i].uid, count = syncWater.body()?.data!![i].count,
						volume = syncWater.body()?.data!![i].mL, createdAt = syncWater.body()?.data!![i].date))
				}
			}
		}else {
			Log.e(TAG, "syncWater: ${syncWater.code()}")
		}

		val syncActivity = RetrofitAPI.api.syncActivity("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncActivity.isSuccessful) {
			Log.d(TAG, "syncActivity: ${syncActivity.body()}")

			for(i in 0 until syncActivity.body()?.data!!.size) {
				val getData = dataManager.getData(EXERCISE, "name", syncActivity.body()?.data!![i].name)

				if(getData.id > 0) {
					if(syncActivity.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(EXERCISE, "uid", syncActivity.body()?.data!![i].uid)
					}else {
						dataManager.updateData1(FOOD, syncActivity.body()?.data!![i].registerType, syncActivity.body()?.data!![i].uid, getData.id)

						if(syncActivity.body()?.data!![i].usages != null) {
							val updatedAt = isoToDateTime(syncActivity.body()?.data!![i].usages!![0].updatedAt).toString()
							dataManager.updateData2(FOOD, syncActivity.body()?.data!![i].usages!![0].usageCount, updatedAt, getData.id)
						}
					}
				}else {
					var useCount = 0
					var useDate = ""
					if(syncActivity.body()?.data!![i].usages!!.isNotEmpty()) {
						useCount = syncActivity.body()?.data!![i].usages!![0].usageCount
						useDate = isoToDateTime(syncActivity.body()?.data!![i].usages!![0].updatedAt).toString()
					}

					dataManager.insertExercise(Exercise(registerType = syncActivity.body()?.data!![i].registerType, uid = syncActivity.body()?.data!![i].uid,
						name = syncActivity.body()?.data!![i].name, useCount = useCount, useDate = useDate))
				}
			}
		}else {
			Log.e(TAG, "syncActivity: ${syncActivity.code()}")
		}

		val syncWorkout = RetrofitAPI.api.syncWorkout("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncWorkout.isSuccessful) {
			Log.d(TAG, "syncWorkout: ${syncWorkout.body()}")

			for(i in 0 until syncWorkout.body()?.data!!.size) {
				val getExercise = dataManager.getData(EXERCISE, "name", syncWorkout.body()?.data!![i].name)
				val getDailyExercise = dataManager.getData(DAILY_EXERCISE, "uid", syncWorkout.body()?.data!![i].uid)

				if(getExercise.uid != "") {
					val getActivity = RetrofitAPI.api.getActivity("Bearer ${getToken.access}", getExercise.uid)
					if(getActivity.isSuccessful) {
						if(getActivity.body()?.usages!!.isNotEmpty()) {
							val useDate = isoToDateTime(getActivity.body()?.usages!![0].updatedAt).toString()
							dataManager.updateInt(EXERCISE, "useCount", getActivity.body()?.usages!![0].usageCount, "id", getExercise.id)
							dataManager.updateStr(EXERCISE, "useDate", useDate, "id", getExercise.id)
						}
					}else {
						Log.e(TAG, "getActivity: ${getActivity.code()}")
					}
				}

				if(getDailyExercise.id > 0) {
					if(syncWorkout.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DAILY_EXERCISE, "id", getDailyExercise.id)
					}else {
						dataManager.updateDailyExercise(Exercise(id = getDailyExercise.id, intensity = syncWorkout.body()?.data!![i].intensity,
							workoutTime = syncWorkout.body()?.data!![i].time, kcal = syncWorkout.body()?.data!![i].calorie))
						dataManager.updateStr(DAILY_EXERCISE, "uid", syncWorkout.body()?.data!![i].uid, "id", getDailyExercise.id)
					}
				}else if(syncWorkout.body()?.data!![i].createdAt != null && syncWorkout.body()?.data!![i].deletedAt == null) {
					dataManager.insertDailyExercise(Exercise(uid = syncWorkout.body()?.data!![i].uid, name = syncWorkout.body()?.data!![i].name,
						intensity = syncWorkout.body()?.data!![i].intensity, workoutTime = syncWorkout.body()?.data!![i].time,
						kcal = syncWorkout.body()?.data!![i].calorie, createdAt = syncWorkout.body()?.data!![i].date.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncWorkout: $syncWorkout")
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
						dataManager.updateStr(BODY, "uid", syncBody.body()?.data!![i].uid, "id", getData.id)
					}
				}else if(syncBody.body()?.data!![i].createdAt != null && syncBody.body()?.data!![i].deletedAt == null) {
					dataManager.insertBody(Body(uid = syncBody.body()?.data!![i].uid, height = syncBody.body()?.data!![i].height, weight = syncBody.body()?.data!![i].weight,
						intensity = syncBody.body()?.data!![i].workoutIntensity, fat = syncBody.body()?.data!![i].bodyFatPercentage,
						muscle = syncBody.body()?.data!![i].skeletalMuscleMass, bmi = syncBody.body()?.data!![i].bodyMassIndex, bmr = syncBody.body()?.data!![i].basalMetabolicRate,
						createdAt = syncBody.body()?.data!![i].time.substring(0, 10)))
				}
			}
		}else {
			Log.e(TAG, "syncBody: $syncBody")
		}

		val syncSleep = RetrofitAPI.api.syncSleep("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncSleep.isSuccessful) {
			Log.d(TAG, "syncSleep: ${syncSleep.body()}")

			for(i in 0 until syncSleep.body()?.data!!.size) {
				val startTime = isoToDateTime(syncSleep.body()?.data!![i].starts).toString()
				val endTime = isoToDateTime(syncSleep.body()?.data!![i].ends).toString()
				val getData = dataManager.getSleep(SLEEP, "startTime", startTime.substring(0, 10))

				if(getData.id > 0) {
					if(syncSleep.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(SLEEP, "id", getData.id)
					}else {
						dataManager.updateSleep(Sleep(id = getData.id, startTime = startTime, endTime = endTime))
						dataManager.updateStr(SLEEP, "uid", syncSleep.body()?.data!![i].uid, "id", getData.id)
					}
				}else if(syncSleep.body()?.data!![i].createdAt != null && syncSleep.body()?.data!![i].deletedAt == null) {
					dataManager.insertSleep(Sleep(uid = syncSleep.body()?.data!![i].uid, startTime = startTime, endTime = endTime))
				}
			}
		}else {
			Log.e(TAG, "syncSleep: $syncSleep")
		}

		val syncMedicine = RetrofitAPI.api.syncMedicine("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncMedicine.isSuccessful) {
			Log.d(TAG, "syncMedicine: ${syncMedicine.body()}")

			val alarmReceiver = AlarmReceiver()
			val timeList = ArrayList<DrugTime>()

			for(i in 0 until syncMedicine.body()?.data!!.size) {
				val startDate = LocalDate.parse(syncMedicine.body()?.data!![i].starts.substring(0, 10))
				val endDate = LocalDate.parse(syncMedicine.body()?.data!![i].ends.substring(0, 10))
				val count = startDate.until(endDate, ChronoUnit.DAYS) + 1 // 약복용 날짜 횟수
				val getData = dataManager.getData(DRUG, "startDate", startDate.toString()) // medicine 중복 데이터 조회

				val drug = Drug(id = getData.id, uid = syncMedicine.body()?.data!![i].uid, type = syncMedicine.body()?.data!![i].category,
					name = syncMedicine.body()?.data!![i].name, amount = syncMedicine.body()?.data!![i].amount, unit = syncMedicine.body()?.data!![i].unit,
					count = count.toInt(), startDate = startDate.toString(), endDate = endDate.toString())

				val getAllMedicineTime = RetrofitAPI.api.getAllMedicineTime("Bearer ${getToken.access}", drug.uid)

				if(getData.id > 0) {
					if(syncMedicine.body()?.data!![i].deletedAt != null) {
						dataManager.deleteItem(DRUG, "id", getData.id)
						dataManager.deleteItem(DRUG_TIME, "drugId", getData.id)
						dataManager.deleteItem(DRUG_CHECK, "drugId", getData.id)
						alarmReceiver.cancelAlarm(context, getData.id)
					}else {
						if(getAllMedicineTime.isSuccessful) {
							Log.d(TAG, "getAllMedicineTime: ${getAllMedicineTime.body()}")

							dataManager.updateDrug(drug) // drug 업데이트
							dataManager.deleteItem(DRUG_TIME, "drugId", getData.id) // drugTime 데이터 삭제
							dataManager.deleteItem(DRUG_CHECK, "drugId", getData.id) // drugCheck 데이터 삭제

							for(j in 0 until getAllMedicineTime.body()!!.size) {
								val drugTime = DrugTime(uid = getAllMedicineTime.body()!![j].uid, drugId = getData.id, time = getAllMedicineTime.body()!![j].time)
								dataManager.insertDrugTime(drugTime) // drugTime 데이터 저장
								timeList.add(DrugTime(time = drugTime.time))
							}

							alarmReceiver.setAlarm(context, getData.id, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}") // 알람 재등록
						}else {
							Log.e(TAG, "getAllMedicineTime: $getAllMedicineTime")
						}
					}
				}else if(syncMedicine.body()?.data!![i].createdAt != null && syncMedicine.body()?.data!![i].deletedAt == null) {
					if(getAllMedicineTime.isSuccessful) {
						Log.d(TAG, "getAllMedicineTime: ${getAllMedicineTime.body()}")

						dataManager.insertDrug(drug)
						val drugId = dataManager.getData(DRUG, "startDate", drug.startDate).id

						for(j in 0 until getAllMedicineTime.body()!!.size) {
							val drugTime = DrugTime(uid = getAllMedicineTime.body()!![j].uid, drugId = drugId, time = getAllMedicineTime.body()!![j].time)
							dataManager.insertDrugTime(drugTime)
							timeList.add(DrugTime(time = drugTime.time))
						}

						alarmReceiver.setAlarm(context, drugId, drug.startDate, drug.endDate, timeList, "${drug.name} ${drug.amount}${drug.unit}") // 알람 등록
					}else {
						Log.e(TAG, "getAllMedicineTime: $getAllMedicineTime")
					}
				}
			}
		}else {
			Log.e(TAG, "syncMedicine: $syncMedicine")
		}

		val syncGoal = RetrofitAPI.api.syncGoal("Bearer ${getToken.access}", SyncDTO(syncedAt))
		if(syncGoal.isSuccessful) {
			Log.d(TAG, "syncGoal: ${syncGoal.body()}")

			if(syncGoal.body()?.data != null) {
				for(i in 0 until syncGoal.body()?.data!!.size) {
					val getData = dataManager.getData(GOAL, CREATED_AT, syncGoal.body()?.data!![i].date.substring(0, 10))

					if(getData.id > 0) {
						if(syncGoal.body()?.data!![i].deletedAt != null) {
							dataManager.deleteItem(GOAL, "id", getData.id)
						}else {
							dataManager.updateGoal(Goal(id=getData.id, uid = syncGoal.body()?.data!![i].uid, food = syncGoal.body()?.data!![i].kcalOfDiet,
								waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
								exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight,
								sleep = syncGoal.body()?.data!![i].sleep, drug = syncGoal.body()?.data!![i].medicineIntake))
						}
					}else if(syncGoal.body()?.data!![i].createdAt != null && syncGoal.body()?.data!![i].deletedAt == null) {
						dataManager.insertGoal(Goal(uid = syncGoal.body()?.data!![i].uid, food = syncGoal.body()?.data!![i].kcalOfDiet,
							waterVolume = syncGoal.body()?.data!![i].waterAmountOfCup, water = syncGoal.body()?.data!![i].waterIntake,
							exercise = syncGoal.body()?.data!![i].kcalOfWorkout, body = syncGoal.body()?.data!![i].weight, sleep = syncGoal.body()?.data!![i].sleep,
							drug = syncGoal.body()?.data!![i].medicineIntake, createdAt = syncGoal.body()?.data!![i].date.substring(0, 10)))
					}
				}
			}
		}else {
			Log.e(TAG, "syncGoal: $syncGoal")
		}

		dataManager.updateUserStr(SYNC_TIME, "syncedAt", LocalDateTime.now().minusSeconds(20).toString(), "id")

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
				Log.e(TAG, "refreshToken: $response")
			}
		}

		if(refreshDiff.toHours() >= 336) {
			when (getUser.type) {
				Constant.GOOGLE.name -> {
					val response = RetrofitAPI.api.loginWithGoogle(LoginDTO(getUser.idToken))
					if (response.isSuccessful) {
						Log.d(TAG, "googleLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
								accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					} else {
						Log.e(TAG, "googleLogin: $response")
					}
				}
				Constant.NAVER.name -> {
					val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(getUser.accessToken))
					if (response.isSuccessful) {
						Log.d(TAG, "naverLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					} else {
						Log.e(TAG, "naverLogin: $response")
					}
				}
				Constant.KAKAO.name -> {
					val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(getUser.accessToken, getUser.idToken))
					if (response.isSuccessful) {
						Log.d(TAG, "kakaoLogin: ${response.body()}")
						dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
							accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
						getToken = dataManager.getToken()
					} else {
						Log.e(TAG, "kakaoLogin: $response")
					}
				}
			}
		}
	}
}
