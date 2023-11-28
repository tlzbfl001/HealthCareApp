package com.makebodywell.bodywell.view.init

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.makebodywell.bodywell.databinding.ActivityStartBinding

class  StartActivity : AppCompatActivity() {
   private var _binding: ActivityStartBinding? = null
   private val binding get() = _binding!!

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityStartBinding.inflate(layoutInflater)
      setContentView(binding.root)

      binding.cvStart.setOnClickListener {
         startActivity(Intent(this@StartActivity, MainActivity::class.java))
      }
   }
}