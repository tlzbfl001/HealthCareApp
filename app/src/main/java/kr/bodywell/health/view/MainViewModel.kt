package kr.bodywell.health.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.bodywell.health.api.RetrofitAPI
import kr.bodywell.health.model.Token
import kr.bodywell.health.util.CalendarUtil.selectedDate
import kr.bodywell.health.util.CustomUtil.TAG
import kr.bodywell.health.util.CustomUtil.networkStatus
import kr.bodywell.health.util.CustomUtil.getToken
import kr.bodywell.health.util.MyApp.Companion.dataManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = getApplication<Application>().applicationContext
   var dateState = MutableLiveData<LocalDate>()
   var medicineCheckState = MutableLiveData<Int>()
   var itemSelectState = MutableLiveData<Boolean>()
   var logoutState = MutableLiveData<Boolean>()

   init {
      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(isActive) {
         if(networkStatus(context)) {
            val accessDiff = Duration.between(LocalDateTime.parse(getToken.accessCreated), LocalDateTime.now())
            val refreshDiff = Duration.between(LocalDateTime.parse(getToken.refreshCreated), LocalDateTime.now())

            if(accessDiff.toHours() in 1..335) {
               val response = RetrofitAPI.api.refreshToken("Bearer ${getToken.refresh}")
               if(response.isSuccessful) {
//                  Log.d(TAG, "refreshToken: ${response.body()!!.accessToken}")
                  dataManager.updateAccess(Token(access = response.body()!!.accessToken, accessCreated = LocalDateTime.now().toString()))
                  getToken = dataManager.getToken()
               }else {
                  Log.e(TAG, "refreshToken: $response")
                  logoutState.value = true
               }
            }

            if(refreshDiff.toHours() >= 336) {
               logoutState.value = true
            }

            delay(10000)
         }else {
            delay(10000)
         }
      }
   }

   fun setDateState() {
      dateState.value = selectedDate
   }

   fun setMedicineCheckState(data: Int) {
      medicineCheckState.value = data
   }

   fun setItemSelectState(data: Boolean) {
      itemSelectState.value = data
   }
}