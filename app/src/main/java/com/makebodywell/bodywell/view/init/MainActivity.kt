package com.makebodywell.bodywell.view.init

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityMainBinding
import com.makebodywell.bodywell.view.home.MainFragment
import com.makebodywell.bodywell.view.note.NoteFragment
import com.makebodywell.bodywell.view.setting.MenuFragment
import com.makebodywell.bodywell.view.report.ReportBodyFragment

class MainActivity : AppCompatActivity() {
   private var _binding: ActivityMainBinding? = null
   private val binding get() = _binding!!

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
            R.id.menu4 -> supportFragmentManager.beginTransaction().replace(R.id.mainFrame, MenuFragment()).commit()
         }
         true
      }
   }
}