package kr.bodywell.android.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kr.bodywell.android.R
import kr.bodywell.android.databinding.ActivityMainBinding
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.view.MainViewModel
import kr.bodywell.android.view.note.NoteFragment
import kr.bodywell.android.view.report.ReportBodyFragment
import kr.bodywell.android.view.setting.SettingFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      replaceFragment()

      // 하단메뉴
      binding.navigation.setOnNavigationItemSelectedListener {
         when(it.itemId) {
            R.id.menu1 -> replaceFragment()
            R.id.menu2 -> replaceFragment1(this, ReportBodyFragment())
            R.id.menu3 -> replaceFragment1(this, NoteFragment())
            R.id.menu4 -> replaceFragment1(this, SettingFragment())
         }
         true
      }

      viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
   }

   private fun replaceFragment() {
      supportFragmentManager.beginTransaction().apply {
         replace(R.id.mainFrame, MainFragment())
         commit()
      }
   }

   override fun onDestroy() {
      super.onDestroy()
      if(viewModel.socketStatus()) viewModel.closeBtConnection()
   }
}