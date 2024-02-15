package com.makebodywell.bodywell.util

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(context: Context) : ViewModel() {
   private var dataManager: DataManager? = null
   private var access = ""
   private var bodyMeasurementId = ""

   init {
      dataManager = DataManager(context)
      dataManager!!.open()

      val user = dataManager!!.getUser(MyApp.prefs.getId())
      val token = dataManager!!.getToken(MyApp.prefs.getId())

      bodyMeasurementId = user.bodyMeasurementId!!
      access = token.accessToken

      Log.d(TAG, "$bodyMeasurementId")
      Log.d(TAG, "$access")
   }

   fun updateData(){
      viewModelScope.launch {
         while(true) {
            val body = dataManager!!.getBody(MyApp.prefs.getId())

            Log.d(TAG, "updateData")
            delay(1000)
         }
      }
   }
}