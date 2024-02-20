package com.makebodywell.bodywell.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   @SuppressLint("StaticFieldLeak")
   private val context = application.applicationContext
   private var dataManager: DataManager? = null
   private var user = User()
   private var token = Token()
   private var access = ""
   private var bodyMeasurementId = ""

   init {
      dataManager = DataManager(context)
      dataManager!!.open()

      user = dataManager!!.getUser()
      token = dataManager!!.getToken()

      if(user.userId == null || user.userId == "" || user.deviceId == null || user.deviceId == "" || user.healthId == null ||
         user.healthId == "" || user.activityId == null || user.activityId == "" || user.bodyMeasurementId == null || user.bodyMeasurementId == "") {
         if(getNetWorkStatusCheck(context)){
            createUser()
         }
      }else {
         bodyMeasurementId = user.bodyMeasurementId!!
         access = token.accessToken
         updateData()
      }
   }

   fun updateData(){
      viewModelScope.launch {
         while(true) {
            if(getNetWorkStatusCheck(context)){
               val body = dataManager!!.getBody(LocalDate.now().toString())

               if(body.regDate != "") {
                  val updateBody = apolloClient.mutation(UpdateBodyMeasurementMutation(
                     bodyMeasurementId = bodyMeasurementId,
                     UpdateBodyMeasurementInput(
                        bodyFatPercentage = Optional.present(body.fat), height = Optional.present(body.height),
                        startedAt = Optional.present(LocalDate.now().toString()), endedAt = Optional.present(LocalDate.now().toString())
                     )
                  )).addHttpHeader(
                     "Authorization", "Bearer $access"
                  ).execute()

                  val test = apolloClient.query(BodyMeasurementQuery(
                     bodyMeasurementId = bodyMeasurementId
                  )).addHttpHeader(
                     "Authorization",
                     "Bearer $access"
                  ).execute()

                  if(test.data != null){ // access token 확인필요
                     Toast.makeText(context, "${test.data!!.bodyMeasurement}", Toast.LENGTH_SHORT).show()
                  }


                  /*val response = apolloClient.mutation(UpdateUserProfileMutation(
                     userId = getUser.userId!!, UpdateUserProfileInput(birth = Optional.present(birthday), name = Optional.present(name))
                  )).addHttpHeader(
                     "Authorization",
                     "Bearer ${getToken.accessToken}"
                  ).execute()

                  val response = apolloClient.mutation(UpdateUserProfileMutation(
                     userId = getUser.userId!!, UpdateUserProfileInput(gender = Optional.present(gender), height = Optional.present(height), weight = Optional.present(weight))
                  )).addHttpHeader(
                     "Authorization",
                     "Bearer ${getToken.accessToken}"
                  ).execute()*/
               }
            }

            delay(5000)
         }
      }
   }

   private fun createUser() {
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
                     val removeUser = apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserGoogle.accessToken}"
                     ).execute()

                     val createUser = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     val loginUser2 = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     if(loginUser2.data != null) {
                        val accessToken = loginUser2.data!!.loginUserGoogle.accessToken
                        val refreshToken = loginUser2.data!!.loginUserGoogle.refreshToken

                        val getToken = dataManager!!.getToken()
                        if(getToken.regDate == "") {
                           dataManager!!.insertToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }else {
                           dataManager!!.updateToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }

                        createData(accessToken)
                     }
                  }
               }
            }
         }
         "naver" -> {
            viewModelScope.launch {
               val createUser = apolloClient.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
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
                     val removeUser = apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserNaver.accessToken}"
                     ).execute()

                     val createUser = apolloClient.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
                        accessToken = user.idToken.toString()
                     ))).execute()

                     val loginUser2 = apolloClient.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
                        accessToken = user.idToken.toString()
                     ))).execute()

                     if(loginUser2.data != null) {
                        val accessToken = loginUser2.data!!.loginUserNaver.accessToken
                        val refreshToken = loginUser2.data!!.loginUserNaver.refreshToken

                        val getToken = dataManager!!.getToken()
                        if(getToken.regDate == "") {
                           dataManager!!.insertToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }else {
                           dataManager!!.updateToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }

                        createData(accessToken)
                     }
                  }
               }
            }
         }
         "kakao" -> {
            viewModelScope.launch {
               val createUser = apolloClient.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
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
                     val removeUser = apolloClient.mutation(RemoveUserMutation(
                        userId = me.data!!.me.user.userId
                     )).addHttpHeader(
                        "Authorization", "Bearer ${loginUser.data!!.loginUserKakao.accessToken}"
                     ).execute()

                     val createUser = apolloClient.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     val loginUser2 = apolloClient.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
                        idToken = user.idToken.toString()
                     ))).execute()

                     if(loginUser2.data != null) {
                        val accessToken = loginUser2.data!!.loginUserKakao.accessToken
                        val refreshToken = loginUser2.data!!.loginUserKakao.refreshToken

                        val getToken = dataManager!!.getToken()
                        if(getToken.regDate == "") {
                           dataManager!!.insertToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }else {
                           dataManager!!.updateToken(Token(accessToken = accessToken!!, refreshToken = refreshToken!!, regDate = LocalDate.now().toString()))
                        }

                        createData(accessToken)
                     }
                  }
               }
            }
         }
      }
   }

   private suspend fun createData(accessToken: String) {
      val me1 = apolloClient.query(MeQuery()).addHttpHeader(
         "Authorization",
         "Bearer $accessToken"
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

         val createDevice = apolloClient.mutation(CreateDeviceMutation(userId = userId,
            CreateDeviceInput(deviceHardwareVersion = ver, deviceLabel = device, deviceManufacturer = manufacturer,
               deviceModel = model, deviceName = manufacturer, deviceSoftwareVersion = ver
         ))).addHttpHeader(
            "Authorization", "Bearer $accessToken"
         ).execute()

         if(createDevice.data != null) {
            val createHealth = apolloClient.mutation(CreateHealthMutation(userId = userId)).addHttpHeader(
               "Authorization", "Bearer $accessToken"
            ).execute()

            if(createHealth.data != null) {
               val me2 = apolloClient.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer $accessToken"
               ).execute()

               if(me2.data != null) {
                  val healthId = me2.data!!.me.user.health!!.healthId
                  val deviceId = me2.data!!.me.user.devices[0].deviceId

                  val createActivity = apolloClient.mutation(CreateActivityMutation(
                     healthId = healthId, deviceId = deviceId, CreateActivityInput(startedAt = LocalDate.now().toString(),
                        endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer $accessToken"
                  ).execute()

                  val createBodyMeasurement = apolloClient.mutation(CreateBodyMeasurementMutation(
                     healthId = me2.data!!.me.user.health!!.healthId, deviceId = me2.data!!.me.user.devices[0].deviceId,
                     CreateBodyMeasurementInput(startedAt = LocalDate.now().toString(), endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer $accessToken"
                  ).execute()

                  if(createActivity.data != null || createBodyMeasurement.data != null) {
                     val me3 = apolloClient.query(MeQuery()).addHttpHeader(
                        "Authorization",
                        "Bearer $accessToken"
                     ).execute()

                     if(me3.data != null) {
                        val getUser = dataManager!!.getUser(user.type!!, user.email!!)
                        if(getUser.id != 0) {
                           Log.d(TAG, "access: $accessToken")
                           Log.d(TAG, "userId: $userId")
                           Log.d(TAG, "deviceId: $deviceId")
                           Log.d(TAG, "healthId: $healthId")
                           Log.d(TAG, "activityId: ${me3.data!!.me.user.health!!.activities[0].activityId}")
                           Log.d(TAG, "bodyMeasurementId: ${me3.data!!.me.user.health!!.bodyMeasurements[0].bodyMeasurementId}")

                           dataManager!!.updateUser(User(id = getUser.id, userId = userId, deviceId = deviceId, healthId = healthId,
                              activityId = me3.data!!.me.user.health!!.activities[0].activityId,
                              bodyMeasurementId = me3.data!!.me.user.health!!.bodyMeasurements[0].bodyMeasurementId)) // 사용자정보 저장
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private fun getNetWorkStatusCheck(context : Context): Boolean {
      val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val networkStatus : NetworkInfo? = connectManager.activeNetworkInfo
      val connectCheck : Boolean = networkStatus?.isConnectedOrConnecting == true
      return connectCheck
   }
}