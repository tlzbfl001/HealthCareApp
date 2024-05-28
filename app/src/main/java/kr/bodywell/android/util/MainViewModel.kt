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
import kr.bodywell.android.api.dto.DietDTO
import kr.bodywell.android.api.dto.Food
import kr.bodywell.android.api.dto.FoodDTO
import kr.bodywell.android.api.dto.GoalDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.api.dto.WorkoutUpdateDTO
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_GOAL
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_UNUSED
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
            val getGoalUid = dataManager.getGoalUid()
            val getGoalUpdated = dataManager.getGoalUpdated()

            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshRegDate), LocalDateTime.now())

            if ((accessDiff.toHours() in 1..335) && !refreshCheck) {
               refreshCheck = refreshToken()
            }

            if (refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            for(i in 0 until getUnused.size) {
               if(getUnused[i].type == "food") {
                  val response = RetrofitAPI.api.deleteFood("Bearer ${getToken.access}", getUnused[i].value)

                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteFood: ${response.body()}")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.d(TAG, "deleteFood: $response")
                  }
               }

               if(getUnused[i].type == "dailyFood") {
                  val response = RetrofitAPI.api.deleteDiets("Bearer ${getToken.access}", getUnused[i].value)

                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteDiets: ${response.body()}")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.d(TAG, "deleteDiets: $response")
                  }
               }

               if(getUnused[i].type == "exercise") {
                  val response = RetrofitAPI.api.deleteActivity("Bearer ${getToken.access}", getUnused[i].value)

                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteActivity: ${response.body()}")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.d(TAG, "deleteActivity: $response")
                  }
               }

               if(getUnused[i].type == "dailyExercise") {
                  val response = RetrofitAPI.api.deleteWorkout("Bearer ${getToken.access}", getUnused[i].value)

                  if(response.isSuccessful) {
                     Log.d(TAG, "deleteWorkout: ${response.body()}")
                     dataManager.deleteItem(TABLE_UNUSED, "id", getUnused[i].id)
                  }else {
                     Log.d(TAG, "deleteWorkout: $response")
                  }
               }
            }

            for(i in 0 until getFoodUid.size) {
               val data = FoodDTO("", getFoodUid[i].name, getFoodUid[i].kcal, getFoodUid[i].carbohydrate,
                  getFoodUid[i].protein, getFoodUid[i].fat, getFoodUid[i].amount, getFoodUid[i].unit, 0, "")
               val response = RetrofitAPI.api.createFood("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createFood: ${response.body()}")
                  dataManager.updateStr(TABLE_FOOD, "uid", response.body()!!.uid, "id", getFoodUid[i].id)
               }else {
                  Log.d(TAG, "createFood: $response")
               }
            }

            for(i in 0 until getFoodUpdated.size) {
               if(getFoodUpdated[i].uid != "") {
                  val data = FoodDTO("", getFoodUpdated[i].name, getFoodUpdated[i].kcal, getFoodUpdated[i].carbohydrate,
                     getFoodUpdated[i].protein, getFoodUpdated[i].fat, getFoodUpdated[i].amount, getFoodUpdated[i].unit, 0, "")
                  val response = RetrofitAPI.api.updateFood("Bearer ${getToken.access}", getFoodUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateFood: ${response.body()}")
                     dataManager.updateInt(TABLE_FOOD, "isUpdated", 0, "id", getFoodUpdated[i].id)
                  }else {
                     Log.d(TAG, "updateFood: $response")
                  }
               }
            }

//            for(i in 0 until getDailyFoodUid.size) {
//               val photos = ArrayList<String>()
//               val getImage = dataManager.getImage(getDailyFoodUid[i].type, getDailyFoodUid[i].regDate)
//
//               for(j in 0 until getImage.size) {
//                  photos.add(getImage[j].imageUri)
//               }
//
//               val getFood = dataManager.getFood("name", getDailyFoodUid[i].name)
//
//               val timeFormat = LocalDate.parse(getDailyFoodUid[i].regDate).atStartOfDay().format(formatter)
//               val data = DietDTO(getDailyFoodUid[i].id.toString(), getDailyFoodUid[i].type, "", getDailyFoodUid[i].name, getDailyFoodUid[i].kcal,
//                  getDailyFoodUid[i].carbohydrate, getDailyFoodUid[i].protein, getDailyFoodUid[i].fat, getDailyFoodUid[i].count, "",
//                  getDailyFoodUid[i].amount, getDailyFoodUid[i].unit, photos, timeFormat, Food(uid = getFood.uid))
//               val response = RetrofitAPI.api.createDiets("Bearer ${getToken.access}", data)
//
//               if(response.isSuccessful) {
//                  Log.d(TAG, "createDiets: ${response.body()}")
//                  dataManager.updateStr(TABLE_DAILY_FOOD, "uid", response.body()!!.uid, "id", getDailyFoodUid[i].id)
//               }else {
//                  Log.d(TAG, "createDiets: $response")
//               }
//            }

            for(i in 0 until getDailyFoodUpdated.size) {
               if(getDailyFoodUpdated[i].uid != "") {
                  val photos = ArrayList<String>()
                  val getImage = dataManager.getImage(getDailyFoodUpdated[i].type, getDailyFoodUpdated[i].regDate)

                  for(j in 0 until getImage.size) {
                     photos.add(getImage[j].imageUri)
                  }

                  val getFood = dataManager.getFood("name", getDailyFoodUpdated[i].name)

                  val timeFormat = LocalDate.parse(getDailyFoodUpdated[i].regDate).atStartOfDay().format(formatter)
                  val data = DietDTO(getDailyFoodUpdated[i].id.toString(), getDailyFoodUpdated[i].type, "", getDailyFoodUpdated[i].name, getDailyFoodUpdated[i].kcal,
                     getDailyFoodUpdated[i].carbohydrate, getDailyFoodUpdated[i].protein, getDailyFoodUpdated[i].fat, getDailyFoodUpdated[i].count, "",
                     getDailyFoodUpdated[i].amount, getDailyFoodUpdated[i].unit, photos, timeFormat, Food(uid = getFood.uid))
                  val response = RetrofitAPI.api.updateDiets("Bearer ${getToken.access}", getDailyFoodUpdated[i].uid, data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "updateDiets: ${response.body()}")
                     dataManager.updateInt(TABLE_DAILY_FOOD, "isUpdated", 0, "id", getDailyFoodUpdated[i].id)
                  }else {
                     Log.d(TAG, "updateDiets: $response")
                  }
               }
            }

            for(i in 0 until getWaterUid.size) {
               val data = WaterDTO(getWaterUid[i].ml, getWaterUid[i].count, getWaterUid[i].regDate)
               val response = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "createWater: ${response.body()}")
                  dataManager.updateStr(TABLE_WATER, "uid", response.body()!!.uid, "id", getWaterUid[i].id)
               }else {
                  Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                  Log.d(TAG, "createWater: $response")
               }
            }

            for(i in 0 until getWaterUpdated.size) {
               if(getWaterUpdated[i].uid != "" ) {
                  val data = WaterDTO(getWaterUpdated[i].ml, getWaterUpdated[i].count, getWaterUpdated[i].regDate)
                  val response = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWaterUpdated[i].uid!!, data)

                  if(response.isSuccessful) {
                     Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                     Log.d(TAG, "updateWater: ${response.body()}")
                     dataManager.updateInt(TABLE_WATER, "isUpdated", 0, "id", getWaterUpdated[i].id)
                  }else {
                     Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
                     Log.d(TAG, "updateWater: $response")
                  }
               }
            }

            for(i in 0 until getExerciseUid.size) {
               val data = ActivityDTO(getExerciseUid[i].name)
               val response = RetrofitAPI.api.createActivity("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createActivity: ${response.body()}")
                  dataManager.updateStr(TABLE_EXERCISE, "uid", response.body()!!.uid, "id", getExerciseUid[i].id)
               }else {
                  Log.d(TAG, "createActivity: $response")
               }
            }

            for(i in 0 until getExerciseUpdated.size) {
               val data = ActivityDTO(getExerciseUpdated[i].name)
               val response = RetrofitAPI.api.updateActivity("Bearer ${getToken.access}", getExerciseUpdated[i].uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateActivity: ${response.body()}")
                  dataManager.updateInt(TABLE_EXERCISE, "isUpdated", 0, "id", getExerciseUpdated[i].id)
               }else {
                  Log.d(TAG, "updateActivity: $response")
               }
            }

//            for(i in 0 until getDailyExerciseUid.size) {
//               val getExercise = dataManager.getExercise("name", getDailyExerciseUid[i].name)
//               val timeFormat = LocalDate.parse(getDailyExerciseUid[i].regDate).atStartOfDay().format(formatter)
//               val data = WorkoutDTO(getDailyExerciseUid[i].id.toString(), getDailyExerciseUid[i].name, getDailyExerciseUid[i].kcal, getDailyExerciseUid[i].intensity,
//                  getDailyExerciseUid[i].workoutTime, timeFormat, true, Activity(uid = getExercise.uid))
//               val response = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)
//
//               if(response.isSuccessful) {
//                  Log.d(TAG, "createWorkout: ${response.body()}")
//                  dataManager.updateStr(TABLE_DAILY_EXERCISE, "uid", response.body()!!.uid, "id", getDailyExerciseUid[i].id)
//               }else {
//                  Log.d(TAG, "createWorkout: $response")
//               }
//            }

            for(i in 0 until getDailyExerciseUpdated.size) {
               val data = WorkoutUpdateDTO(calories=getDailyExerciseUpdated[i].kcal, intensity=getDailyExerciseUpdated[i].intensity, time=getDailyExerciseUpdated[i].workoutTime)
               val response = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getDailyExerciseUpdated[i].uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateWorkout: ${response.body()}")
                  dataManager.updateInt(TABLE_DAILY_EXERCISE, "isUpdated", 0, "id", getDailyExerciseUpdated[i].id)
               }else {
                  Log.d(TAG, "updateWorkout: $response")
               }
            }

            for(i in 0 until getBodyUid.size) {
               val timeFormat = getBodyUid[i].regDate.format(formatter)
               val data = BodyDTO(getBodyUid[i].height, getBodyUid[i].weight, getBodyUid[i].bmi, getBodyUid[i].fat, getBodyUid[i].muscle, getBodyUid[i].bmr,
                  getBodyUid[i].intensity, timeFormat)
               val response = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createBody: ${response.body()}")
                  dataManager.updateStr(TABLE_BODY, "uid", response.body()!!.uid, "id", getBodyUid[i].id)
               }else {
                  Log.d(TAG, "createBody: $response")
               }
            }

            for(i in 0 until getBodyUpdated.size) {
               val timeFormat = getBodyUpdated[i].regDate.format(formatter)
               val data = BodyDTO(getBodyUpdated[i].height, getBodyUpdated[i].weight, getBodyUpdated[i].bmi, getBodyUpdated[i].fat, getBodyUpdated[i].muscle,
                  getBodyUpdated[i].bmr, getBodyUpdated[i].intensity, timeFormat)
               val response = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBodyUpdated[i].uid!!, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateBody: ${response.body()}")
                  dataManager.updateInt(TABLE_BODY, "isUpdated", 0, "id", getBodyUpdated[i].id)
               }else {
                  Log.d(TAG, "updateBody: $response")
               }
            }

            for(i in 0 until getSleepUid.size) {
               val data = SleepDTO(getSleepUid[i].startTime, getSleepUid[i].endTime)
               val response = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)

               if(response.isSuccessful) {
                  Log.d(TAG, "createSleep: ${response.body()}")
                  dataManager.updateStr(TABLE_SLEEP, "uid", response.body()!!.uid, "id", getSleepUid[i].id)
               }else {
                  Log.d(TAG, "createSleep: $response")
               }
            }

            for(i in 0 until getSleepUpdated.size) {
               val data = SleepDTO(getSleepUpdated[i].startTime, getSleepUpdated[i].endTime)
               val response = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleepUpdated[i].uid!!, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateSleep: ${response.body()}")
                  dataManager.updateInt(TABLE_SLEEP, "isUpdated", 0, "id", getSleepUpdated[i].id)
               }else {
                  Log.d(TAG, "updateSleep: $response")
               }
            }

            for(i in 0 until getGoalUid.size) {
               Log.d(TAG, "getGoalUid: $getGoalUid")
               val getGoal = dataManager.getGoal(getGoalUid[i].id)
               if(getGoal.uid == "") {
                  val data = GoalDTO(getGoalUid[i].bodyGoal, getGoalUid[i].foodGoal, getGoalUid[i].exerciseGoal, getGoalUid[i].waterGoal,
                     getGoalUid[i].sleepGoal, getGoalUid[i].drugGoal)
                  val response = RetrofitAPI.api.createGoal("Bearer ${getToken.access}", data)

                  if(response.isSuccessful) {
                     Log.d(TAG, "createGoal: ${response.body()}")
                     dataManager.updateStr(TABLE_GOAL, "uid", response.body()!!.uid, "id", getGoalUid[i].id)
                  }else {
                     Log.d(TAG, "createGoal: $response")
                  }
               }
            }

            for(i in 0 until getGoalUpdated.size) {
               val data = GoalDTO(getGoalUpdated[i].bodyGoal, getGoalUpdated[i].foodGoal, getGoalUpdated[i].exerciseGoal, getGoalUpdated[i].waterGoal,
                  getGoalUpdated[i].sleepGoal, getGoalUpdated[i].drugGoal)
               val response = RetrofitAPI.api.updateGoal("Bearer ${getToken.access}", getGoalUpdated[i].uid, data)

               if(response.isSuccessful) {
                  Log.d(TAG, "updateGoal: ${response.body()}")
                  dataManager.updateInt(TABLE_GOAL, "isUpdated", 0, "id", getGoalUpdated[i].id)
               }else {
                  Log.d(TAG, "updateGoal: $response")
               }
            }
         }

         delay(10000)
      }
   }

   private suspend fun refreshToken(): Boolean {
      val response = RetrofitAPI.api.refreshToken("Bearer " + getToken.refresh)
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
