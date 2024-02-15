package com.makebodywell.bodywell.view.home

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityMainBinding
import com.makebodywell.bodywell.util.MainViewModel
import com.makebodywell.bodywell.view.note.NoteFragment
import com.makebodywell.bodywell.view.setting.SettingFragment
import com.makebodywell.bodywell.view.report.ReportBodyFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var mainViewModel: MainViewModel

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
            R.id.menu2 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, ReportBodyFragment()).commit()
            R.id.menu3 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, NoteFragment()).commit()
            R.id.menu4 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, SettingFragment()).commit()
         }
         true
      }

      mainViewModel = ViewModelProvider(this, ViewModelFactory(this))[MainViewModel::class.java]
      mainViewModel.updateData()
   }

   class ViewModelFactory(private val context: Context) :
      ViewModelProvider.NewInstanceFactory() {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
         if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context) as T
         }
         throw IllegalArgumentException("Unknown ViewModel class")
      }
   }
}