package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.makebodywell.bodywell.util.MyApp
import kotlin.system.exitProcess

class InitActivity : AppCompatActivity() {
   private var _binding: ActivityInitBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

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

   override fun onBackPressed() {
      if(System.currentTimeMillis() - backWait >=2000 ) {
         backWait = System.currentTimeMillis()
         Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
      } else {
         ActivityCompat.finishAffinity(this)
         System.runFinalization()
         exitProcess(0)
      }
   }
}