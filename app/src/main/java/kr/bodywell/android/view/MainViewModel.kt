package kr.bodywell.android.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.ViewModelUtil.createRequest
import kr.bodywell.android.util.ViewModelUtil.getToken
import kr.bodywell.android.util.ViewModelUtil.getUser
import kr.bodywell.android.util.ViewModelUtil.requestStatus
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      updateData()

      Log.d(TAG, "access: ${getToken.access}")
   }

   private fun updateData() = viewModelScope.launch {
      while(requestStatus) {
         if(networkStatus(context)) {
            createRequest(context, dataManager)
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
