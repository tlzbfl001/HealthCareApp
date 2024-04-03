package com.makebodywell.bodywell.util

import android.app.Application
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.makebodywell.bodywell.BodyMeasurementQuery
import com.makebodywell.bodywell.CreateActivityMutation
import com.makebodywell.bodywell.CreateBodyMeasurementMutation
import com.makebodywell.bodywell.CreateDeviceMutation
import com.makebodywell.bodywell.CreateHealthMutation
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.CreateUserKakaoMutation
import com.makebodywell.bodywell.CreateUserNaverMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.MeQuery
import com.makebodywell.bodywell.RefreshTokenMutation
import com.makebodywell.bodywell.RemoveUserMutation
import com.makebodywell.bodywell.UpdateBodyMeasurementMutation
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateActivityInput
import com.makebodywell.bodywell.type.CreateBodyMeasurementInput
import com.makebodywell.bodywell.type.CreateDeviceInput
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.type.UpdateBodyMeasurementInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.apolloClient
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager? = null
   private var user = User()
   private var token = Token()
   private var accessCheck = false
   private var loginCheck = false

   init {
      dataManager = DataManager(context)
      dataManager!!.open()

      user = dataManager!!.getUser()
      token = dataManager!!.getToken()

      if(user.userId == null || user.userId == "" || user.deviceId == null || user.deviceId == "" || user.healthId == null || user.healthId == "" ||
         user.activityId == null || user.activityId == "" || user.bodyMeasurementId == null || user.bodyMeasurementId == "" || token.accessToken == "") {
         if(networkStatusCheck(context)){
            register()
         }
      }else {
         updateData()
      }
   }

   private fun updateData() = viewModelScope.launch {
      while(true) {
         if(networkStatusCheck(context)){
            val body = dataManager!!.getBody(LocalDate.now().toString())
            val accessDiff = Duration.between(LocalDateTime.parse(token.accessTokenRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(token.refreshTokenRegDate), LocalDateTime.now())
            Log.d(TAG, "accessDiff: ${accessDiff.toHours()}/${accessDiff.toMinutes()}/${accessDiff.seconds}")
            Log.d(TAG, "refreshDiff: ${refreshDiff.toHours()}/${refreshDiff.toMinutes()}/${refreshDiff.seconds}")

            if((accessDiff.toHours() in 1..335) && !accessCheck) {
               accessCheck = refreshToken()
            }

            if(refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            if(body.regDate != "") {
               val updateBody = apolloClient.mutation(UpdateBodyMeasurementMutation(bodyMeasurementId = user.bodyMeasurementId!!, UpdateBodyMeasurementInput(
                  height = Optional.present(body.height), bodyFatPercentage = Optional.present(body.fat),
                  startedAt = Optional.present(LocalDate.now().toString()), endedAt = Optional.present(LocalDate.now().toString()))
               )).addHttpHeader(
                  "Authorization", "Bearer ${token.accessToken}"
               ).execute()

               if(updateBody.data == null) {
                  register()
               }

               val test = apolloClient.query(BodyMeasurementQuery(
                  bodyMeasurementId = user.bodyMeasurementId!!
               )).addHttpHeader(
                  "Authorization",
                  "Bearer ${token.accessToken}"
               ).execute()

               if(test.data != null){
                  Toast.makeText(context, "${test.data!!.bodyMeasurement.bodyMeasurement.height}/" +
                     "${test.data!!.bodyMeasurement.bodyMeasurement.bodyFatPercentage}", Toast.LENGTH_SHORT).show()
               }
            }
         }

         delay(10000)
      }
   }

   private fun register() {
      when(user.type) {
         "google" -> {
            viewModelScope.launch {
               val createUser = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                  idToken = user.idToken.toString()
               ))).execute()

               val loginUser = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                  idToken = user.idToken.toString()
               ))).execute()

               if(loginUser.data != null) {
                  val me = apolloClient.query(MeQuery()).addHttpHeader(
                     "Authorization",
                     "Bearer ${loginUser.data!!.loginUserGoogle.accessToken}"
                  ).execute()

                  if(me.data != null) {
                     apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserGoogle.accessToken}"
                     ).execute()

                     apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     if(login()) createData()
                  }
               }
            }
         }
         "naver" -> {
            viewModelScope.launch {
               apolloClient.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
                  accessToken = user.idToken.toString()
               ))).execute()

               val loginUser = apolloClient.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
                  accessToken = user.idToken.toString()
               ))).execute()

               if(loginUser.data != null) {
                  val me = apolloClient.query(MeQuery()).addHttpHeader(
                     "Authorization",
                     "Bearer ${loginUser.data!!.loginUserNaver.accessToken}"
                  ).execute()

                  if(me.data != null) {
                     apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserNaver.accessToken}"
                     ).execute()

                     apolloClient.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
                        accessToken = user.idToken.toString()
                     ))).execute()

                     if(login()) createData()
                  }
               }
            }
         }
         "kakao" -> {
            viewModelScope.launch {
               apolloClient.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
                  idToken = user.idToken.toString()
               ))).execute()

               val loginUser = apolloClient.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
                  idToken = user.idToken.toString()
               ))).execute()

               if(loginUser.data != null) {
                  val me = apolloClient.query(MeQuery()).addHttpHeader(
                     "Authorization",
                     "Bearer ${loginUser.data!!.loginUserKakao.accessToken}"
                  ).execute()

                  if(me.data != null) {
                     apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserKakao.accessToken}"
                     ).execute()

                     apolloClient.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     if(login()) createData()
                  }
               }
            }
         }
      }
   }

   private suspend fun login(): Boolean {
      var result = false
      var access = ""
      var refresh = ""

      when(user.type) {
         "google" -> {
            val loginUser = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            if(loginUser.data != null) {
               access = loginUser.data!!.loginUserGoogle.accessToken!!
               refresh = loginUser.data!!.loginUserGoogle.refreshToken!!
            }
         }
         "naver" -> {
            val loginUser = apolloClient.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
               accessToken = user.idToken.toString()
            ))).execute()

            if(loginUser.data != null) {
               access = loginUser.data!!.loginUserNaver.accessToken!!
               refresh = loginUser.data!!.loginUserNaver.refreshToken!!
            }
         }
         "kakao" -> {
            val loginUser = apolloClient.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            if(loginUser.data != null) {
               access = loginUser.data!!.loginUserKakao.accessToken!!
               refresh = loginUser.data!!.loginUserKakao.refreshToken!!
            }
         }
      }

      if(access != "" && refresh != "") {
         if(token.accessToken == "") {
            dataManager!!.insertToken(Token(accessToken = access, refreshToken = refresh,
               accessTokenRegDate = LocalDateTime.now().toString(), refreshTokenRegDate = LocalDateTime.now().toString()))
         }else {
            dataManager!!.updateToken(Token(accessToken = access, refreshToken = refresh,
               accessTokenRegDate = LocalDateTime.now().toString(), refreshTokenRegDate = LocalDateTime.now().toString()))
         }

         token = dataManager!!.getToken()
         result = true
      }

      return result
   }

   private suspend fun createData() {
      val me1 = apolloClient.query(MeQuery()).addHttpHeader(
         "Authorization",
         "Bearer ${token.accessToken}"
      ).execute()

      if(me1.data != null) {
         val userId = me1.data!!.me.user.userId
         val device = if(Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) != null) {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
         }else ""
         val model = if(Build.MODEL != null) {
            Build.MODEL
         }else ""
         val manufacturer = if(Build.MANUFACTURER != null) {
            Build.MANUFACTURER
         }else ""
         val ver = if(Build.VERSION.RELEASE != null) {
            Build.VERSION.RELEASE
         }else ""

         val createDevice = apolloClient.mutation(CreateDeviceMutation(userId = userId, CreateDeviceInput(deviceHardwareVersion = ver,
            deviceLabel = device, deviceManufacturer = manufacturer, deviceModel = model, deviceName = manufacturer, deviceSoftwareVersion = ver
         ))).addHttpHeader(
            "Authorization", "Bearer ${token.accessToken}"
         ).execute()

         if(createDevice.data != null) {
            val createHealth = apolloClient.mutation(CreateHealthMutation(userId = userId)).addHttpHeader(
               "Authorization", "Bearer ${token.accessToken}"
            ).execute()

            if(createHealth.data != null) {
               val me2 = apolloClient.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer ${token.accessToken}"
               ).execute()

               if(me2.data != null) {
                  val healthId = me2.data!!.me.user.health!!.healthId
                  val deviceId = me2.data!!.me.user.devices[0].deviceId

                  val createActivity = apolloClient.mutation(CreateActivityMutation(
                     healthId = healthId, deviceId = deviceId, CreateActivityInput(startedAt = LocalDate.now().toString(), endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer ${token.accessToken}"
                  ).execute()

                  val createBodyMeasurement = apolloClient.mutation(CreateBodyMeasurementMutation(
                     healthId = healthId, deviceId = deviceId, CreateBodyMeasurementInput(startedAt = LocalDate.now().toString(), endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer ${token.accessToken}"
                  ).execute()

                  if(createActivity.data != null || createBodyMeasurement.data != null) {
                     val me3 = apolloClient.query(MeQuery()).addHttpHeader(
                        "Authorization",
                        "Bearer ${token.accessToken}"
                     ).execute()

                     if(me3.data != null) {
                        val getUser = dataManager!!.getUser(user.type!!, user.email!!)
                        if(getUser.id > 0) {
                           Log.d(TAG,  "access: ${token.accessToken}\n" +
                              "userId: $userId\n" +
                              "deviceId: $deviceId\n" +
                              "healthId: $healthId\n" +
                              "activityId: ${me3.data!!.me.user.health!!.activities[0].activityId}\n" +
                              "bodyMeasurementId: ${me3.data!!.me.user.health!!.bodyMeasurements[0].bodyMeasurementId}")

                           // 사용자정보 업데이트
                           dataManager!!.updateUser(User(id = getUser.id, userId = userId, deviceId = deviceId, healthId = healthId,
                              activityId = me3.data!!.me.user.health!!.activities[0].activityId,
                              bodyMeasurementId = me3.data!!.me.user.health!!.bodyMeasurements[0].bodyMeasurementId))

                           user = dataManager!!.getUser()
                           updateData()
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private suspend fun refreshToken(): Boolean {
      var result = false

      val refreshToken = apolloClient.mutation(RefreshTokenMutation()).addHttpHeader(
         "Authorization",
         "Bearer ${token.refreshToken}"
      ).execute()

      if(refreshToken.data != null) {
         dataManager!!.updateAccessToken(Token(accessToken = refreshToken.data!!.refreshToken.accessToken, accessTokenRegDate = LocalDateTime.now().toString()))

         token = dataManager!!.getToken()
         result = true
      }

      return result
   }
}