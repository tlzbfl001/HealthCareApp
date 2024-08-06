package kr.bodywell.android.view.home

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.util.CustomUtil.TAG
import java.time.*

class HealthConnectActivity : AppCompatActivity() {
   private var textView: TextView? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_health_connect)

      textView = findViewById(R.id.textView)

      val permissions = setOf(
         Permission.createReadPermission(StepsRecord::class),
         Permission.createWritePermission(StepsRecord::class)
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
            if (HealthConnectClient.isAvailable(applicationContext)) {
               readStepsByTimeRange(client)
            } else {
               requestPermissions.launch(permissions)
            }
         }
      }

      checkPermissionsAndRun(HealthConnectClient.getOrCreate(this))
   }

   private suspend fun readStepsByTimeRange(healthConnectClient: HealthConnectClient) {
      try {
         var count = 0
         val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
               StepsRecord::class,
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