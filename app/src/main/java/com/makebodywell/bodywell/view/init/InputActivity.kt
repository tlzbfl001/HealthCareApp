package com.makebodywell.bodywell.view.init

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityInputBinding
import kotlin.system.exitProcess

class InputActivity : AppCompatActivity() {
   private var _binding: ActivityInputBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInputBinding.inflate(layoutInflater)
      setContentView(binding.root)

      supportFragmentManager.beginTransaction().add(R.id.inputFrame, InputTermsFragment()).commit()
   }

   override fun onBackPressed() {
      if(System.currentTimeMillis() - backWait >= 2000 ) {
         backWait = System.currentTimeMillis()
         Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
      } else {
         ActivityCompat.finishAffinity(this)
         System.runFinalization()
         exitProcess(0)
      }
   }
}