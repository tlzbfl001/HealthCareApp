package com.makebodywell.bodywell.util

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.service.Dto
import com.makebodywell.bodywell.service.RetrofitAPI
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private var user = User()
   private var token = Token()
   private var accessCheck = false
   private var loginCheck = false

   init {
      dataManager.open()

      user = dataManager.getUser()
      token = dataManager.getToken()

      Log.d(TAG, "uid: ${user.uid}")
      Log.d(TAG, "accessToken: ${token.accessToken}")

      val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
      val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
      val hardwareVer = if(context.packageManager.getPackageInfo(context.packageName, 0).versionName == null || context.packageManager.getPackageInfo(context.packageName, 0).versionName == "") "" else context.packageManager.getPackageInfo(context.packageName, 0).versionName
      val softwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE

      val dto = Dto("BodyWell-Android","Android",manufacturer,model,hardwareVer,softwareVer)
      Log.d(TAG, "dto: $dto")

      RetrofitAPI.api.createDevice("Bearer "+token.accessToken, dto).enqueue(object : Callback<Dto> {
         override fun onResponse(call: Call<Dto>, response: Response<Dto>) {
            if(response.isSuccessful) {
               Log.d(TAG, "onResponse: ${response.body()}")
            }else {
               Log.e(TAG, "onResponse: $response")
            }
         }

         override fun onFailure(call: Call<Dto>, t: Throwable) {
            Log.e(TAG, "onFailure: $t")
         }
      })

      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(true) {
         if(networkStatusCheck(context)) {
            val getBody = dataManager.getBody(LocalDate.now().toString())
            val accessDiff = Duration.between(LocalDateTime.parse(token.accessTokenRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(token.refreshTokenRegDate), LocalDateTime.now())
//            Log.d(TAG, "accessDiff: ${accessDiff.toHours()}/${accessDiff.toMinutes()}/${accessDiff.seconds}")
//            Log.d(TAG, "refreshDiff: ${refreshDiff.toHours()}/${refreshDiff.toMinutes()}/${refreshDiff.seconds}")

            if ((accessDiff.toHours() in 1..335) && !accessCheck) {
               accessCheck = refreshToken()
            }

            if (refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            delay(5000)
         }
      }
   }

   private fun login(): Boolean {
      return true
   }

   private fun refreshToken(): Boolean {
      return true
   }
}
