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
import kr.bodywell.android.R
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.util.MyApp.Companion.dataManager
import kr.bodywell.android.view.MainActivity

class StartActivity : AppCompatActivity() {
   private lateinit var splashScreen: SplashScreen

   override fun onCreate(savedInstanceState: Bundle?) {
      splashScreen = installSplashScreen()
      splashScreen.setOnExitAnimationListener { splashScreenView ->
         ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView).run {
            duration = 3000L
            doOnEnd {
               splashScreenView.remove()
            }
            start()
         }
      }

      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_start)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.WHITE
         navigationBarColor = Color.WHITE
      }

      val getUser = dataManager.getUser()

      if(MyApp.prefs.getUserId() < 1 || (MyApp.prefs.getUserId() > 0 && getUser.email == "")) {
         startActivity(Intent(this, InitActivity::class.java))
      }else {
         startActivity(Intent(this, MainActivity::class.java))
      }
   }
}