package kr.bodywell.android.view.init

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.home.MainActivity

class StartActivity : AppCompatActivity() {
   private lateinit var splashScreen: SplashScreen
   private var appUpdateManager: AppUpdateManager? = null
   private val REQUEST_CODE = 500

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      splashScreen = installSplashScreen()
      setContentView(R.layout.activity_start)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.WHITE
         navigationBarColor = Color.WHITE
      }

      splashScreen.setOnExitAnimationListener { splashScreenView ->
         ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView).run {
            duration = 5000L
            doOnEnd {
               splashScreenView.remove()
            }
            start()
         }
      }

      val dataManager = DataManager(this)
      dataManager.open()

      appUpdateManager = AppUpdateManagerFactory.create(this)
      val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

      appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
         if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
         ) { // 업데이트가 있는 경우
            requestUpdate(appUpdateInfo)
         }else { // 업데이트가 없는 경우
            startActivity()
         }
      }
   }

   // 업데이트 요청
   private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
      try {
         appUpdateManager!!.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            this,
            REQUEST_CODE
         )
      }catch(e: Exception) {
         e.printStackTrace()
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == REQUEST_CODE) {
         if(resultCode != RESULT_OK) {
            val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
               if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                  && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
               ) { // 업데이트가 있는 경우
                  requestUpdate (appUpdateInfo)
               }
            }
         }else {
            startActivity()
         }
      }
   }

   private fun startActivity() {
      if(MyApp.prefs.getUserId() < 1) {
         startActivity(Intent(this, InitActivity::class.java))
      }else {
         startActivity(Intent(this, MainActivity::class.java))
      }
   }
}