package kr.bodywell.test.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kr.bodywell.test.R
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.util.CustomUtil
import kr.bodywell.test.util.MyApp
import kr.bodywell.test.view.home.MainActivity

class StartActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_start)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
      }

      val dataManager = DataManager(this)
      dataManager.open()

      Log.d(CustomUtil.TAG, "state: ${MyApp.prefs.getId()}")

      if(MyApp.prefs.getId() < 1 || dataManager.getUserCount() == 0 || (MyApp.prefs.getId() > 0 && dataManager.getUser().created == "")) {
         startActivity(Intent(this, InitActivity::class.java))
      }else startActivity(Intent(this, MainActivity::class.java))
   }
}