package com.makebodywell.bodywell.view.home

import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityMainBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MainViewModel
import com.makebodywell.bodywell.view.note.NoteFragment
import com.makebodywell.bodywell.view.report.ReportBodyFragment
import com.makebodywell.bodywell.view.setting.SettingFragment
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private var pressedTime: Long = 0
   private lateinit var viewModel: MainViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      supportFragmentManager.beginTransaction().apply {
         replace(R.id.mainFrame, MainFragment())
         commit()
      }

      // 하단메뉴
      binding.navigation.setOnNavigationItemSelectedListener { item ->
         when (item.itemId) {
            R.id.menu1 -> {
               supportFragmentManager.beginTransaction().apply {
                  replace(R.id.mainFrame, MainFragment())
                  commit()
               }
            }
            R.id.menu2 -> replaceFragment1(this, ReportBodyFragment())
            R.id.menu3 -> replaceFragment1(this, NoteFragment())
            R.id.menu4 -> replaceFragment1(this, SettingFragment())
         }
         true
      }

      viewModel = ViewModelProvider(this)[MainViewModel::class.java]
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