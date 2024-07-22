package kr.bodywell.android.view

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.VMUtil.createApiRequest
import kr.bodywell.android.util.VMUtil.createSync
import kr.bodywell.android.util.VMUtil.getToken
import kr.bodywell.android.util.VMUtil.getUser
import kr.bodywell.android.util.VMUtil.refreshToken
import kr.bodywell.android.util.VMUtil.requestST
import kr.bodywell.android.util.VMUtil.syncedST
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   @SuppressLint("StaticFieldLeak")
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)

   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      updateData()

      Log.d(TAG, "user: $getUser")
      Log.d(TAG, "access: ${getToken.access}")
   }

   private fun updateData() = viewModelScope.launch {
      while(requestST) {
         if(networkStatusCheck(context)) {
            if(!syncedST) {
               refreshToken(dataManager)
               syncedST = createSync(dataManager)
            }else {
               refreshToken(dataManager)
               createApiRequest(dataManager)
            }
         }

         delay(10000)
      }
   }

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setInt(data: Int) {
      intVM.value = data
   }
}
