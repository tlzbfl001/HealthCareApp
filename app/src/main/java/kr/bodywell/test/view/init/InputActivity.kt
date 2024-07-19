package kr.bodywell.test.view.init

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kr.bodywell.test.R
import kr.bodywell.test.databinding.ActivityInputBinding

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
      }

      supportFragmentManager.beginTransaction().apply {
         replace(R.id.inputFrame, InputInfoFragment())
         commit()
      }
   }
}