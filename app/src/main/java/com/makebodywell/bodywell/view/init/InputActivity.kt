package com.makebodywell.bodywell.view.init

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
   private var _binding: ActivityInputBinding? = null
   private val binding get() = _binding!!

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInputBinding.inflate(layoutInflater)
      setContentView(binding.root)

      supportFragmentManager.beginTransaction().add(R.id.inputFrame, InputTermsFragment()).commit()
   }
}