package kr.bodywell.android.util

import android.app.Application
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DBHelper.Companion.TABLE_USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.BodyResponse
import kr.bodywell.android.service.DeviceResponse
import kr.bodywell.android.service.RetrofitAPI
import kr.bodywell.android.service.TokenResponse
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private var getUser = User()
   private var getToken = Token()
   private var getBody = Body()
   private var accessCheck = false
   private var loginCheck = false

   class Factory(private val application: Application) : ViewModelProvider.Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
         return MainViewModel(application) as T
      }
   }

   init {
      dataManager.open()

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      if(getUser.userUid == "" || getUser.deviceUid == "" || getUser.bodyUid == "" || getToken.access == "" || getToken.refresh == "") {
         loginUser()
      } else updateData()
   }

   private fun loginUser() {
      when(getUser.type) {
         "google" -> {
            RetrofitAPI.api.googleLogin(getUser.idToken).enqueue(object : Callback<TokenResponse> {
               override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                  if(response.isSuccessful) {
                     Log.d(TAG, "googleLogin: ${response.body()}")

                     if(getToken.accessRegDate == "") {
                        dataManager.insertToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                           accessRegDate = LocalDateTime.now().toString(), refreshRegDate = LocalDateTime.now().toString()))
                     }else {
                        dataManager.updateToken(Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                           accessRegDate = LocalDateTime.now().toString(), refreshRegDate = LocalDateTime.now().toString()))
                     }

                     val uid = decodeToken(response.body()!!.accessToken)
                     dataManager.updateUserStr(TABLE_USER, "userUid", uid)

                     getToken = dataManager.getToken()

                     createData()
                  }else {
                     Log.e(TAG, "googleLogin: $response")
                  }
               }

               override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                  Log.e(TAG, "googleLogin: $t")
               }
            })
         }
      }
   }

   private fun createData() {
      // deviceUid 저장
      val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
      val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
      val hardwareVer = if(context.packageManager.getPackageInfo(context.packageName, 0).versionName == null || context.packageManager.getPackageInfo(context.packageName, 0).versionName == "") {
         ""
      }else {
         context.packageManager.getPackageInfo(context.packageName, 0).versionName
      }
      val softwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE

      RetrofitAPI.api.createDevice("Bearer " + getToken.access, "BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer)
         .enqueue(object : Callback<DeviceResponse> {
         override fun onResponse(call: Call<DeviceResponse>, response: Response<DeviceResponse>) {
            if(response.isSuccessful) {
               Log.d(TAG, "createDevice: ${response.body()}")
               dataManager.updateUserStr(TABLE_USER, "deviceUid", response.body()!!.uid)
            }else {
               Log.e(TAG, "createDevice: $response")
            }
         }

         override fun onFailure(call: Call<DeviceResponse>, t: Throwable) {
            Log.e(TAG, "createDevice: $t")
         }
      })

      // bodyUid 저장
      RetrofitAPI.api.createBody("Bearer " + getToken.access, 0.0, 0.0, 0.0, 0.0,
         0.0, 0.0, 1, LocalDateTime.now().toString()).enqueue(object : Callback<BodyResponse> {
         override fun onResponse(call: Call<BodyResponse>, response: Response<BodyResponse>) {
            if(response.isSuccessful) {
               Log.d(TAG, "createBody: $response")
               Log.d(TAG, "createBody: ${response.body()}")
               dataManager.updateUserStr(TABLE_USER, "bodyUid", response.body()!!.uid)
            }else {
               Log.e(TAG, "createBody: $response")
            }
         }

         override fun onFailure(call: Call<BodyResponse>, t: Throwable) {
            Log.e(TAG, "createBody: $t")
         }
      })
   }

   private fun updateData() = viewModelScope.launch {
      while(true) {
         if(networkStatusCheck(context)) {
            getBody = dataManager.getBody(LocalDate.now().toString())
            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshRegDate), LocalDateTime.now())

            if ((accessDiff.toHours() in 1..335) && !accessCheck) {
               accessCheck = refreshToken()
            }

            if (refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            if(getBody.regDate != "") {
               RetrofitAPI.api.updateBody("Bearer " + getToken.access, getUser.bodyUid, "", "", getBody.height, getBody.weight,
                  getBody.fat, getBody.muscle, getBody.bmi, getBody.bmr, getBody.intensity, LocalDateTime.now().toString()).enqueue(object : Callback<BodyResponse> {
                  override fun onResponse(call: Call<BodyResponse>, response: Response<BodyResponse>) {
                     if(response.isSuccessful) {
                        Log.e(TAG, "onResponse: ${response.body()}")
                     }else {
                        Log.e(TAG, "onResponse: $response")
                     }
                  }

                  override fun onFailure(call: Call<BodyResponse>, t: Throwable) {
                     Log.e(TAG, "onFailure: $t")
                  }
               })
            }

            delay(10000)
         }
      }
   }

   private fun decodeToken(token: String): String {
      val decodeData = String(Base64.decode(token.split(".")[1], Base64.URL_SAFE), charset("UTF-8"))
      val obj = JSONObject(decodeData)
      return obj.get("sub").toString()
   }

   private fun login(): Boolean {
      return true
   }

   private fun refreshToken(): Boolean {
      return true
   }
}
