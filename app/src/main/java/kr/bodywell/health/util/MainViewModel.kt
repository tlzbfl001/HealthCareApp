package kr.bodywell.health.util

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.health.api.RetrofitAPI
import kr.bodywell.health.api.dto.Activity
import kr.bodywell.health.api.dto.ActivityDTO
import kr.bodywell.health.api.dto.DietDTO
import kr.bodywell.health.api.dto.DietUpdateDTO
import kr.bodywell.health.api.dto.Food
import kr.bodywell.health.api.dto.FoodDTO
import kr.bodywell.health.api.dto.LoginDTO
import kr.bodywell.health.api.dto.SyncedAtDTO
import kr.bodywell.health.api.dto.WorkoutDTO
import kr.bodywell.health.api.dto.WorkoutUpdateDTO
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.health.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.health.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.health.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.health.database.DBHelper.Companion.TABLE_UNUSED
import kr.bodywell.health.database.DataManager
import kr.bodywell.health.model.Token
import kr.bodywell.health.model.User
import kr.bodywell.health.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.health.util.CustomUtil.Companion.isoFormat1
import kr.bodywell.health.util.CustomUtil.Companion.TAG
import kr.bodywell.health.util.CustomUtil.Companion.networkStatusCheck
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   @SuppressLint("StaticFieldLeak")
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private var getUser = User()
   private var getToken = Token()
   private var refreshCheck = false
   private var loginCheck = false
   private var syncedCheck = false
   private var isRunning = true

   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
      /*dataManager.open()

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      Log.d(TAG, "getUser: $getUser")
      Log.d(TAG, "access: ${getToken.access}")

      updateData()*/
   }

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setInt(data: Int) {
      intVM.value = data
   }

   private fun updateData() = viewModelScope.launch {
      while(isRunning) {
         if(networkStatusCheck(context)) {
            if(!syncedCheck) {
               syncedCheck = getSynced()
            }else {
               val getUnused = dataManager.getUnused()
               val getUserUpdated = dataManager.getUserUpdated()
               val getFoodUid = dataManager.getFoodUid()
               val getFoodUpdated = dataManager.getFoodUpdated()
               val getDailyFoodUid = dataManager.getDailyFoodUid()
               val getDailyFoodUpdated = dataManager.getDailyFoodUpdated()
               val getWaterUid = dataManager.getWaterUid()
               val getWaterUpdated = dataManager.getWaterUpdated()
               val getExerciseUid = dataManager.getExerciseUid()
               val getExerciseUpdated = dataManager.getExerciseUpdated()
               val getDailyExerciseUid = dataManager.getDailyExerciseUid()
               val getDailyExerciseUpdated = dataManager.getDailyExerciseUpdated()
               val getBodyUid = dataManager.getBodyUid()
               val getBodyUpdated = dataManager.getBodyUpdated()
               val getSleepUid = dataManager.getSleepUid()
               val getSleepUpdated = dataManager.getSleepUpdated()
               val getDrugUid = dataManager.getDrugUid()
               val getDrugCheckUid = dataManager.getDrugCheckUid()
               val getDrugUpdated = dataManager.getDrugUpdated()
               val getGoalUid = dataManager.getGoalUid()
               val getGoalUpdated = dataManager.getGoalUpdated()

               val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
               val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

               if ((accessDiff.toHours() in 1..335) && !refreshCheck) refreshCheck = refreshToken()

               if (refreshDiff.toHours() >= 336 && !loginCheck) {
                  when(getUser.type) {
                     "google" -> loginCheck = loginWithGoogle()
                  }
               }

               for(i in 0 until getUnused.size) {
                  when(getUnused[i].type) {
                     "food" -> {
                        val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)
                        if(response.isSuccessful) {
                           Log.d(TAG, "deleteFood: $response")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteFood: $response")
                     }
                     "dailyFood" -> {
                        val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)
                        if(response.isSuccessful) {
                           Log.d(TAG, "deleteDiets: $response")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteDiets: $response")
                     }
                     "exercise" -> {
                        val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)
                        if(response.isSuccessful) {
                           Log.d(TAG, "deleteActivity: $response")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteActivity: $response")
                     }
                     "dailyExercise" -> {
                        val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)
                        if(response.isSuccessful) {
                           Log.d(TAG, "deleteWorkout: $response")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteWorkout: $response")
                     }
                     "drugCheck" -> {
                        val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused[i].drugUid, getUnused[i].drugTimeUid, getUnused[i].value)
                        if(response.isSuccessful) {
                           Log.d(TAG, "deleteMedicineIntake: $response")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteMedicineIntake: $response")
                     }
                     "drug" -> {
                        val deleteMedicine = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused[i].value)
                        if(deleteMedicine.isSuccessful) {
                           Log.d(TAG, "deleteMedicine: $deleteMedicine")
                           dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                        }else Log.e(TAG, "deleteMedicine: $deleteMedicine")

                        val times = dataManager.getUnused("drugTime", getUnused[i].created)

                        for(j in 0 until times.size) {
                           val deleteMedicineTime = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}",
                              times[j].drugUid, times[j].value)
                           if(deleteMedicineTime.isSuccessful) {
                              Log.d(TAG, "deleteMedicineTime: $deleteMedicineTime")
                              dataManager.deleteItem(TABLE_UNUSED, "id", times[j].id)
                           }else Log.e(TAG, "deleteMedicineTime: $deleteMedicineTime")
                        }
                     }
                  }
               }

               /*if(getUserUpdated.profileUid != "") {
                  val data = ProfileDTO(getUserUpdated.name!!, getUserUpdated.birthday!!, getUserUpdated.gender!!,
                     getUserUpdated.height!!, getUserUpdated.weight!!, "Asia/Seoul")

                  val response = RetrofitAPI.api.updateProfile("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateProfile: ${response.body()}")
                     dataManager.updateUserInt("isUpdated", 0)
                  } else Log.d(TAG, "updateProfile: $response")
               }*/

               for(i in 0 until getFoodUid.size) {
                  val data = FoodDTO("null", getFoodUid[i].name, getFoodUid[i].kcal, getFoodUid[i].carbohydrate, getFoodUid[i].protein,
                     getFoodUid[i].fat, getFoodUid[i].count, "null", getFoodUid[i].amount, getFoodUid[i].unit)

                  val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createFood: ${response.body()}")
                     dataManager.updateStr(TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid[i].id)
                  }else Log.d(TAG, "createFood: $response")
               }

               for(i in 0 until getFoodUpdated.size) {
                  val data = FoodDTO("", getFoodUpdated[i].name, getFoodUpdated[i].kcal, getFoodUpdated[i].carbohydrate, getFoodUpdated[i].protein,
                     getFoodUpdated[i].fat, 0, "", getFoodUpdated[i].amount, getFoodUpdated[i].unit)

                  val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateFood: ${response.body()}")
                     dataManager.updateInt(TABLE_FOOD, "isUpdated", 0, "id", getFoodUpdated[i].id)
                  }else Log.d(TAG, "updateFood: $response")
               }

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
                     }else Log.d(TAG, "createDiets: $response")
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

                  val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateDiets: ${response.body()}")
                     dataManager.updateInt(TABLE_DAILY_FOOD, "isUpdated", 0, "id", getDailyFoodUpdated[i].id)
                  }else Log.d(TAG, "updateDiets: $response")
               }

               /*for(i in 0 until getWaterUid.size) {
                  val data = WaterDTO(getWaterUid[i].volume, getWaterUid[i].count, getWaterUid[i].created)

                  val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createWater: ${response.body()}")
                     dataManager.updateStr(TABLE_WATER, "uid", response.body()!!.uid, "id", getWaterUid[i].id)
                  }else Log.d(TAG, "createWater: $response")
               }

               for(i in 0 until getWaterUpdated.size) {
                  if(getWaterUpdated[i].uid != "") {
                     val data = WaterDTO(getWaterUpdated[i].volume, getWaterUpdated[i].count, getWaterUpdated[i].created)
                     val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, data)

                     if(response.isSuccessful) {
                        Log.d(TAG, "updateWater: ${response.body()}")
                        dataManager.updateInt(TABLE_WATER, "isUpdated", 0, "id", getWaterUpdated[i].id)
                     }else Log.d(TAG, "updateWater: $response")
                  }
               }*/

               for(i in 0 until getExerciseUid.size) {
                  val data = ActivityDTO(getExerciseUid[i].name)

                  val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createActivity: ${response.body()}")
                     dataManager.updateStr(TABLE_EXERCISE, "uid", response.body()!!.uid, "id", getExerciseUid[i].id)
                  }else Log.d(TAG, "createActivity: $response")
               }

               for(i in 0 until getExerciseUpdated.size) {
                  val data = ActivityDTO(getExerciseUpdated[i].name)

                  val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExerciseUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateActivity: ${response.body()}")
                     dataManager.updateInt(TABLE_EXERCISE, "isUpdated", 0, "id", getExerciseUpdated[i].id)
                  }else Log.d(TAG, "updateActivity: $response")
               }

               for(i in 0 until getDailyExerciseUid.size) {
                  val getExercise = dataManager.getExercise("name", getDailyExerciseUid[i].name)

                  if(getExercise.uid != "") {
                     val isoFormat = isoFormat1(getDailyExerciseUid[i].created)
                     val data = WorkoutDTO("BodyWell${getDailyExerciseUid[i].id}", getDailyExerciseUid[i].name, getDailyExerciseUid[i].kcal,
                        getDailyExerciseUid[i].intensity, getDailyExerciseUid[i].workoutTime, isoFormat, true, Activity(getExercise.uid))

                     val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)

                     if(response.isSuccessful) {
                        Log.d(TAG, "createWorkout: ${response.body()}")
                        dataManager.updateStr(TABLE_DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExerciseUid[i].id)
                     }else Log.d(TAG, "createWorkout: $response")
                  }
               }

               for(i in 0 until getDailyExerciseUpdated.size) {
                  val data = WorkoutUpdateDTO(getDailyExerciseUpdated[i].kcal, getDailyExerciseUpdated[i].intensity, getDailyExerciseUpdated[i].workoutTime)

                  val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExerciseUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateWorkout: ${response.body()}")
                     dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 0, "id", getDailyExerciseUpdated[i].id)
                  }else Log.d(TAG, "updateWorkout: $response")
               }

               /*for(i in 0 until getBodyUid.size) {
                  val isoFormat = isoFormat1(getBodyUid[i].created)
                  val data = BodyDTO("BodyWell${getBodyUid[i].id}", getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat,
                     getBodyUid[i].muscle, getBodyUid[i].bmr, getBodyUid[i].intensity, isoFormat)

                  val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createBody: ${response.body()}")
                     dataManager.updateStr(TABLE_BODY, "uid", response.body()!!.uid, "id", getBodyUid[i].id)
                  }else Log.d(TAG, "createBody: $response")
               }

               for(i in 0 until getBodyUpdated.size) {
                  val isoFormat = isoFormat1(getBodyUpdated[i].created)
                  val data = BodyUpdateDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight, getBodyUpdated[i].bmi, getBodyUpdated[i].fat,
                     getBodyUpdated[i].muscle, getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, isoFormat)

                  val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated[i].uid!!, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateBody: ${response.body()}")
                     dataManager.updateInt(TABLE_BODY, "isUpdated", 0, "id", getBodyUpdated[i].id)
                  }else Log.d(TAG, "updateBody: $response")
               }

               for(i in 0 until getSleepUid.size) {
                  val data = SleepDTO("BodyWell${getSleepUid[i].id}", getSleepUid[i].startTime, getSleepUid[i].endTime)

                  val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createSleep: ${response.body()}")
                     dataManager.updateStr(TABLE_SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
                  }else Log.d(TAG, "createSleep: $response")
               }

               for(i in 0 until getSleepUpdated.size) {
                  val data = SleepUpdateDTO(getSleepUpdated[i].startTime, getSleepUpdated[i].endTime)

                  val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateSleep: ${response.body()}")
                     dataManager.updateInt(TABLE_SLEEP, "isUpdated", 0, "id", getSleepUpdated[i].id)
                  }else Log.d(TAG, "updateSleep: $response")
               }

               for(i in 0 until getDrugUid.size) {
                  val startDate = isoFormat1(getDrugUid[i].startDate)
                  val endDate = isoFormat1(getDrugUid[i].endDate)
                  val data = MedicineDTO("BodyWell${getDrugUid[i].id}", getDrugUid[i].type, getDrugUid[i].name, getDrugUid[i].amount, getDrugUid[i].unit, startDate, endDate)
                  val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createMedicine: ${response.body()}")
                     dataManager.updateStr(TABLE_DRUG, "uid", response.body()!!.uid, "id", getDrugUid[i].id)
                  }else Log.d(TAG, "createMedicine: $response")

                  val getDrug = dataManager.getDrug(getDrugUid[i].id)

                  if(getDrug.uid != "") {
                     val getDrugTime = dataManager.getDrugTime(getDrugUid[i].id)

                     for(j in 0 until getDrugTime.size) {
                        val data2 = MedicineTimeDTO(getDrugTime[j].time)
                        val response2 = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", getDrug.uid, data2)

                        if(response2.isSuccessful) {
                           Log.d(TAG, "createMedicineTime: ${response2.body()}")
                           dataManager.updateStr(TABLE_DRUG_TIME, "uid", response2.body()!!.uid, "id", getDrugTime[j].id)
                        }else Log.d(TAG, "createMedicineTime: $response2")
                     }
                  }
               }

               for(i in 0 until getDrugUpdated.size) {
                  val startDate = isoFormat1(getDrugUpdated[i].startDate)
                  val endDate = isoFormat1(getDrugUpdated[i].endDate)
                  val data = MedicineUpdateDTO(getDrugUpdated[i].type, getDrugUpdated[i].name, getDrugUpdated[i].amount, getDrugUpdated[i].unit, startDate, endDate)

                  val update = RetrofitAPI.api.updateMedicine("Bearer ${getToken.access}", getDrugUpdated[i].uid, data)
                  val drugUid = update.body()!!.uid

                  if(update.isSuccessful) {
                     Log.d(TAG, "updateMedicine: ${update.body()}")
                     dataManager.updateStr(TABLE_DRUG, "uid", drugUid, "id", getDrugUpdated[i].id)
                     dataManager.updateInt(TABLE_DRUG, "isUpdated", 0, "id", getDrugUpdated[i].id)
                  }else Log.e(TAG, "updateMedicine: $update")

                  val getUnusedTime = dataManager.getUnused("drugTime", getDrugUpdated[i].startDate)

                  for(j in 0 until getUnusedTime.size) {
                     val delete = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getDrugUpdated[i].uid, getUnusedTime[j].value)
                     if(delete.isSuccessful) {
                        Log.d(TAG, "deleteMedicineTime: ${delete.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnusedTime[i].id)
                     }else Log.e(TAG, "deleteMedicineTime: $delete")
                  }

                  val getDrugTime = dataManager.getDrugTimeData(getDrugUpdated[i].id)

                  for(j in 0 until getDrugTime.size) {
                     val timeDTO = MedicineTimeDTO(getDrugTime[j].time)
                     val create = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", drugUid, timeDTO)
                     if(create.isSuccessful) {
                        Log.d(TAG, "createMedicineTime: ${create.body()}")
                        dataManager.updateStr(TABLE_DRUG_TIME, "uid", create.body()!!.uid, "id", getDrugTime[j].id)
                     }else Log.e(TAG, "createMedicineTime: $create")
                  }
               }

               for(i in 0 until getDrugCheckUid.size) {
                  val getDrugTimeUid = dataManager.getDrugTimeUid(TABLE_DRUG_TIME, getDrugCheckUid[i].drugTimeId)

                  if(getDrugTimeUid != "") {
                     val isoFormat = isoFormat1(getDrugCheckUid[i].created)
                     val data = MedicineIntakeDTO("BodyWell${getDrugCheckUid[i].id}", isoFormat)

                     val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", getDrugTimeUid, data)

                     if(response.isSuccessful) {
                        Log.d(TAG, "createMedicineIntake: ${response.body()}")
                        dataManager.updateStr(TABLE_DRUG_CHECK, "uid", response.body()!!.uid, "id", getDrugCheckUid[i].id)
                     }else Log.e(TAG, "createMedicineIntake: $response")
                  }
               }

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
                     }else Log.d(TAG, "createGoal: $response")
                  }
               }

               for(i in 0 until getGoalUpdated.size) {
                  val data = GoalDTO(getGoalUpdated[i].body, getGoalUpdated[i].food, getGoalUpdated[i].exercise, getGoalUpdated[i].waterVolume,
                     getGoalUpdated[i].water, getGoalUpdated[i].sleep, getGoalUpdated[i].drug)

                  val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", getGoalUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateGoal: ${response.body()}")
                     dataManager.updateInt(TABLE_GOAL, "isUpdated", 0, "id", getGoalUpdated[i].id)
                  }else Log.d(TAG, "updateGoal: $response")
               }*/
            }
         }

         delay(10000)
      }
   }

   private suspend fun getSynced(): Boolean {
      val response = RetrofitAPI.api.syncProfile("Bearer ${getToken.access}", SyncedAtDTO(getUser.updated))

      return if(response.isSuccessful) {
         Log.d(TAG, "syncProfile: ${response.body()}")
         if(response.body()!!.data != null) {
            val gender = if(response.body()!!.data!!.gender == null) getUser.gender else response.body()!!.data!!.gender
            val birthday = if(response.body()!!.data!!.birth == null) getUser.birthday else response.body()!!.data!!.birth
            val height = if(response.body()!!.data!!.height == null) getUser.height else response.body()!!.data!!.height
            val weight = if(response.body()!!.data!!.weight == null) getUser.weight else response.body()!!.data!!.weight

            dataManager.updateProfile(User(name=response.body()!!.data!!.name, gender=gender!!, birthday=birthday!!,
               height=height!!.toDouble(), weight=weight!!.toDouble(), updated=response.body()!!.data!!.updatedAt, isUpdated=0))
         }
         true
      }else {
         Log.e(TAG, "syncProfile: $response")
         isRunning = false
         Log.e(TAG, "isRunning: $isRunning")
         false
      }
   }

   private suspend fun refreshToken(): Boolean {
      val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")

      return if(response.isSuccessful) {
         Log.d(TAG, "refreshToken: ${response.body()}")
         dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
         getToken = dataManager.getToken()
         true
      }else {
         Log.d(TAG, "refreshToken: $response")
         false
      }
   }

   private suspend fun loginWithGoogle(): Boolean {
      val data = LoginDTO(getUser.idToken)
      val response = RetrofitAPI.api.loginWithGoogle(data)

      return if(response.isSuccessful) {
         Log.d(TAG, "googleLogin: ${response.body()}")
         dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
            accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
         getToken = dataManager.getToken()
         true
      }else {
         Log.e(TAG, "googleLogin: $response")
         false
      }
   }
}
