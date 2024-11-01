package kr.bodywell.android.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.api.powerSync.AppService
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.api.ViewModelUtil.refreshToken
import kr.bodywell.android.api.ViewModelUtil.requestStatus
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   val dataManager = DataManager(context)
   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
      dataManager.open()

      CustomUtil.getUser = dataManager.getUser()
      CustomUtil.getToken = dataManager.getToken()
      Log.d(CustomUtil.TAG, "userId: ${CustomUtil.getUser.uid}")
      Log.d(CustomUtil.TAG, "access: ${CustomUtil.getToken.access}")

      updateData()

      CustomUtil.powerSync = AppService(context)
   }

   private fun updateData() = viewModelScope.launch {
      while(requestStatus) {
         if(networkStatus(context)) {
//            createRequest(context, dataManager)
            refreshToken(dataManager)
         }else {
            delay(15000)
         }
      }
   }

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setInt(data: Int) {
      intVM.value = data
   }
}