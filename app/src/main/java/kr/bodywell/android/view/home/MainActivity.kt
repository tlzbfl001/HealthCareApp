package kr.bodywell.android.view.home

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivityMainBinding
import kr.bodywell.android.model.Constants.FILES
import kr.bodywell.android.model.Constants.MEDICINES
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.downloadFile
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.note.NoteFragment
import kr.bodywell.android.view.report.ReportBodyFragment
import kr.bodywell.android.view.setting.SettingFragment
import java.io.File
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel
   private var appUpdateManager: AppUpdateManager? = null
   private lateinit var dataManager: DataManager
   private val fileList = arrayListOf<String>()
   private val newFileList = arrayListOf<FileItem>()

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

      val alarmReceiver = AlarmReceiver()

      lifecycleScope.launch {
         // 약복용 데이터 업데이트
         val watchMedicine = powerSync.watchMedicine(LocalDate.now().toString())
         watchMedicine.collect {
            for(i in it.indices) {
               val getMedicine = dataManager.getMedicine(it[i].id)

               // 알람 저장
               if(getMedicine.id == "") {
                  val timeList = ArrayList<MedicineTime>()
                  val getTime = powerSync.getAllMedicineTime(it[i].id)
                  for(j in getTime.indices) timeList.add(MedicineTime(time = getTime[j].time))

                  if(timeList.isNotEmpty()) {
                     dataManager.insertMedicine(it[i].id, it[i].updatedAt)
                     val getData = dataManager.getMedicine(it[i].id)
                     val split = it[i].name.split("/", limit=3)
                     alarmReceiver.setAlarm(this@MainActivity, getData.id.toInt(), it[i].starts, it[i].ends, timeList, "${split[0]} ${it[i].amount}${it[i].unit}")
                  }
               }

               // 알람 수정
               if(getMedicine.updatedAt != "" && it[i].updatedAt != getMedicine.updatedAt) {
                  val timeList = ArrayList<MedicineTime>()
                  val getTime = powerSync.getAllMedicineTime(it[i].id)
                  for(j in getTime.indices) timeList.add(MedicineTime(time = getTime[j].time))

                  if(timeList.isNotEmpty()) {
                     dataManager.updateData("medicine", it[i].updatedAt)
                     val split = it[i].name.split("/", limit=3)
                     alarmReceiver.setAlarm(this@MainActivity, getMedicine.id.toInt(), it[i].starts, it[i].ends, timeList, "${split[0]} ${it[i].amount}${it[i].unit}")
                  }
               }
            }

            // 알람 삭제
            val getAllMedicine = dataManager.getAllMedicine()
            for(j in 0 until getAllMedicine.size) {
               val result = powerSync.getMedicine(getAllMedicine[j].medicineId)
               if(result.id == "") {
                  dataManager.deleteItem("medicine", "medicineId", getAllMedicine[j].medicineId)
                  alarmReceiver.cancelAlarm(this@MainActivity, getAllMedicine[j].id)
               }
            }
         }

         // 파일 데이터 업데이트
         val getFileUpdated = dataManager.getUpdatedAt()
         val watchFile = powerSync.watchFile(getFileUpdated)
         watchFile.collect {
            // 내부저장소에 파일이 없으면 내부저장소에 저장
            for(i in it.indices) {
               val imgPath = filesDir.toString() + "/" + it[i].name
               val file = File(imgPath)

               if(!file.exists()){
                  newFileList.add(FileItem(name = it[i].name, data = it[i].data))
               }
            }

            for(i in newFileList.indices) {
               Thread {
                  downloadFile(newFileList[i].data, "$filesDir/${newFileList[i].name}")
               }.start()
            }

            // 내부저장소 파일 가져오기
            val directory = File(filesDir.toString())
            val files = directory.listFiles()
            for(element in files!!) fileList.add(element.name)

            // 삭제된 파일 내부저장소에서 제거
            for(i in fileList.indices) {
               val getData = powerSync.getData(FILES, "name", "name", fileList[i])
               if(getData == "") File(filesDir, fileList[i]).delete()
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