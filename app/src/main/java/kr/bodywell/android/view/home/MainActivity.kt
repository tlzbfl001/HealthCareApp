package kr.bodywell.android.view.home

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kr.bodywell.android.R
import kr.bodywell.android.databinding.ActivityMainBinding
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.note.NoteFragment
import kr.bodywell.android.view.report.ReportBodyFragment
import kr.bodywell.android.view.setting.SettingFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel
   private var appUpdateManager: AppUpdateManager? = null
   private val UPDATE_REQUEST_CODE = 500

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
            R.id.menu2 -> replaceFragment1(this, ReportBodyFragment())
            R.id.menu3 -> replaceFragment1(this, NoteFragment())
            R.id.menu4 -> replaceFragment1(this, SettingFragment())
         }
         true
      }

      viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
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
                     UPDATE_REQUEST_CODE
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
      if(requestCode == UPDATE_REQUEST_CODE) {
         if(resultCode != RESULT_OK) requestUpdate()
      }
   }

   private fun setStatusBarIconColor(isBlack : Boolean) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         window.insetsController?.let {
            if(isBlack) {
               it.setSystemBarsAppearance(
                  0, // value
                  APPEARANCE_LIGHT_STATUS_BARS // mask
               )
            }else {
               it.setSystemBarsAppearance(
                  APPEARANCE_LIGHT_STATUS_BARS, // value
                  APPEARANCE_LIGHT_STATUS_BARS // mask
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
                  UPDATE_REQUEST_CODE
               )
            }catch(e: Exception) {
               e.printStackTrace()
            }
         }
      }
   }
}