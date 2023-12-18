package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.makebodywell.bodywell.R

class SplashActivity : AppCompatActivity() {
   private val splashDisplayLength = 1000

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_splash)

      Handler().postDelayed({
         startActivity(Intent(this@SplashActivity, InitActivity::class.java))
         finish()
      }, splashDisplayLength.toLong())
   }
}