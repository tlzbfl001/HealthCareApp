package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.adapter.SectionPageAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivityInitBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.MyApp
import kotlin.system.exitProcess

class InitActivity : AppCompatActivity() {
   private var _binding: ActivityInitBinding? = null
   private val binding get() = _binding!!

   private var pressedTime: Long = 0

   private var adapter: SectionPageAdapter = SectionPageAdapter(supportFragmentManager)

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInitBinding.inflate(layoutInflater)
      setContentView(binding.root)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK
      }

      adapter.addFragment(SlideFragment1(), "1")
      adapter.addFragment(SlideFragment2(), "2")
      adapter.addFragment(SlideFragment3(), "3")
      adapter.addFragment(SlideFragment4(), "4")

      binding.viewpager.adapter = adapter
      binding.dotsIndicator.setViewPager(binding.viewpager)

      binding.cvStart.setOnClickListener {
         startActivity(Intent(this, LoginActivity::class.java))
      }

      binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
         override fun onPageSelected(position: Int) {
            when (position) {
               0 -> {
                  binding.tvStart.text = "시작"
                  binding.cvStart.setOnClickListener {
                     startActivity(Intent(this@InitActivity, LoginActivity::class.java))
                  }
               }
               1 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     binding.viewpager.currentItem = 2
                  }
               }
               2 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     binding.viewpager.currentItem = 3
                  }
               }
               3 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     startActivity(Intent(this@InitActivity, LoginActivity::class.java))
                  }
               }
            }
         }

         override fun onPageScrollStateChanged(state: Int) {}
         override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
      })
   }

   @Deprecated("Deprecated in Java")
   override fun onBackPressed() {
      if(pressedTime == 0L) {
         Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
         pressedTime = System.currentTimeMillis()
      }else {
         val seconds = (System.currentTimeMillis() - pressedTime).toInt()
         if(seconds > 2000) {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            pressedTime = 0
         }else {
            super.onBackPressed()
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
         }
      }
   }
}