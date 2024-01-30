package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.MyApp
import com.navercorp.nid.NaverIdLoginSDK

class SplashActivity : AppCompatActivity() {
   private var dataManager: DataManager? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_splash)

      dataManager = DataManager(this)
      dataManager!!.open()

      Handler().postDelayed({
         if(MyApp.prefs.userId() == 0) {
            startActivity(Intent(this, InitActivity::class.java))
         }else {
            val getUser = dataManager!!.getUser(MyApp.prefs.userId())

            when(getUser.type) {
               "google" -> {
                  val gsa = GoogleSignIn.getLastSignedInAccount(this)
                  if(gsa == null) {
                     startActivity(Intent(this, InitActivity::class.java))
                  }else {
                     startActivity(Intent(this, MainActivity::class.java))
                  }
               }
               "naver" -> {
                  NaverIdLoginSDK.initialize(this, getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(
                     R.string.app_name))
                  Log.d(CustomUtil.TAG, "NaverIdLoginSDK: ${NaverIdLoginSDK.getAccessToken()}")
                  if(NaverIdLoginSDK.getAccessToken() == null) {
                     startActivity(Intent(this, InitActivity::class.java))
                  }else {
                     startActivity(Intent(this, MainActivity::class.java))
                  }
               }
               "kakao" -> {
                  UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                     if (error != null) {
                        startActivity(Intent(this, InitActivity::class.java))
                     } else if (tokenInfo != null) {
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