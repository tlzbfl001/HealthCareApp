package kr.bodywell.android.view.home

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivityMainBinding
import kr.bodywell.android.model.Constant.FILES
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.dateTimeToIso2
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.note.NoteFragment
import kr.bodywell.android.view.report.ReportBodyFragment
import kr.bodywell.android.view.setting.SettingFragment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel
   private var appUpdateManager: AppUpdateManager? = null
   private lateinit var dataManager: DataManager
   private lateinit var alarmReceiver: AlarmReceiver

   @RequiresApi(Build.VERSION_CODES.R)
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

      viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]

      dataManager = DataManager(this)
      dataManager.open()

      alarmReceiver = AlarmReceiver()

      // 약복용 데이터 업데이트
      updateMedicine()

      // 약복용 데이터 삭제
      lifecycleScope.launch {
         val watchMedicine = powerSync.watchMedicine1()
         val getAllMedicine = dataManager.getAllMedicine()

         watchMedicine.collect {
            for(i in 0 until getAllMedicine.size) {
               val result = powerSync.getMedicine(getAllMedicine[i].medicineId)
               if(result.id == "") {
                  Log.d(TAG, "알람 삭제: ${getAllMedicine[i].medicineId}")
                  dataManager.deleteMedicine(getAllMedicine[i].medicineId)
                  alarmReceiver.cancelAlarm(this@MainActivity, getAllMedicine[i].id)
               }
            }
         }
      }

      // 파일 데이터 업데이트
      updateFile()
   }

   private fun updateMedicine() {
      lifecycleScope.launch {
         var updatedAt = dataManager.getUpdatedAt("medicine")
         val watchMedicine = powerSync.watchMedicine2(updatedAt)
         var isNotEmpty = true
         Log.d(TAG, "updatedAt: $updatedAt")

         watchMedicine.collect {
            Log.d(TAG, "it: $it")
            if(it.isNotEmpty()) {
               for(i in it.indices) {
                  val getMedicine = dataManager.getMedicine(it[i].id)
                  val getTime = powerSync.getAllMedicineTime(it[i].id)
                  Log.d(TAG, "getMedicine: $getMedicine")

                  if(getMedicine == 0) { // 알람 저장
                     val timeList = ArrayList<MedicineTime>()
                     for(j in getTime.indices) timeList.add(MedicineTime(time = getTime[j].time))

                     Log.d(TAG, "timeList: $timeList")

                     if(timeList.isNotEmpty()) {
                        Log.d(TAG, "알람 저장: ${it[i].id}")
                        dataManager.insertMedicine(it[i].id)
                        val getId = dataManager.getMedicine(it[i].id)
                        val split = it[i].category.split("/", limit=3)
                        alarmReceiver.setAlarm(this@MainActivity, getId, it[i].starts, it[i].ends, timeList, "${split[0]} ${it[i].amount}${it[i].unit}")
                     }else isNotEmpty = false
                  }else { // 알람 수정
                     val timeList = ArrayList<MedicineTime>()
                     var check = false

                     for(j in getTime.indices) {
                        Log.d(TAG, "getTime[j].createdAt: ${getTime[j].createdAt} / updatedAt: $updatedAt")
                        if(getTime[j].createdAt > updatedAt) {
                           check = true
                           break
                        }
                     }

                     if(check) for(j in getTime.indices) timeList.add(MedicineTime(time = getTime[j].time))

                     Log.d(TAG, "timeList: $timeList")

                     if(timeList.isNotEmpty()) {
                        Log.d(TAG, "알람 수정: ${it[i].id}")
                        val split = it[i].category.split("/", limit=3)
                        alarmReceiver.setAlarm(this@MainActivity, getMedicine, it[i].starts, it[i].ends, timeList, "${split[0]} ${it[i].amount}${it[i].unit}")
                     }else isNotEmpty = false
                  }
               }

               if(isNotEmpty) dataManager.updateMedicineTime(dateTimeToIso2())

               delay(2000)
               updateMedicine()
            }
         }
      }
   }

   private fun updateFile() {
      lifecycleScope.launch {
         var updatedAt = dataManager.getUpdatedAt("file")
         val watchFile = powerSync.watchFile(updatedAt)

         watchFile.collect {
            Log.d(TAG, "updatedAt: $updatedAt")

            // 파일 저장
            for(i in it.indices) {
               val file1 = File(filesDir.toString() + "/" + it[i].name)
               if(!file1.exists()){
                  Log.d(TAG, "파일 저장: ${it[i]}")

                  val base64Image = it[i].data.split(",")[1]
                  val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                  val file2 = File(filesDir, it[i].name)
                  val deferred = async {
                     withContext(Dispatchers.IO) {
                        val fos = FileOutputStream(file2)
                        fos.use { fos ->
                           fos.write(imageBytes)
                        }
                     }
                  }

                  deferred.await()
               }
            }

            // 파일 삭제
            val file = File(filesDir.toString())
            val files = file.listFiles()
            val today = SimpleDateFormat("yyMMdd").format(Date())
            for(i in files.indices) {
               val fileUploadDate = if(files!![i].name.length > 6) files[i].name.substring(0, 6) else ""
               if(files[i].name.contains("jpg") && today != fileUploadDate) {
                  val getData = powerSync.getData(FILES, "name", "name", files[i].name)
                  if(getData == "") {
                     Log.d(TAG, "파일 삭제: ${files[i].name}")
                     File(filesDir, files[i].name).delete()
                  }
               }
            }

            if(it.isNotEmpty()) {
               dataManager.updateFileTime(dateTimeToIso1(Calendar.getInstance()))
               delay(2000)
               updateFile()
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