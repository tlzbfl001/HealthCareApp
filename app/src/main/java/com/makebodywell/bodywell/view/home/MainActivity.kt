package com.makebodywell.bodywell.view.home

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityMainBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MainViewModel
import com.makebodywell.bodywell.view.note.NoteFragment
import com.makebodywell.bodywell.view.setting.SettingFragment
import com.makebodywell.bodywell.view.report.ReportBodyFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private val mainViewModel: MainViewModel by viewModels()

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

      mainViewModel.updateData()
   }
}