package com.makebodywell.bodywell.view

import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

class HealthConnectTestActivity : AppCompatActivity() {
   private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(this) }

   private val strDate = "2023-09-06"
   private val strTime = "10:00"
   private val zdt: ZonedDateTime = ZonedDateTime.of(
      LocalDate.parse(strDate), LocalTime.parse(strTime),
      ZoneId.systemDefault()
   )

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_health_connect_test)

      val PERMISSIONS = setOf(
         HealthPermission.getReadPermission(StepsRecord::class),
         HealthPermission.getWritePermission(StepsRecord::class),
      )

      suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
         return healthConnectClient.permissionController.getGrantedPermissions().containsAll(permissions)
      }

      fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
         return PermissionController.createRequestPermissionResultContract()
      }

      CoroutineScope(Dispatchers.IO).launch {
         while (true) {
//            readSteps(zdt.toInstant(), Instant.now())
            readSteps(healthConnectClient)
            readWeight(healthConnectClient)
            delay(3000)
         }
      }
   }

   var str = "2023-09-08 01:26:39.098"
   var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
   var dateTime = LocalDateTime.parse(str, formatter)

   private suspend fun readSteps (
      healthConnectClient: HealthConnectClient,
   ) {
      val response =
         healthConnectClient.aggregateGroupByPeriod(
            AggregateGroupByPeriodRequest(
               metrics = setOf(StepsRecord.COUNT_TOTAL),
               timeRangeFilter = TimeRangeFilter.between(dateTime, LocalDateTime.now()),
               timeRangeSlicer = Period.ofMonths(1)
            )
         )
      for (monthlyResult in response) {
         val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL]
         Log.d(TAG, totalSteps.toString())
      }
   }

   private suspend fun readWeight (
      healthConnectClient: HealthConnectClient,
   ) {
      try {
         val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
               WeightRecord::class,
               timeRangeFilter = TimeRangeFilter.between(zdt.toInstant(), Instant.now())
            )
         )
         response.records.forEach {
            Log.d(TAG, it.weight.toString())
         }
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }
}