package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.makebodywell.bodywell.adapter.SectionPageAdapter
import com.makebodywell.bodywell.databinding.ActivityInitBinding
import kotlin.system.exitProcess

class InitActivity : AppCompatActivity() {
   private var _binding: ActivityInitBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

   private var adapter: SectionPageAdapter = SectionPageAdapter(supportFragmentManager)

   private var googleSignInClient: GoogleSignInClient? = null
   private var googleSignInOptions: GoogleSignInOptions? = null
   private var googleSignInAccount: GoogleSignInAccount? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInitBinding.inflate(layoutInflater)
      setContentView(binding.root)

      adapter.addFragment(SlideFragment1(), "1")
      adapter.addFragment(SlideFragment2(), "2")
      adapter.addFragment(SlideFragment3(), "3")
      adapter.addFragment(SlideFragment4(), "4")
      binding.viewpager.adapter = adapter

      binding.dotsIndicator.setViewPager(binding.viewpager)

      binding.cvStart.setOnClickListener {
         startActivity(Intent(this@InitActivity, LoginActivity::class.java))
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

   private fun loginCheck() {
      var userType = ""
      var userName = ""
      var userEmail = ""
      googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
      googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions!!)
      googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
      val isLogin = googleSignInAccount != null

      // 사용자가 구글 로그인하면 Google SDK 에서 사용자 정보 가져오기
      if(googleSignInAccount != null) {
         userType = "google"
         userName = googleSignInAccount!!.displayName.toString()
         userEmail = googleSignInAccount!!.email.toString()
      }

      // 사용자가 로그인했는지 확인하고 activity 로 이동
      if(!isLogin) {
         startActivity(Intent(this, LoginActivity::class.java))
      }else {
         val intent = Intent(this, MainActivity::class.java)
         intent.putExtra("userType", userType)
         intent.putExtra("userName", userName)
         intent.putExtra("userEmail", userEmail)
         startActivity(intent)
      }
   }

   override fun onBackPressed() {
      if(System.currentTimeMillis() - backWait >= 2000) {
         backWait = System.currentTimeMillis()
         Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
      } else {
         ActivityCompat.finishAffinity(this) // 액티비티 종료
         System.runFinalization()
         exitProcess(0) // 프로세스 종료
      }
   }
}