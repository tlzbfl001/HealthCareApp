package com.makebodywell.bodywell.util

import android.content.Context
import android.graphics.Path.Op
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
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
      val token = dataManager!!.getToken()

      bodyMeasurementId = user.bodyMeasurementId!!
      access = token.accessToken

      Log.d(TAG, bodyMeasurementId)
      Log.d(TAG, access)
   }

   fun updateData(context: Context){
      viewModelScope.launch {
         while(true) {
            if(getNetWorkStatusCheck(context)){
               val body = dataManager!!.getBody(LocalDate.now().toString())

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

                  if(test.data != null){ // access token 확인필요
                     Toast.makeText(context, "${test.data!!.bodyMeasurement}", Toast.LENGTH_SHORT).show()
                  }
               }
            }

            delay(5000)
         }
      }
   }

   private fun getNetWorkStatusCheck(context : Context): Boolean {
      val connectManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      val networkStatus : NetworkInfo? = connectManager.activeNetworkInfo
      val connectCheck : Boolean = networkStatus?.isConnectedOrConnecting == true
      return connectCheck
   }
}