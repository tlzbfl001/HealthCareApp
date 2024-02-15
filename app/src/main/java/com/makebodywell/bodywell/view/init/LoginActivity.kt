package com.makebodywell.bodywell.view.init

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      replaceLoginFragment1(this, LoginFragment())
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