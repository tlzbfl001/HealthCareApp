package com.makebodywell.bodywell.view

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*

class HealthConnectTestActivity : AppCompatActivity() {

   private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }

   private var textView: TextView? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_health_connect_test)

      textView = findViewById(R.id.textView)

      val permissions = setOf(
         Permission.createReadPermission(Steps::class),
         Permission.createWritePermission(Steps::class)
      )

      // 권한 요청
      val requestPermissions = registerForActivityResult(HealthDataRequestPermissions()) { granted ->
         if (granted.containsAll(permissions)) {
            Log.e(TAG, "헬스커넥트 권한이 모두 허용되었습니다.")
         }else {
            Log.e(TAG, "헬스커넥트 권한이 거부되었습니다.")
         }
      }

      fun checkPermissionsAndRun(client: HealthConnectClient) {
         lifecycleScope.launch {
            val granted = client.permissionController.getGrantedPermissions(permissions)
            if (granted.containsAll(permissions)) {
               readStepsByTimeRange(healthConnectClient)
            } else {
               requestPermissions.launch(permissions)
            }
         }
      }

      checkPermissionsAndRun(healthConnectClient)
   }

   private suspend fun readStepsByTimeRange(healthConnectClient: HealthConnectClient) {
      try {
         var count = 0
         val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
               Steps::class,
               timeRangeFilter = TimeRangeFilter.between(
                  LocalDateTime.now().with(LocalTime.of(0, 0, 0)),
                  LocalDateTime.now()
               )
            )
         )
         for (stepRecord in response.records) {
            count += stepRecord.count.toInt()
         }
         textView?.text = count.toString()
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }
}