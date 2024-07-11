package kr.bodywell.android.view.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.database.DBHelper.Companion.TABLE_TOKEN
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivityMainBinding
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Token
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.util.MainViewModel
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.note.NoteFragment
import kr.bodywell.android.view.report.ReportBodyFragment
import kr.bodywell.android.view.setting.SettingFragment
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

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

      viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[MainViewModel::class.java]
   }
}