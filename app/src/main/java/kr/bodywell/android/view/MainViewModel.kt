package kr.bodywell.android.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.powersync.DatabaseDriverFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.api.powerSync.SyncService
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.ViewModelUtil.refreshToken
import kr.bodywell.android.util.ViewModelUtil.requestStatus
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.getToken
import kr.bodywell.android.util.CustomUtil.getUser
import kr.bodywell.android.util.CustomUtil.powerSync
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   val dataManager = DataManager(context)
   var dateVM = MutableLiveData<LocalDate>()
   var medicineCheckVM = MutableLiveData<Int>()
   var selectedVM = MutableLiveData<Boolean>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      val driverFactory = DatabaseDriverFactory(context)
      powerSync = SyncService(context,driverFactory)

      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      while(requestStatus) {
         if(networkStatus(context)) {
            refreshToken(dataManager)
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

   fun setSelected(data: Boolean) {
      selectedVM.value = data
   }
}