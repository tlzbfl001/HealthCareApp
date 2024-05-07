package kr.bodywell.android.util

import android.app.Application
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.BodyDTO
import kr.bodywell.android.api.dto.SleepDTO
import kr.bodywell.android.api.dto.WaterDTO
import kr.bodywell.android.api.dto.WorkoutDTO
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.model.Water
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
   private var getUser = User()
   private var getToken = Token()
   private var getWater = Water()
   private var getExercise = ArrayList<Exercise>()
   private var getBody = Body()
   private var getSleep = Sleep()
   private var refreshCheck = false
   private var loginCheck = false

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
            getWater = dataManager.getWater(LocalDate.now().toString())
            getExercise = dataManager.getDailyExercise(LocalDate.now().toString())
            getBody = dataManager.getBody(LocalDate.now().toString())
            getSleep = dataManager.getSleep(LocalDate.now().toString())

            val timeFormat = LocalDateTime.now().format(formatter)
            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshRegDate), LocalDateTime.now())

            if ((accessDiff.toHours() in 1..335) && !refreshCheck) {
               refreshCheck = refreshToken()
            }

            if (refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            if(getWater.regDate != "") {
               val data = WaterDTO(getWater.mL, getWater.count, getWater.regDate)
               if(getWater.waterUid == "") {
                  val createWater = RetrofitAPI.api.createWater("Bearer ${getToken.access}", data)
                  if(createWater.isSuccessful) {
                     Log.d(TAG, "createWater: ${createWater.body()}")
                     dataManager.updateStrByDate(TABLE_WATER, "waterUid", createWater.body()!!.uid, LocalDate.now().toString())
                     getWater = dataManager.getWater(LocalDate.now().toString())
                  }else {
                     Log.e(TAG, "createWater: $createWater")
                  }
               }else {
                  val updateWater = RetrofitAPI.api.updateWater("Bearer ${getToken.access}", getWater.waterUid, data)
                  if(updateWater.isSuccessful) {
                     Log.d(TAG, "updateWater: $updateWater")
                  }else {
                     Log.e(TAG, "updateWater: $updateWater")
                  }
               }
            }

//            for(i in 0 until getExercise.size) {
//               val data = WorkoutDTO(getExercise[i].name, getExercise[i].intensity, getExercise[i].workoutTime, timeFormat)
//               if(getExercise[i].exerciseUid == "") {
//                  val createWorkout = RetrofitAPI.api.createWorkout("Bearer ${getToken.access}", data)
//                  if(createWorkout.isSuccessful) {
//                     Log.d(TAG, "createWorkout: ${createWorkout.body()}")
//                     dataManager.updateStr(TABLE_DAILY_EXERCISE, "exerciseUid", createWorkout.body()!!.uid, getExercise[i].id)
//                     getExercise = dataManager.getDailyExercise(LocalDate.now().toString())
//                  }else {
//                     Log.e(TAG, "createWorkout: $createWorkout")
//                  }
//               }else {
//                  val updateWorkout = RetrofitAPI.api.updateWorkout("Bearer ${getToken.access}", getExercise[i].exerciseUid, data)
//                  if(updateWorkout.isSuccessful) {
//                     Log.d(TAG, "updateWorkout: $updateWorkout")
//                  }else {
//                     Log.e(TAG, "updateWorkout: $updateWorkout")
//                  }
//               }
//            }

            if(getBody.regDate != "") {
               val data = BodyDTO(getBody.height, getBody.weight, getBody.bmi, getBody.fat, getBody.muscle, getBody.bmr, getBody.intensity, timeFormat)
               if(getBody.bodyUid == "") {
                  val createBody = RetrofitAPI.api.createBody("Bearer ${getToken.access}", data)
                  if(createBody.isSuccessful) {
                     Log.d(TAG, "createBody: ${createBody.body()}")
                     dataManager.updateStrByDate(TABLE_BODY, "bodyUid", createBody.body()!!.uid, LocalDate.now().toString())
                     getBody = dataManager.getBody(LocalDate.now().toString())
                  }else {
                     Log.e(TAG, "createBody: $createBody")
                  }
               }else {
                  val updateBody = RetrofitAPI.api.updateBody("Bearer ${getToken.access}", getBody.bodyUid, data)
                  if(updateBody.isSuccessful) {
                     Log.d(TAG, "updateBody: $updateBody")
                  }else {
                     Log.e(TAG, "updateBody: $updateBody")
                  }
               }
            }

            if(getSleep.regDate != "") {
               val data = SleepDTO(getSleep.bedTime, getSleep.wakeTime)

               if(getSleep.sleepUid == "" || getSleep.sleepUid == null) {
                  val createSleep = RetrofitAPI.api.createSleep("Bearer ${getToken.access}", data)
                  if(createSleep.isSuccessful) {
                     Log.d(TAG, "createSleep: ${createSleep.body()}")
                     dataManager.updateStrByDate(TABLE_SLEEP, "sleepUid", createSleep.body()!!.uid, LocalDate.now().toString())
                     getSleep = dataManager.getSleep(LocalDate.now().toString())
                  }else {
                     Log.e(TAG, "createSleep: $createSleep")
                  }
               }else {
                  val updateSleep = RetrofitAPI.api.updateSleep("Bearer ${getToken.access}", getSleep.sleepUid!!, data)
                  if(updateSleep.isSuccessful) {
                     Log.d(TAG, "updateSleep: $updateSleep")
                  }else {
                     Log.e(TAG, "updateSleep: $updateSleep")
                  }
               }
            }
         }

         delay(10000)
      }
   }

   private fun login(): Boolean {
      return true
   }

   private suspend fun refreshToken(): Boolean {
      val refreshToken = RetrofitAPI.api.refreshToken("Bearer " + getToken.refresh)
      if(refreshToken.isSuccessful) {
         Log.d(TAG, "refreshToken: $refreshToken")
         dataManager.updateAccess(Token(access = refreshToken.body()!!.accessToken, accessRegDate = LocalDateTime.now().toString()))
      }else {
         Log.e(TAG, "refreshToken: $refreshToken")
      }

      getToken = dataManager.getToken()
      Log.e(TAG, "refreshToken: $getToken")

      return true
   }
}
