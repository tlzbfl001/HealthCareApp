package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.MyApp
import com.navercorp.nid.NaverIdLoginSDK

class SplashActivity : AppCompatActivity() {
   private var dataManager: DataManager? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_splash)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
      }

      dataManager = DataManager(this)
      dataManager!!.open()

      Handler().postDelayed({
         if(MyApp.prefs.getId() == 0) {
            startActivity(Intent(this, InitActivity::class.java))
         }else {
            val getUser = dataManager!!.getUser(MyApp.prefs.getId())

            when(getUser.type) {
               "google" -> {
                  val gsa = GoogleSignIn.getLastSignedInAccount(this)

                  if(gsa == null) {
                     startActivity(Intent(this, LoginActivity::class.java))
                  }else {
                     startActivity(Intent(this, MainActivity::class.java))
                  }
               }
               "naver" -> {
                  NaverIdLoginSDK.initialize(this, getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(R.string.app_name))

                  if(NaverIdLoginSDK.getAccessToken() == null) {
                     startActivity(Intent(this, LoginActivity::class.java))
                  }else {
                     startActivity(Intent(this, MainActivity::class.java))
                  }
               }
               "kakao" -> {
                  UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                     if (error != null) {
                        startActivity(Intent(this, LoginActivity::class.java))
                     }else if (tokenInfo != null) {
                        startActivity(Intent(this, MainActivity::class.java))
                     }
                  }
               }
            }
         }

         finish()
      }, 1000)
   }
}