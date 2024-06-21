package kr.bodywell.android.util

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.Activity
import kr.bodywell.android.api.dto.ActivityDTO
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.BodyUpdateDTO
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.DietUpdateDTO
import kr.bodywell.android.api.dto.Food
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.MedicineDTO
import kr.bodywell.android.api.dto.MedicineIntakeDTO
import kr.bodywell.android.api.dto.MedicineTimeDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.SleepUpdateDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private var getUser = User()
   private var getToken = Token()
   private var refreshCheck = false
   private var loginCheck = false
   private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

   init {
      dataManager.open()

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      Log.d(TAG, "getUser: $getUser")
      Log.d(TAG, "getToken: $getToken")

      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(true) {
         if(networkStatusCheck(context)) {
            val getUnused = dataManager.getUnused()
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
            val getDrugTimeUid = dataManager.getDrugTimeUid()
            val getDrugCheckUid = dataManager.getDrugCheckUid()
            val getGoalUid = dataManager.getGoalUid()
            val getGoalUpdated = dataManager.getGoalUpdated()

            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshRegDate), LocalDateTime.now())

            if ((accessDiff.toHours() in 1..335) && !refreshCheck) refreshCheck = refreshToken()

            if (refreshDiff.toHours() >= 336 && !loginCheck) loginCheck = login()

            /*if(getUnused.type != "") {
               when(getUnused.type) {
                  "food" -> {
                     val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteFood: ${response.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteFood: $response")
                  }
                  "dailyFood" -> {
                     val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteDiets: ${response.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteDiets: $response")
                  }
                  "exercise" -> {
                     val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteActivity: ${response.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteActivity: $response")
                  }
                  "dailyExercise" -> {
                     val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteWorkout: $response")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteWorkout: $response")
                  }
                  "drugCheck" -> {
                     val response = RetrofitAPI.api.deleteMedicineIntake("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteMedicineIntake: $response")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteMedicineIntake: $response")
                  }
                  "drugTime" -> {
                     val response = RetrofitAPI.api.deleteMedicineTime("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteMedicineTime: ${response.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteMedicineTime: $response")
                  }
                  "drug" -> {
                     val response = RetrofitAPI.api.deleteMedicine("Bearer ${getToken.access}", getUnused.value)
                     if(response.isSuccessful) {
                        Log.d(TAG, "deleteMedicine: ${response.body()}")
                        dataManager.deleteItem(TABLE_UNUSED, "id", getUnused.id)
                     }else Log.d(TAG, "deleteMedicine: $response")
                  }
               }
            }*/

            if(getFoodUid.name != "") {
               val data = FoodDTO("null", getFoodUid.name, getFoodUid.kcal, getFoodUid.carbohydrate, getFoodUid.protein,
                  getFoodUid.fat, getFoodUid.count, "null", getFoodUid.amount, getFoodUid.unit)

               val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createFood: ${response.body()}")
                  dataManager.updateStr(TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid.id)
               }else Log.d(TAG, "createFood: $response")
            }

            if(getFoodUpdated.uid != "") {
               val data = FoodDTO("", getFoodUpdated.name, getFoodUpdated.kcal, getFoodUpdated.carbohydrate, getFoodUpdated.protein,
                  getFoodUpdated.fat, 0, "", getFoodUpdated.amount, getFoodUpdated.unit)

               val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateFood: ${response.body()}")
                  dataManager.updateInt(TABLE_FOOD, "isUpdated", 0, "id", getFoodUpdated.id)
               }else Log.d(TAG, "updateFood: $response")
            }

            if(getDailyFoodUid.regDate != "") {
               val photos = ArrayList<String>()
               val getImage = dataManager.getImage(getDailyFoodUid.type, getDailyFoodUid.regDate)

               for(i in 0 until getImage.size) photos.add(getImage[i].imageUri)

               val getFood = dataManager.getFood("name", getDailyFoodUid.name)

               if(getFood.uid != "") {
                  val timeFormat = LocalDate.parse(getDailyFoodUid.regDate).atStartOfDay().format(formatter)
                  val data = DietDTO("BodyWell${getDailyFoodUid.id}", getDailyFoodUid.type, "null", getDailyFoodUid.name,
                     getDailyFoodUid.kcal, getDailyFoodUid.carbohydrate, getDailyFoodUid.protein, getDailyFoodUid.fat, getDailyFoodUid.count,
                     "null", getDailyFoodUid.amount, getDailyFoodUid.unit, photos, timeFormat, Food(getFood.uid))

                  val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createDiets: ${response.body()}")
                     dataManager.updateStr(TABLE_DAILY_FOOD, "uid", response.body()!!.uid, "id", getDailyFoodUid.id)
                  }else Log.d(TAG, "createDiets: $response")
               }
            }

            if(getDailyFoodUpdated.uid != "") {
               val photos = ArrayList<String>()
               val getImage = dataManager.getImage(getDailyFoodUpdated.type, getDailyFoodUpdated.regDate)

               for(i in 0 until getImage.size) photos.add(getImage[i].imageUri)
               Log.d(TAG, "photos: $photos")

               val timeFormat = LocalDate.parse(getDailyFoodUpdated.regDate).atStartOfDay().format(formatter)
               val data = DietUpdateDTO(getDailyFoodUpdated.type, "", getDailyFoodUpdated.name, getDailyFoodUpdated.kcal,
                  getDailyFoodUpdated.carbohydrate, getDailyFoodUpdated.protein, getDailyFoodUpdated.fat, getDailyFoodUpdated.count,
                  "", getDailyFoodUpdated.amount, getDailyFoodUpdated.unit, photos, timeFormat)

               val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateDiets: ${response.body()}")
                  dataManager.updateInt(TABLE_DAILY_FOOD, "isUpdated", 0, "id", getDailyFoodUpdated.id)
               }else Log.d(TAG, "updateDiets: $response")
            }

            if(getWaterUid.regDate != "") {
               val data = WaterDTO("BodyWell${getWaterUid.id}", getWaterUid.volume, getWaterUid.count, getWaterUid.regDate)

               val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "createWater: ${response.body()}")
                  dataManager.updateStr(TABLE_WATER, "uid", response.body()!!.uid, "id", getWaterUid.id)
               }else {
                  Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "createWater: $response")
               }
            }

            /*if(getWaterUpdated.uid != "") {
               val data = WaterDTO(getWaterUpdated.volume, getWaterUpdated.count, getWaterUpdated.regDate)
               val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated.uid!!, data)

               if(response.isSuccessful) {
                  Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "updateWater: ${response.body()}")
                  dataManager.updateInt(TABLE_WATER, "isUpdated", 0, "id", getWaterUpdated.id)
               }else {
                  Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "updateWater: $response")
               }
            }*/

            if(getExerciseUid.name != "") {
               val data = ActivityDTO(getExerciseUid.name)

               val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createActivity: ${response.body()}")
                  dataManager.updateStr(TABLE_EXERCISE, "uid", response.body()!!.uid, "id", getExerciseUid.id)
               }else Log.d(TAG, "createActivity: $response")
            }

            if(getExerciseUpdated.uid != "") {
               val data = ActivityDTO(getExerciseUpdated.name)

               val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExerciseUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateActivity: ${response.body()}")
                  dataManager.updateInt(TABLE_EXERCISE, "isUpdated", 0, "id", getExerciseUpdated.id)
               }else Log.d(TAG, "updateActivity: $response")
            }

            if(getDailyExerciseUid.regDate != "") {
               val getExercise = dataManager.getExercise("name", getDailyExerciseUid.name)

               if(getExercise.uid != "") {
                  val timeFormat = LocalDate.parse(getDailyExerciseUid.regDate).atStartOfDay().format(formatter)
                  val data = WorkoutDTO("BodyWell${getDailyExerciseUid.id}", getDailyExerciseUid.name, getDailyExerciseUid.kcal, getDailyExerciseUid.intensity,
                     getDailyExerciseUid.workoutTime, timeFormat, true, Activity(getExercise.uid))

                  val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createWorkout: ${response.body()}")
                     dataManager.updateStr(TABLE_DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExerciseUid.id)
                  }else Log.d(TAG, "createWorkout: $response")
               }
            }

            if(getDailyExerciseUpdated.uid != "") {
               val data = WorkoutUpdateDTO(getDailyExerciseUpdated.kcal, getDailyExerciseUpdated.intensity, getDailyExerciseUpdated.workoutTime)

               val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExerciseUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateWorkout: ${response.body()}")
                  dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 0, "id", getDailyExerciseUpdated.id)
               }else Log.d(TAG, "updateWorkout: $response")
            }

            if(getBodyUid.regDate != "") {
               val timeFormat = LocalDate.parse(getBodyUid.regDate).atStartOfDay().format(formatter)
               val data = BodyDTO("BodyWell${getBodyUid.id}", getBodyUid.height, getBodyUid.weight, getBodyUid.bmi, getBodyUid.fat,
                  getBodyUid.muscle, getBodyUid.bmr, getBodyUid.intensity, timeFormat)

               val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createBody: ${response.body()}")
                  dataManager.updateStr(TABLE_BODY, "uid", response.body()!!.uid, "id", getBodyUid.id)
               }else Log.d(TAG, "createBody: $response")
            }

            if(getBodyUpdated.uid != "") {
               val timeFormat = LocalDate.parse(getBodyUpdated.regDate).atStartOfDay().format(formatter)
               val data = BodyUpdateDTO(getBodyUpdated.height, getBodyUpdated.weight, getBodyUpdated.bmi, getBodyUpdated.fat,
                  getBodyUpdated.muscle, getBodyUpdated.bmr, getBodyUpdated.intensity, timeFormat)

               val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated.uid!!, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateBody: ${response.body()}")
                  dataManager.updateInt(TABLE_BODY, "isUpdated", 0, "id", getBodyUpdated.id)
               }else Log.d(TAG, "updateBody: $response")
            }

            if(getSleepUid.startTime != "") {
               val data = SleepDTO("BodyWell${getSleepUid.id}", getSleepUid.startTime, getSleepUid.endTime)

               val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createSleep: ${response.body()}")
                  dataManager.updateStr(TABLE_SLEEP, "uid", response.body()!!.uid, "id", getSleepUid.id)
               }else Log.d(TAG, "createSleep: $response")
            }

            if(getSleepUpdated.uid != "") {
               val data = SleepUpdateDTO(getSleepUpdated.startTime, getSleepUpdated.endTime)

               val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateSleep: ${response.body()}")
                  dataManager.updateInt(TABLE_SLEEP, "isUpdated", 0, "id", getSleepUpdated.id)
               }else Log.d(TAG, "updateSleep: $response")
            }

            if(getDrugUid.regDate != "") {
               val startDate = LocalDate.parse(getDrugUid.startDate).atStartOfDay().format(formatter)
               val endDate = LocalDate.parse(getDrugUid.endDate).atStartOfDay().format(formatter)
               val data = MedicineDTO("BodyWell${getDrugUid.id}", getDrugUid.type, getDrugUid.name, getDrugUid.amount, getDrugUid.unit, startDate, endDate)

               val response = RetrofitAPI.api.createMedicine("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createMedicine: ${response.body()}")
                  dataManager.updateStr(TABLE_DRUG, "uid", response.body()!!.uid, "id", getDrugUid.id)
               }else Log.d(TAG, "createMedicine: $response")
            }

            for(i in 0 until getDrugTimeUid.size) {
               val getDrug = dataManager.getDrug(getDrugTimeUid[i].drugId)

               if(getDrug.uid != "") {
                  val data = MedicineTimeDTO(getDrugTimeUid[i].time)

                  val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", getDrug.uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createMedicineTime: ${response.body()}")
                     dataManager.updateStr(TABLE_DRUG_TIME, "uid", response.body()!!.uid, "id", getDrugTimeUid[i].id)
                  }else Log.d(TAG, "createMedicineTime: $response")
               }
            }

            for(i in 0 until getDrugCheckUid.size) {
               val getDrug = dataManager.getDrug(getDrugTimeUid[i].drugId)

               if(getDrug.uid != "") {
                  val data = MedicineTimeDTO(getDrugTimeUid[i].time)

                  val response = RetrofitAPI.api.createMedicineTime("Bearer ${getToken.access}", getDrug.uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createMedicineTime: ${response.body()}")
                     dataManager.updateStr(TABLE_DRUG_TIME, "uid", response.body()!!.uid, "id", getDrugTimeUid[i].id)
                  }else Log.d(TAG, "createMedicineTime: $response")
               }
            }

            if(getDrugCheckUid.drugTimeId != 0) {
               val getDrugTime = dataManager.getDrugTimeUidById(getDrugCheckUid.drugTimeId)

               if(getDrugTime.uid != "") {
                  val regDate = LocalDate.parse(getDrugCheckUid.regDate).atStartOfDay().format(formatter)
                  val data = MedicineIntakeDTO("BodyWell${getDrugCheckUid.id}", regDate)

                  val response = RetrofitAPI.api.createMedicineIntake("Bearer ${getToken.access}", getDrugTime.uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createMedicineIntake: ${response.body()}")
                     dataManager.updateStr(TABLE_DRUG_CHECK, "uid", response.body()!!.uid, "id", getDrugCheckUid.id)
                  }else Log.d(TAG, "createMedicineIntake: $response")
               }
            }

            if(getGoalUid.id != 0) {
               val getGoal = dataManager.getGoal(getGoalUid.id)

               if(getGoal.uid == "") {
                  val data = GoalDTO(getGoalUid.body, getGoalUid.food, getGoalUid.exercise, getGoalUid.waterVolume, getGoalUid.water,
                     getGoalUid.sleep, getGoalUid.drug)

                  val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createGoal: ${response.body()}")
                     dataManager.updateStr(TABLE_GOAL, "uid", response.body()!!.uid, "id", getGoalUid.id)
                  }else Log.d(TAG, "createGoal: $response")
               }
            }

            if(getGoalUpdated.uid != "") {
               val data = GoalDTO(getGoalUpdated.body, getGoalUpdated.food, getGoalUpdated.exercise, getGoalUpdated.waterVolume,
                  getGoalUpdated.water, getGoalUpdated.sleep, getGoalUpdated.drug)

               val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", getGoalUpdated.uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateGoal: ${response.body()}")
                  dataManager.updateInt(TABLE_GOAL, "isUpdated", 0, "id", getGoalUpdated.id)
               }else Log.d(TAG, "updateGoal: $response")
            }
         }

//         inet+=1
//         Log.d(TAG, "$inet")
         delay(10000)
      }
   }
   var inet =0

   private suspend fun refreshToken(): Boolean {
      val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
      val check = response.isSuccessful

      return if(check) {
         Log.d(TAG, "refreshToken: ${response.body()}")
         dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessRegDate = LocalDateTime.now().toString()))
         getToken = dataManager.getToken()
         true
      }else {
         Log.d(TAG, "refreshToken: $response")
         false
      }
   }

   private fun login(): Boolean {
      return true
   }
}
