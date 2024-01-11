package com.makebodywell.bodywell.view.init

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityMainBinding
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.note.NoteFragment
import com.makebodywell.bodywell.view.setting.SettingFragment
import com.makebodywell.bodywell.view.report.ReportBodyFragment
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityMainBinding.inflate(layoutInflater)
      setContentView(binding.root)

      // 처음화면
      supportFragmentManager.beginTransaction().add(R.id.mainFrame, MainFragment()).commit()

      // 하단메뉴
      binding.navigation.setOnNavigationItemSelectedListener { item ->
         when (item.itemId) {
            R.id.menu1 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, MainFragment()).commit()
            R.id.menu2 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, ReportBodyFragment()).commit()
            R.id.menu3 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, NoteFragment()).commit()
            R.id.menu4 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, SettingFragment()).commit()
         }
         true
      }
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