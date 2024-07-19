package kr.bodywell.test.util

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.bodywell.test.api.RetrofitAPI
import kr.bodywell.test.api.dto.ActivityDTO
import kr.bodywell.test.api.dto.BodyDTO
import kr.bodywell.test.api.dto.FoodDTO
import kr.bodywell.test.api.dto.LoginDTO
import kr.bodywell.test.api.dto.MedicineDTO
import kr.bodywell.test.api.dto.MedicineIntakeDTO
import kr.bodywell.test.api.dto.MedicineTimeDTO
import kr.bodywell.test.api.dto.MedicineUpdateDTO
import kr.bodywell.test.api.dto.SleepDTO
import kr.bodywell.test.api.dto.SleepUpdateDTO
import kr.bodywell.test.api.dto.SyncDTO
import kr.bodywell.test.api.dto.SyncData
import kr.bodywell.test.api.dto.SyncUpdateDTO
import kr.bodywell.test.api.dto.SyncUpdateData
import kr.bodywell.test.api.dto.SyncedAtDTO
import kr.bodywell.test.api.dto.WaterDTO
import kr.bodywell.test.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.test.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.test.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.test.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.test.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.test.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.test.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.test.database.DBHelper.Companion.TABLE_UNUSED
import kr.bodywell.test.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.model.Token
import kr.bodywell.test.model.User
import kr.bodywell.test.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.test.util.CustomUtil.Companion.TAG
import kr.bodywell.test.util.CustomUtil.Companion.isoFormat1
import kr.bodywell.test.util.CustomUtil.Companion.networkStatusCheck
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   @SuppressLint("StaticFieldLeak")
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private var getUser = User()
   private var getToken = Token()

   private var refreshST = false
   private var loginST = false
   private var syncedST = false
   private var deleteFoodST = true
   private var deleteDailyFoodST = true
   private var deleteExerciseST = true
   private var deleteDailyExerciseST = true
   private var deleteDrugCheckST = true
   private var deleteDrugST = true
   private var createFoodST = true
   private var createDailyFoodST = true
   private var createWaterST = true
   private var createExerciseST = true
   private var createDailyExerciseST = true
   private var createBodyST = true
   private var createSleepST = true
   private var createDrugST = true
   private var createDrugCheckST = true
   private var createGoalST = true
   private var updateProfileST = true
   private var updateFoodST = true
   private var updateDailyFoodST = true
   private var updateWaterST = true
   private var updateExerciseST = true
   private var updateDailyExerciseST = true
   private var updateBodyST = true
   private var updateSleepST = true
   private var updateDrugST = true
   private var updateGoalST = true

   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      Log.d(TAG, "getUser: $getUser")
      Log.d(TAG, "access: ${getToken.access}")

      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(isActive) {
         if(networkStatusCheck(context)) {
            if(!syncedST) {
               refreshToken()
               syncedST = getSyncedData()
            }else {
               refreshToken()
               createApiRequest()
            }
         }

         delay(10000)
      }
   }

   private suspend fun createApiRequest() {
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
               if(deleteFoodST) {
                  val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteFood: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.e(TAG, "deleteFood: $response")
                     deleteFoodST = false
                  }
               }
            }
            "dailyFood" -> {
               if(deleteDailyFoodST) {
                  val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteDiets: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.e(TAG, "deleteDiets: $response")
                     deleteDailyFoodST = false
                  }
               }
            }
            "exercise" -> {
               if(deleteExerciseST) {
                  val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteActivity: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.e(TAG, "deleteActivity: $response")
                     deleteExerciseST = false
                  }
               }
            }
            "dailyExercise" -> {
               if(deleteDailyExerciseST) {
                  val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteWorkout: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.e(TAG, "deleteWorkout: $response")
                     deleteDailyExerciseST = false
                  }
               }
            }
            "drugCheck" -> {
               if(deleteDrugCheckST) {
                  val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].drugTimeUid, getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteMedicineIntake: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.e(TAG, "deleteMedicineIntake: $response")
                     deleteDrugCheckST = false
                  }
               }
            }
            "drug" -> {
               if(deleteDrugST) {
                  val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)
                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteMedicine: $response")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)

                     val getTime = dataManager.getUnused("drugTime", getUnused[i].created)
                     for(j in 0 until getTime.size) {
                        val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getTime[j].drugUid, getTime[j].value)
                        if(response2.isSuccessful) {
                           Log.d(TAG, "deleteMedicineTime: $response2")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getTime[j].id)
                        }else Log.e(TAG, "deleteMedicineTime: $response2")
                     }
                  }else {
                     Log.e(TAG, "deleteMedicine: $response")
                     deleteDrugST = false
                  }
               }
            }
         }
      }

      /*if(updateProfileST && getUserUpdated.profileUid != "") {
			val data = ProfileDTO(getUserUpdated.name!!, getUserUpdated.birthday!!, getUserUpdated.gender!!,
				getUserUpdated.height!!, getUserUpdated.weight!!, "Asia/Seoul")
			val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", data)

			if(response.isSuccessful) {
				Log.d(TAG, "updateProfile: ${response.body()}")
				dataManager.updateUserInt("isUpdated", 0)
			}else {
			   Log.d(TAG, "updateProfile: $response")
			   updateProfileST = false
			 }
		}*/

      if(createFoodST) {
         for(i in 0 until getFoodUid.size) {
            val data = FoodDTO("null", getFoodUid[i].name, getFoodUid[i].kcal, getFoodUid[i].carbohydrate, getFoodUid[i].protein,
               getFoodUid[i].fat, getFoodUid[i].count, "null", getFoodUid[i].amount, getFoodUid[i].unit)
            val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "createFood: ${response.body()}")
               dataManager.updateStr(TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid[i].id)
            }else {
               Log.d(TAG, "createFood: $response")
               createFoodST = false
               return
            }
         }
      }

      if(updateFoodST) {
         for(i in 0 until getFoodUpdated.size) {
            val data = FoodDTO("", getFoodUpdated[i].name, getFoodUpdated[i].kcal, getFoodUpdated[i].carbohydrate, getFoodUpdated[i].protein,
               getFoodUpdated[i].fat, 0, "", getFoodUpdated[i].amount, getFoodUpdated[i].unit)
            val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated[i].uid, data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateFood: ${response.body()}")
               dataManager.updateInt(TABLE_FOOD, "isUpdated", 0, "id", getFoodUpdated[i].id)
            }else {
               Log.d(TAG, "updateFood: $response")
               updateFoodST = false
               return
            }
         }
      }

      /*if(createDailyFoodST) {
         for(i in 0 until getDailyFoodUid.size) {
            val photos = ArrayList<String>()
            val getImage = dataManager.getImage(getDailyFoodUid[i].type, getDailyFoodUid[i].created)

//                  for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
            for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

            val getFood = dataManager.getFood("name", getDailyFoodUid[i].name)

            if(getFood.uid != "") {
               val isoFormat = isoFormat1(getDailyFoodUid[i].created)
               val data = DietDTO("BodyWell${getDailyFoodUid[i].id}", getDailyFoodUid[i].type, "null", getDailyFoodUid[i].name,
                  getDailyFoodUid[i].kcal, getDailyFoodUid[i].carbohydrate, getDailyFoodUid[i].protein, getDailyFoodUid[i].fat, getDailyFoodUid[i].count,
                  "null", getDailyFoodUid[i].amount, getDailyFoodUid[i].unit, photos, isoFormat, Food(getFood.uid))
               val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createDiets: ${response.body()}")
                  dataManager.updateStr(TABLE_DAILY_FOOD, "uid", response.body()!!.uid, "id", getDailyFoodUid[i].id)
               }else {
                  Log.d(TAG, "createDiets: $response")
                  createDailyFoodST = false
                  return
               }
            }
         }
      }*/

      /*if(updateDailyFoodST) {
         for(i in 0 until getDailyFoodUpdated.size) {
            val photos = ArrayList<String>()
            val getImage = dataManager.getImage(getDailyFoodUpdated[i].type, getDailyFoodUpdated[i].created)

//                  for(j in 0 until getImage.size) photos.add(getImage[j].imageUri)
            for(j in 0 until getImage.size) photos.add("https://example.com/picture.jpg")

            val isoFormat = isoFormat1(getDailyFoodUpdated[i].created)
            val data = DietUpdateDTO(getDailyFoodUpdated[i].type, "null", getDailyFoodUpdated[i].name, getDailyFoodUpdated[i].kcal,
               getDailyFoodUpdated[i].carbohydrate, getDailyFoodUpdated[i].protein, getDailyFoodUpdated[i].fat, getDailyFoodUpdated[i].count,
               "null", getDailyFoodUpdated[i].amount, getDailyFoodUpdated[i].unit, photos, isoFormat)
            val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated[i].uid, data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateDiets: ${response.body()}")
               dataManager.updateInt(TABLE_DAILY_FOOD, "isUpdated", 0, "id", getDailyFoodUpdated[i].id)
            }else {
               Log.d(TAG, "updateDiets: $response")
               updateDailyFoodST = false
               return
            }
         }
      }*/

      if(createWaterST) {
         for(i in 0 until getWaterUid.size) {
            val data = WaterDTO(getWaterUid[i].volume, getWaterUid[i].count, getWaterUid[i].created)
            val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "createWater: ${response.body()}")
               dataManager.updateStr(TABLE_WATER, "uid", response.body()!!.uid, "id", getWaterUid[i].id)
            }else {
               Log.d(TAG, "createWater: $response")
               createWaterST = false
               return
            }
         }
      }

      /*if(updateWaterST) {
         for(i in 0 until getWaterUpdated.size) {
				if(getWaterUpdated[i].uid != "") {
					val data = WaterDTO(getWaterUpdated[i].volume, getWaterUpdated[i].count, getWaterUpdated[i].created)
					val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, data)

					if(response.isSuccessful) {
						Log.d(TAG, "updateWater: ${response.body()}")
						dataManager.updateInt(TABLE_WATER, "isUpdated", 0, "id", getWaterUpdated[i].id)
					}else {
                  Log.d(TAG, "updateWater: $response")
                  updateWaterST = false
                  return
               }
				}
			}
      }*/

      if(createExerciseST) {
         for(i in 0 until getExUid.size) {
            val data1 = ActivityDTO(getExUid[i].name)
            val response1 = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", data1)

            if(response1.isSuccessful) {
               Log.d(TAG, "createActivity: ${response1.body()}")
               dataManager.updateStr(TABLE_EXERCISE, "uid", response1.body()!!.uid, "id", getExUid[i].id)

               val data2 = SyncDTO(SyncData(getExUid[i].name, "User", getExUid[i].created, getExUid[i].created), getExUid[i].created)
               val response2 = RetrofitAPI.api.syncCreateActivity("Bearer ${getToken.access}", data2)
               if(response2.isSuccessful) Log.d(TAG, "SyncActivity: ${response2.body()}") else Log.e(TAG, "SyncActivity: $response2")
            }else {
               Log.e(TAG, "createActivity: $response1")
               createExerciseST = false
               return
            }
         }
      }

      if(updateExerciseST) {
         for(i in 0 until getExUpdated.size) {
            val data1 = ActivityDTO(getExUpdated[i].name)
            val response1 = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExUpdated[i].uid, data1)

            if(response1.isSuccessful) {
               Log.d(TAG, "updateActivity: ${response1.body()}")
               dataManager.updateInt(TABLE_EXERCISE, "isUpdated", 0, "id", getExUpdated[i].id)

               val data2 = SyncUpdateDTO(SyncUpdateData(getExUpdated[i].uid, getExUpdated[i].name, "User", getExUpdated[i].created, getExUpdated[i].updated), getExUpdated[i].created)
               val response2 = RetrofitAPI.api.syncUpdateActivity("Bearer ${getToken.access}", data2)
               if(response2.isSuccessful) Log.d(TAG, "SyncActivity: ${response2.body()}") else Log.e(TAG, "SyncActivity: $response2")
            }else {
               Log.d(TAG, "updateActivity: $response1")
               updateExerciseST = false
               return
            }
         }
      }

      /*if(createDailyExerciseST) {
         for(i in 0 until getDailyExerciseUid.size) {
            val getExercise = dataManager.getExercise("name", getDailyExerciseUid[i].name)

            if(getExercise.uid != "") {
               val isoFormat = isoFormat1(getDailyExerciseUid[i].created)
               val data = WorkoutDTO(getDailyExerciseUid[i].name, getDailyExerciseUid[i].kcal, getDailyExerciseUid[i].intensity,
                  getDailyExerciseUid[i].workoutTime, isoFormat, true, Activity(getExercise.uid))
               val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createWorkout: ${response.body()}")
                  dataManager.updateStr(TABLE_DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExerciseUid[i].id)
               }else {
                  Log.d(TAG, "createWorkout: $response")
                  createDailyExerciseST = false
                  return
               }
            }
         }
      }*/

      /*if(updateDailyExerciseST) {
         for(i in 0 until getDailyExerciseUpdated.size) {
            val data = WorkoutUpdateDTO(getDailyExerciseUpdated[i].kcal, getDailyExerciseUpdated[i].intensity, getDailyExerciseUpdated[i].workoutTime)
            val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExerciseUpdated[i].uid, data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateWorkout: ${response.body()}")
               dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 0, "id", getDailyExerciseUpdated[i].id)
            }else {
               Log.d(TAG, "updateWorkout: $response")
               updateDailyExerciseST = false
               return
            }
         }
      }*/

      if(createBodyST) {
         for(i in 0 until getBodyUid.size) {
            val isoFormat = isoFormat1(getBodyUid[i].created)
            val data = BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat, getBodyUid[i].muscle,
               getBodyUid[i].bmr, getBodyUid[i].intensity, isoFormat)
            val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "createBody: ${response.body()}")
               dataManager.updateStr(TABLE_BODY, "uid", response.body()!!.uid, "id", getBodyUid[i].id)
            }else {
               Log.d(TAG, "createBody: $response")
               createBodyST = false
               return
            }
         }
      }

      if(updateBodyST) {
         for(i in 0 until getBodyUpdated.size) {
            val isoFormat = isoFormat1(getBodyUpdated[i].created)
            val data = BodyDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight, getBodyUpdated[i].bmi, getBodyUpdated[i].fat,
               getBodyUpdated[i].muscle, getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, isoFormat)
            val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated[i].uid!!, data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateBody: ${response.body()}")
               dataManager.updateInt(TABLE_BODY, "isUpdated", 0, "id", getBodyUpdated[i].id)
            }else {
               Log.d(TAG, "updateBody: $response")
               updateBodyST = false
               return
            }
         }
      }

      if(createSleepST) {
         for(i in 0 until getSleepUid.size) {
            Log.d(TAG, "updateData: $getSleepUid")
            val data = SleepDTO("BodyWell${getSleepUid[i].id}", getSleepUid[i].startTime, getSleepUid[i].endTime)
            val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "createSleep: ${response.body()}")
               dataManager.updateStr(TABLE_SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
            }else {
               Log.d(TAG, "createSleep: $response")
               createSleepST = false
               return
            }
         }
      }

      if(updateSleepST) {
         for(i in 0 until getSleepUpdated.size) {
            val data = SleepUpdateDTO(getSleepUpdated[i].startTime, getSleepUpdated[i].endTime)
            Log.d(TAG, "data: $data")
            val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid, data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateSleep: ${response.body()}")
               dataManager.updateInt(TABLE_SLEEP, "isUpdated", 0, "id", getSleepUpdated[i].id)
            }else {
               Log.e(TAG, "updateSleep: $response")
               updateSleepST = false
               return
            }
         }
      }

      if(createDrugST) {
         for(i in 0 until getDrugUid.size) {
            val startDate = isoFormat1(getDrugUid[i].startDate)
            val endDate = isoFormat1(getDrugUid[i].endDate)
            val data = MedicineDTO(getDrugUid[i].type, getDrugUid[i].name, getDrugUid[i].amount, getDrugUid[i].unit, startDate, endDate)
            val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "createMedicine: ${response.body()}")
               dataManager.updateStr(TABLE_DRUG, "uid", response.body()!!.uid, "id", getDrugUid[i].id)

               val getDrugTime = dataManager.getDrugTime(getDrugUid[i].id)

               for(j in 0 until getDrugTime.size) {
                  val response2 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response.body()!!.uid, MedicineTimeDTO(getDrugTime[j].time))

                  if(response2.isSuccessful) {
                     Log.d(TAG, "createMedicineTime: ${response2.body()}")
                     dataManager.updateStr(TABLE_DRUG_TIME, "uid", response2.body()!!.uid, "id", getDrugTime[j].id)
                  }else Log.d(TAG, "createMedicineTime: $response2")
               }
            }else {
               Log.d(TAG, "createMedicine: $response")
               createDrugST = false
               return
            }
         }
      }

      if(updateDrugST) {
         for(i in 0 until getDrugUpdated.size) {
            val startDate = isoFormat1(getDrugUpdated[i].startDate)
            val endDate = isoFormat1(getDrugUpdated[i].endDate)
            val data = MedicineUpdateDTO(getDrugUpdated[i].type, getDrugUpdated[i].name, getDrugUpdated[i].amount, getDrugUpdated[i].unit, startDate, endDate)
            val response1 = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getDrugUpdated[i].uid, data)

            if(response1.isSuccessful) {
               Log.d(TAG, "updateMedicine: ${response1.body()}")
               dataManager.updateStr(TABLE_DRUG, "uid", response1.body()!!.uid, "id", getDrugUpdated[i].id)
               dataManager.updateInt(TABLE_DRUG, "isUpdated", 0, "id", getDrugUpdated[i].id)

               val getUnusedTime = dataManager.getUnused("drugTime", getDrugUpdated[i].startDate)
               for(j in 0 until getUnusedTime.size) {
                  val response2 = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[j].value)

                  if(response2.isSuccessful) {
                     Log.d(TAG, "deleteMedicineTime: ${response2.body()}")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnusedTime[i].id)
                  }else Log.e(TAG, "deleteMedicineTime: $response2")
               }

               val getDrugTime = dataManager.getDrugTimeData(getDrugUpdated[i].id)
               for(j in 0 until getDrugTime.size) {
                  val timeDTO = MedicineTimeDTO(getDrugTime[j].time)
                  val response3 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", response1.body()!!.uid, timeDTO)

                  if(response3.isSuccessful) {
                     Log.d(TAG, "createMedicineTime: ${response3.body()}")
                     dataManager.updateStr(TABLE_DRUG_TIME, "uid", response3.body()!!.uid, "id", getDrugTime[j].id)
                  }else Log.e(TAG, "createMedicineTime: $response3")
               }
            }else {
               Log.e(TAG, "updateMedicine: $response1")
               updateDrugST = false
               return
            }
         }
      }

      if(createDrugCheckST) {
         for(i in 0 until getDrugCheckUid.size) {
            val drugUid = dataManager.getUid(TABLE_DRUG, getDrugCheckUid[i].drugId)
            val drugTimeUid = dataManager.getUid(TABLE_DRUG_TIME, getDrugCheckUid[i].drugTimeId)

            if(drugUid != "" && drugTimeUid != "") {
               val isoFormat = isoFormat1(getDrugCheckUid[i].created)
               val data = MedicineIntakeDTO(isoFormat)
               val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", drugUid, drugTimeUid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createMedicineIntake: ${response.body()}")
                  dataManager.updateStr(TABLE_DRUG_CHECK, "uid", response.body()!!.uid, "id", getDrugCheckUid[i].id)
               }else {
                  Log.e(TAG, "createMedicineIntake: $response")
                  createDrugCheckST = false
                  return
               }
            }
         }
      }

      /*if(createGoalST) {
         for(i in 0 until getGoalUid.size) {
            val getGoal = dataManager.getGoal(getGoalUid[i].id)

            if(getGoal.uid == "") {
               val isoFormat = isoFormat1(getGoalUid[i].created)
               val data = GoalDTO(getGoalUid[i].body, getGoalUid[i].food, getGoalUid[i].exercise, getGoalUid[i].waterVolume, getGoalUid[i].water,
                  getGoalUid[i].sleep, getGoalUid[i].drug, isoFormat)
               val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createGoal: ${response.body()}")
                  dataManager.updateStr(TABLE_GOAL, "uid", response.body()!!.uid, "id", getGoalUid[i].id)
               }else {
                  Log.d(TAG, "createGoal: $response")
                  createGoalST = false
                  return
               }
            }
         }
      }

      if(updateGoalST) {
         for(i in 0 until getGoalUpdated.size) {
            val data = GoalDTO(getGoalUpdated[i].body, getGoalUpdated[i].food, getGoalUpdated[i].exercise, getGoalUpdated[i].waterVolume,
               getGoalUpdated[i].water, getGoalUpdated[i].sleep, getGoalUpdated[i].drug)
            Log.d(TAG, "data: $data")
            val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", data)

            if(response.isSuccessful) {
               Log.d(TAG, "updateGoal: ${response.body()}")
               dataManager.updateInt(TABLE_GOAL, "isUpdated", 0, "id", getGoalUpdated[i].id)
            }else {
               Log.d(TAG, "updateGoal: $response")
               updateGoalST = false
               return
            }
         }
      }*/
   }

   private suspend fun getSyncedData():Boolean {
      val response = RetrofitAPI.api.syncProfile("Bearer ${getToken.access}", SyncedAtDTO(getUser.updated))
      if(response.isSuccessful) {
         Log.d(TAG, "syncProfile: ${response.body()}")

         if(response.body()!!.data != null) {
            val gender = if(response.body()!!.data!!.gender == null) getUser.gender else response.body()!!.data!!.gender
            val birthday = if(response.body()!!.data!!.birth == null) getUser.birthday else response.body()!!.data!!.birth
            val height = if(response.body()!!.data!!.height == null) getUser.height else response.body()!!.data!!.height
            val weight = if(response.body()!!.data!!.weight == null) getUser.weight else response.body()!!.data!!.weight

            dataManager.updateProfile(User(name=response.body()!!.data!!.name, gender=gender!!, birthday=birthday!!, height=height!!.toDouble(),
               weight=weight!!.toDouble(), updated=response.body()!!.data!!.updatedAt, isUpdated=0))
         }
      }else {
         Log.e(TAG, "syncProfile: $response")
      }
      return true
   }

   private suspend fun refreshToken() {
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
                  dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                     accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
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

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setInt(data: Int) {
      intVM.value = data
   }
}
