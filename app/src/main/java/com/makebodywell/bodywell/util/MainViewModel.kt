package com.makebodywell.bodywell.util

import android.content.Context
import android.graphics.Path.Op
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.makebodywell.bodywell.BodyMeasurementQuery
import com.makebodywell.bodywell.CreateBodyMeasurementMutation
import com.makebodywell.bodywell.MeQuery
import com.makebodywell.bodywell.UpdateBodyMeasurementMutation
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.type.CreateBodyMeasurementInput
import com.makebodywell.bodywell.type.UpdateBodyMeasurementInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.apolloClient
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
            val body = dataManager!!.getBody(MyApp.prefs.getId(), LocalDate.now().toString())

            if(body.regDate != "") {
               val updateBody = apolloClient.mutation(UpdateBodyMeasurementMutation(
                  bodyMeasurementId = bodyMeasurementId,
                  UpdateBodyMeasurementInput(
                     bodyFatPercentage = Optional.present(body.fat), height = Optional.present(body.height),
                     startedAt = Optional.present(LocalDate.now().toString()), endedAt = Optional.present(LocalDate.now().toString())
                  )
               )).addHttpHeader(
                  "Authorization", "Bearer $access"
               ).execute()

               val test = apolloClient.query(BodyMeasurementQuery(
                  bodyMeasurementId = bodyMeasurementId
               )).addHttpHeader(
                  "Authorization",
                  "Bearer $access"
               ).execute()

               Log.d(TAG, "updateData: ${test.data!!.bodyMeasurement}")
            }

            delay(5000)
         }
      }
   }
}