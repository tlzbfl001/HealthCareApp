package kr.bodywell.android.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kr.bodywell.android.BuildConfig
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.home.MainActivity

class StartActivity : AppCompatActivity() {
   private val REQUEST_CODE = 500

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_start)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
      }

      val dataManager = DataManager(this)
      dataManager.open()

      checkAppUpdate()
   }

   private fun checkAppUpdate() {
      val appUpdateManager = AppUpdateManagerFactory.create(this)
      val appUpdateInfoTask = appUpdateManager.appUpdateInfo

      appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
         if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
         ) { // 업데이트가 있는 경우
            val nowVerCode = BuildConfig.VERSION_CODE
            val newVerCode = appUpdateInfo.availableVersionCode()

            if((newVerCode - nowVerCode) >= 1) {
               appUpdateManager.startUpdateFlowForResult(
                  appUpdateInfo,
                  AppUpdateType.IMMEDIATE,
                  this,
                  REQUEST_CODE
               )
            }else {
               startActivity()
            }
         }else { // 업데이트가 없는 경우
            startActivity()
         }
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == REQUEST_CODE) {
         if (resultCode != RESULT_OK) {
            Log.e(TAG, "Update flow failed! Result code: $resultCode") // 업데이트가 취소되거나 실패한 경우 업데이트를 다시 요청
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