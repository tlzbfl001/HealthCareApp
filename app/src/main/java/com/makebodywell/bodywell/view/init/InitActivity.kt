package com.makebodywell.bodywell.view.init

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.makebodywell.bodywell.adapter.SectionPageAdapter
import com.makebodywell.bodywell.databinding.ActivityInitBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permissions1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permissions2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permissions3

class InitActivity : AppCompatActivity() {
   private var _binding: ActivityInitBinding? = null
   private val binding get() = _binding!!

   private var adapter: SectionPageAdapter = SectionPageAdapter(supportFragmentManager)

   private var googleSignInClient: GoogleSignInClient? = null
   private var googleSignInOptions: GoogleSignInOptions? = null
   private var googleSignInAccount: GoogleSignInAccount? = null

   private val permissionRequestCode = 1

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInitBinding.inflate(layoutInflater)
      setContentView(binding.root)

      // 권한 요청
      requestPermission()

      // viewPager 설정
      setupViewPager()
   }

   private fun requestPermission() {
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         for(permission in permissions3) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, arrayOf(*permissions3), permissionRequestCode)
            }
         }
      }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         for(permission in permissions2) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, arrayOf(*permissions2), permissionRequestCode)
            }
         }
      }else {
         for(permission in permissions1) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(this, arrayOf(*permissions1), permissionRequestCode)
            }
         }
      }
   }

   override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      if(requestCode == permissionRequestCode && grantResults.isNotEmpty()) {
         var result = true
         for (element in grantResults) {
            if (element == -1) {
               result = false
            }
         }
         if(!result) {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialog.setTitle("권한 설정")
            alertDialog.setMessage("권한을 허가하지 않으셨습니다.\n[설정]에서 권한을 허가해주세요.")
            alertDialog.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, _ ->
               val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                  Uri.parse("package:" + applicationContext.packageName)
               )
               startActivity(intent)
               dialogInterface.cancel()
            })
            alertDialog.setNegativeButton("취소", DialogInterface.OnClickListener { dialogInterface, _ ->
               dialogInterface.cancel()
            })
            alertDialog.show()
         }
      }
   }

   private fun setupViewPager() {
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
}