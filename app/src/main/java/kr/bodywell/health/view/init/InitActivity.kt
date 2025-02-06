package kr.bodywell.health.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kr.bodywell.health.adapter.PagerAdapter
import kr.bodywell.health.databinding.ActivityInitBinding
import kr.bodywell.health.util.MyApp
import kotlin.system.exitProcess

class InitActivity : AppCompatActivity() {
   private var _binding: ActivityInitBinding? = null
   private val binding get() = _binding!!

   private var pressedTime: Long = 0
   private var adapter: PagerAdapter = PagerAdapter(supportFragmentManager)
   private var appUpdateManager: AppUpdateManager? = null
   private val UPDATE_REQUEST_CODE = 500

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityInitBinding.inflate(layoutInflater)
      setContentView(binding.root)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK
      }

      MyApp.prefs.removePrefs()

      appUpdateManager = AppUpdateManagerFactory.create(this)
      requestUpdate()

      adapter.add(SlideFragment1(), "1")
      adapter.add(SlideFragment2(), "2")
      adapter.add(SlideFragment3(), "3")
      adapter.add(SlideFragment4(), "4")

      binding.viewpager.adapter = adapter
      binding.dotsIndicator.setViewPager(binding.viewpager)

      binding.cvStart.setOnClickListener {
         startActivity(Intent(this, LoginActivity::class.java))
      }

      binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
         override fun onPageSelected(position: Int) {
            when (position) {
               0 -> {
                  binding.tvStart.text = "시작"
                  binding.cvStart.setOnClickListener {
                     startActivity(Intent(this@InitActivity, LoginActivity::class.java))
                  }
               }
               1 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     binding.viewpager.currentItem = 2
                  }
               }
               2 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     binding.viewpager.currentItem = 3
                  }
               }
               3 -> {
                  binding.tvStart.text = "계속"
                  binding.cvStart.setOnClickListener {
                     startActivity(Intent(this@InitActivity, LoginActivity::class.java))
                  }
               }
            }
         }

         override fun onPageScrollStateChanged(state: Int) {}
         override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
      })
   }

   private fun requestUpdate() {
      appUpdateManager?.let {
         it.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) { // 업데이트가 있는 경우
               try {
                  appUpdateManager!!.startUpdateFlowForResult(
                     appUpdateInfo,
                     AppUpdateType.IMMEDIATE,
                     this,
                     UPDATE_REQUEST_CODE
                  )
               }catch(e: Exception) {
                  e.printStackTrace()
               }
            }
         }
      }
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if(requestCode == UPDATE_REQUEST_CODE) {
         if(resultCode != RESULT_OK) requestUpdate()
      }
   }

   override fun onBackPressed() {
      pressedTime = if(pressedTime == 0L) {
         Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
         System.currentTimeMillis()
      }else {
         val seconds = (System.currentTimeMillis() - pressedTime).toInt()
         if(seconds > 2000) {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            0
         }else {
            super.onBackPressed()
            finishAffinity()
            System.runFinalization()
            exitProcess(0)
         }
      }
   }

   override fun onResume() {
      super.onResume()
      appUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
         if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
            try {
               appUpdateManager!!.startUpdateFlowForResult(
                  appUpdateInfo,
                  AppUpdateType.IMMEDIATE,
                  this,
                  UPDATE_REQUEST_CODE
               )
            }catch(e: Exception) {
               e.printStackTrace()
            }
         }
      }
   }
}