package com.makebodywell.bodywell.view.init

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityInputBinding
import com.makebodywell.bodywell.util.CustomUtil
import kotlin.system.exitProcess

class InputActivity : AppCompatActivity() {
   private var _binding: ActivityInputBinding? = null
   private val binding get() = _binding!!

   private var pressedTime: Long = 0

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

   interface OnBackPressedListener {
      fun onBackPressed()
   }

   private var backPressedListener: OnBackPressedListener? = null

   fun setOnBackPressedListener(listener: OnBackPressedListener?) {
      backPressedListener = listener
   }

   @Deprecated("Deprecated in Java")
   override fun onBackPressed() {
      if(backPressedListener != null) {
         backPressedListener!!.onBackPressed()
      }else {
         if (pressedTime == 0L) {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            pressedTime = System.currentTimeMillis()
         } else {
            val seconds = (System.currentTimeMillis() - pressedTime).toInt()
            if (seconds > 2000) {
               Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
               pressedTime = 0
            } else {
               super.onBackPressed()
               finishAffinity()
               System.runFinalization()
               exitProcess(0)
            }
         }
      }
   }
}