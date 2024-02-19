package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
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

      if(MyApp.prefs.getId() == -1 && dataManager.getUserCount() == 0) {
         startActivity(Intent(this, InitActivity::class.java))
      }else {
         val getUser = dataManager.getUser()

         when(getUser.type) {
            "google" -> {
               val gsa = GoogleSignIn.getLastSignedInAccount(this)

               if(gsa == null) { // refresh token == null 일때 이동
                  startActivity(Intent(this, LoginActivity::class.java))
               }else {
                  startActivity(Intent(this, MainActivity::class.java))
               }
            }
            "naver" -> {
               NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, getString(R.string.app_name))

               if(NaverIdLoginSDK.getRefreshToken() == null) {
                  startActivity(Intent(this, LoginActivity::class.java))
               }else {
                  startActivity(Intent(this, MainActivity::class.java))
               }
            }
            "kakao" -> {
               UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                  if (error != null) { // refresh token == null 일때 이동
                     startActivity(Intent(this, LoginActivity::class.java))
                  }else if (tokenInfo != null) {
                     startActivity(Intent(this, MainActivity::class.java))
                  }
               }
            }
         }
      }
   }
}