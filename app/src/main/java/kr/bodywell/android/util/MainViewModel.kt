package kr.bodywell.android.util

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Body
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.BodyResponse
import kr.bodywell.android.service.RetrofitAPI
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
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

   init {
      dataManager.open()

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(true) {
         if(networkStatusCheck(context)) {
            getBody = dataManager.getBody(LocalDate.now().toString())
            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessTokenRegDate), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshTokenRegDate), LocalDateTime.now())
//            Log.d(TAG, "accessDiff: ${accessDiff.toHours()}/${accessDiff.toMinutes()}/${accessDiff.seconds}")
//            Log.d(TAG, "refreshDiff: ${refreshDiff.toHours()}/${refreshDiff.toMinutes()}/${refreshDiff.seconds}")

            if ((accessDiff.toHours() in 1..335) && !accessCheck) {
               accessCheck = refreshToken()
            }

            if (refreshDiff.toHours() >= 336 && !loginCheck) {
               loginCheck = login()
            }

            if(getBody.regDate != "") {
               RetrofitAPI.api.updateBody("Bearer " + getToken.accessToken, getUser.bodyUid!!, "", "", getBody.height, getBody.weight,
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

   private fun login(): Boolean {
      return true
   }

   private fun refreshToken(): Boolean {
      return true
   }
}
