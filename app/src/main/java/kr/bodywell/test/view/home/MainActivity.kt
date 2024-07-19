package kr.bodywell.test.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kr.bodywell.test.R
import kr.bodywell.test.databinding.ActivityMainBinding
import kr.bodywell.test.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.test.util.MainViewModel
import kr.bodywell.test.view.note.NoteFragment
import kr.bodywell.test.view.report.ReportBodyFragment
import kr.bodywell.test.view.setting.SettingFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private lateinit var viewModel: MainViewModel

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      setFragment()

      // 하단메뉴
      binding.navigation.setOnNavigationItemSelectedListener {
         when(it.itemId) {
            R.id.menu1 -> setFragment()
            R.id.menu2 -> replaceFragment1(this, ReportBodyFragment())
            R.id.menu3 -> replaceFragment1(this, NoteFragment())
            R.id.menu4 -> replaceFragment1(this, SettingFragment())
         }
         true
      }

      viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
   }

   private fun setFragment() {
      supportFragmentManager.beginTransaction().apply {
         replace(R.id.mainFrame, MainFragment())
         commit()
      }
   }
}