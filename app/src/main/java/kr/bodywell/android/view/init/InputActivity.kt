package kr.bodywell.android.view.init

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import kr.bodywell.android.R
import kr.bodywell.android.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
   private var _binding: ActivityInputBinding? = null
   private val binding get() = _binding!!

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInputBinding.inflate(layoutInflater)
      setContentView(binding.root)

      window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK
         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)

         val darkModeCheck = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
         if(darkModeCheck == Configuration.UI_MODE_NIGHT_YES) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
               insetsController!!.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
         }
      }

      supportFragmentManager.beginTransaction().apply {
         replace(R.id.inputFrame, InputInfoFragment())
         commit()
      }
   }
}