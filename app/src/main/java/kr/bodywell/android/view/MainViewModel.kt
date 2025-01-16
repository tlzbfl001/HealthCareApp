package kr.bodywell.android.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersync.DatabaseDriverFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.api.powerSync.SyncService
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Token
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.getUser
import kr.bodywell.android.util.CustomUtil.powerSync
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   val dataManager = DataManager(context)
   var dateVM = MutableLiveData<LocalDate>()
   var medicineCheckVM = MutableLiveData<Int>()
   var pictureSelectedVM = MutableLiveData<Boolean>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      Log.d(TAG, "uid: ${getUser.uid}\naccess: ${getToken.access}")

      updateData()

      val driverFactory = DatabaseDriverFactory(context)
      powerSync = SyncService(context,driverFactory)
   }

   private fun updateData() = viewModelScope.launch {
      while(isActive) {
         if(networkStatus(context)) {
            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

            if (accessDiff.toHours() in 1..335) {
               val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
               if(response.isSuccessful) {
                  Log.d(TAG, "refreshToken: ${response.body()!!.accessToken}")
                  dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
                  getToken = dataManager.getToken()
               }else {
                  Log.e(TAG, "refreshToken: $response")
               }
            }

            if(refreshDiff.toHours() >= 336) {
               when(getUser.type) {
                  Constant.GOOGLE -> {
                     val response = RetrofitAPI.api.loginWithGoogle(LoginDTO(getUser.idToken))
                     if(response.isSuccessful) {
                        Log.d(TAG, "googleLogin: ${response.body()}")
                        dataManager.updateToken(
                           Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                           accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString())
                        )
                        getToken = dataManager.getToken()
                     }else {
                        Log.e(TAG, "googleLogin: $response")
                     }
                  }
                  Constant.NAVER -> {
                     val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(getUser.accessToken))
                     if(response.isSuccessful) {
                        Log.d(TAG, "naverLogin: ${response.body()}")
                        dataManager.updateToken(
                           Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                           accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString())
                        )
                        getToken = dataManager.getToken()
                     }else {
                        Log.e(TAG, "naverLogin: $response")
                     }
                  }
                  Constant.KAKAO -> {
                     val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(getUser.accessToken, getUser.idToken))
                     if(response.isSuccessful) {
                        Log.d(TAG, "kakaoLogin: ${response.body()}")
                        dataManager.updateToken(
                           Token(access = response.body()!!.accessToken, refresh = response.body()!!.refreshToken,
                           accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString())
                        )
                        getToken = dataManager.getToken()
                     }else {
                        Log.e(TAG, "kakaoLogin: $response")
                     }
                  }
               }
            }
            delay(10000)
         }else {
            delay(10000)
         }
      }
   }

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setMedicineCheck(data: Int) {
      medicineCheckVM.value = data
   }

   fun setPictureSelected(data: Boolean) {
      pictureSelectedVM.value = data
   }
}