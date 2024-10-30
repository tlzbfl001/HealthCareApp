package kr.bodywell.android.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.ViewModelUtil.createRequest
import kr.bodywell.android.util.ViewModelUtil.requestStatus
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {
   private val context = application.applicationContext
   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()

   init {
//      updateData()
   }

   private fun updateData() = viewModelScope.launch {
      val dataManager = DataManager(context)
      dataManager.open()

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