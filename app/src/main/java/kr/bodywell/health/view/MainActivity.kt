package kr.bodywell.health.view

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.powersync.DatabaseDriverFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.api.powerSync.SyncService
import kr.bodywell.health.databinding.ActivityMainBinding
import kr.bodywell.health.model.Constant.FILES
import kr.bodywell.health.model.MedicineItem
import kr.bodywell.health.model.MedicineTime
import kr.bodywell.health.service.AlarmReceiver
import kr.bodywell.health.util.CustomUtil.getToken
import kr.bodywell.health.util.CustomUtil.getUser
import kr.bodywell.health.util.CustomUtil.logout
import kr.bodywell.health.util.CustomUtil.replaceFragment1
import kr.bodywell.health.util.MyApp.Companion.dataManager
import kr.bodywell.health.util.MyApp.Companion.powerSync
import kr.bodywell.health.view.note.NoteFragment
import kr.bodywell.health.view.report.ReportBodyFragment
import kr.bodywell.health.view.setting.SettingFragment
import java.io.File

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel
   private var appUpdateManager: AppUpdateManager? = null
   private lateinit var alarmReceiver: AlarmReceiver

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      replaceFragment()

      window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         when(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setStatusBarIconColor(true)
            else -> setStatusBarIconColor(false)
         }
      }

      appUpdateManager = AppUpdateManagerFactory.create(this)
      requestUpdate()

      // 하단메뉴
      binding.navigation.setOnNavigationItemSelectedListener {
         when(it.itemId) {
            R.id.menu1 -> replaceFragment()
            R.id.menu2 -> replaceFragment1(supportFragmentManager, ReportBodyFragment())
            R.id.menu3 -> replaceFragment1(supportFragmentManager, NoteFragment())
            R.id.menu4 -> replaceFragment1(supportFragmentManager, SettingFragment())
         }
         true
      }

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

//      Log.d(TAG, "${getUser.uid}\n${getToken.access}")

      if(getUser.uid != "" && getToken.access != "") {
         val driverFactory = DatabaseDriverFactory(this)
         powerSync = SyncService(this, driverFactory)
      }

      viewModel = ViewModelProvider(this)[MainViewModel::class.java]

      viewModel.logoutState.observe(this) {
         if(it == true) logout(this)
      }

      alarmReceiver = AlarmReceiver()

      lifecycleScope.launch {
         val watchMedicine = powerSync.watchAllMedicine()

         watchMedicine.collect {
            if(it.isNotEmpty()) {
               delay(10000)

               // 약복용 데이터 업데이트
               for(i in it.indices) {
                  var getMedicine = dataManager.getMedicine(it[i].id)
                  val message = "${it[i].name} ${it[i].amount}${it[i].unit}"

                  if(getMedicine.id == 0) { // 알람 저장
                     dataManager.insertMedicine(MedicineItem(medicineId = it[i].id, name = it[i].name, amount = it[i].amount, unit = it[i].unit, starts = it[i].starts, ends = it[i].ends))
                     getMedicine = dataManager.getMedicine(it[i].id)
                     val getTime = powerSync.getAllMedicineTime(it[i].id) as ArrayList
                     for(j in getTime.indices) dataManager.insertMedicineTime(MedicineTime(userId = getMedicine.id, time = getTime[j].time))
                     alarmReceiver.setAlarm(this@MainActivity, getMedicine.id, it[i].starts, it[i].ends, getTime, message)
                  }else { // 알람 수정
                     dataManager.updateMedicine(MedicineItem(id = getMedicine.id, name = it[i].name, amount = it[i].amount, unit = it[i].unit, starts = it[i].starts, ends = it[i].ends))
                     dataManager.deleteMedicineTime(getMedicine.id)
                     val getTime = powerSync.getAllMedicineTime(it[i].id) as ArrayList
                     for(j in getTime.indices) dataManager.insertMedicineTime(MedicineTime(userId = getMedicine.id, time = getTime[j].time))
                     if(getMedicine.isSet == 1 && getTime.size > 0) {
                        alarmReceiver.setAlarm(this@MainActivity, getMedicine.id, it[i].starts, it[i].ends, getTime, "${it[i].name} ${it[i].amount}${it[i].unit}")
                     }
                  }
               }

               // 약복용 데이터 삭제
               val getMedicines = dataManager.getMedicines()
               for(i in 0 until getMedicines.size) {
                  val result = powerSync.getMedicine(getMedicines[i].medicineId)
                  if(result.id == "") {
                     dataManager.deleteMedicine(getMedicines[i].id)
                     dataManager.deleteMedicineTime(getMedicines[i].id)
                     alarmReceiver.cancelAlarm(this@MainActivity, getMedicines[i].id)
                  }
               }
            }
         }
      }

      // 파일 삭제
      lifecycleScope.launch {
         val files = File(filesDir.toString()).listFiles()
         if(files.isNotEmpty()) {
            for(i in files!!.indices) {
               if(files[i].name.contains("jpg")) {
                  val getData = powerSync.getData(FILES, "name", "name", files[i].name)
                  if(getData == "") File(filesDir, files[i].name).delete()
               }
            }
         }
      }
   }

   private fun requestUpdate() {
      appUpdateManager?.let {
         it.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) { // 업데이트가 있는 경우
               try {
                  appUpdateManager!!.startUpdateFlowForResult(
                     appUpdateInfo,
                     AppUpdateType.IMMEDIATE,
                     this,
                     500
                  )
               }catch(e: Exception) {
                  e.printStackTrace()
               }
            }
         }
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(requestCode == 500) {
         if(resultCode != RESULT_OK) requestUpdate()
      }
   }

   private fun setStatusBarIconColor(isBlack : Boolean) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         window.insetsController?.let {
            if(isBlack) {
               it.setSystemBarsAppearance(
                  0,
                  APPEARANCE_LIGHT_STATUS_BARS
               )
            }else {
               it.setSystemBarsAppearance(
                  APPEARANCE_LIGHT_STATUS_BARS,
                  APPEARANCE_LIGHT_STATUS_BARS
               )
            }
         }
      }
   }

   private fun replaceFragment() {
      supportFragmentManager.beginTransaction().apply {
         replace(R.id.mainFrame, MainFragment())
         commit()
      }
   }

   override fun onResume() {
      super.onResume()
      appUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
         if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            try {
               appUpdateManager!!.startUpdateFlowForResult(
                  appUpdateInfo,
                  AppUpdateType.IMMEDIATE,
                  this,
                  500
               )
            }catch(e: Exception) {
               e.printStackTrace()
            }
         }
      }
   }
}