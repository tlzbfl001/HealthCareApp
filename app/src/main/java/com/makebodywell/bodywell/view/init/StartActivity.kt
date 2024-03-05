package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.home.drug.DrugFragment
import com.navercorp.nid.NaverIdLoginSDK

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

      if(MyApp.prefs.getId() == -1 || dataManager.getUserCount() == 0 || (MyApp.prefs.getId() > -1 && dataManager.getUserById() == 0)) {
         startActivity(Intent(this, InitActivity::class.java))
      }else {
         startActivity(Intent(this, MainActivity::class.java))
      }
   }
}