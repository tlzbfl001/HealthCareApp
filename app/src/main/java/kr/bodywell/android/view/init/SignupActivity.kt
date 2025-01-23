package kr.bodywell.android.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.databinding.ActivitySignupBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Constant.PREFERENCE
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.CustomUtil.resetAlarm
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.util.MyApp.Companion.dataManager
import kr.bodywell.android.view.MainActivity
import java.time.LocalDateTime

class SignupActivity : AppCompatActivity() {
   private var _binding: ActivitySignupBinding? = null
   private val binding get() = _binding!!

   private var isAll = true
   private var isClickable = true

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivitySignupBinding.inflate(layoutInflater)
      setContentView(binding.root)

      setStatusBar()

      val user = intent.getParcelableExtra<User>("user")!!
      val token = intent.getParcelableExtra<Token>("token")!!

      binding.ivBack.setOnClickListener {
         startActivity(Intent(this, LoginActivity::class.java))
      }

      binding.cbAll.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            binding.cb1.isChecked = true
            binding.cb2.isChecked = true
            binding.cb3.isChecked = true
            binding.cb4.isChecked = true
            isAll = true
         }else if(isAll) {
            binding.cb1.isChecked = false
            binding.cb2.isChecked = false
            binding.cb3.isChecked = false
            binding.cb4.isChecked = false
         }
      }

      binding.cb1.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb2.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb3.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb4.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.tvView1.setOnClickListener {
         showTermsDialog("서비스 이용 약관 동의", 1)
      }

      binding.tvView2.setOnClickListener {
         showTermsDialog("개인정보처리방침 동의", 2)
      }

      binding.tvView3.setOnClickListener {
         showTermsDialog("민감정보 수집 및 이용 동의", 3)
      }

      binding.tvView4.setOnClickListener {
         showTermsDialog("마케팅 수신 동의", 4)
      }

      binding.cvContinue.setOnClickListener {
         if(isClickable) {
            isClickable = false
            if(networkStatus(this)) {
               if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
                  lifecycleScope.launch {
                     var getDevice = RetrofitAPI.api.getDevice("Bearer ${token.access}")

                     if(getDevice.body()!!.isEmpty()) {
                        val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
                        val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
                        val hardwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
                        val softwareVer = if(packageManager.getPackageInfo(packageName, 0).versionName == null || packageManager.getPackageInfo(packageName, 0).versionName == "") {
                           "" } else packageManager.getPackageInfo(packageName, 0).versionName
                        RetrofitAPI.api.createDevice("Bearer ${token.access}", DeviceDTO("BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer))
                     }

                     getDevice = RetrofitAPI.api.getDevice("Bearer ${token.access}")
                     Log.d(TAG, "getDevice: ${getDevice.isSuccessful}")

                     if(getDevice.isSuccessful && getDevice.body()!![0].id != "") {
                        var getUser = dataManager.getUser(user.type, user.email)

                        // 사용자 정보 저장
                        if(getUser.email == "") {
                           dataManager.insertUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
                        }else {
                           dataManager.updateUser(User(type = user.type, idToken = user.idToken, accessToken = user.accessToken, username = user.username, email = user.email, role = user.role, uid = user.uid))
                        }

                        // 사용자ID 저장
                        getUser = dataManager.getUser(user.type, user.email)
                        MyApp.prefs.setUserId(PREFERENCE, getUser.id)

                        // 토큰 정보 저장
                        val getToken = dataManager.getToken()
                        if(getToken.accessCreated == "") {
                           dataManager.insertToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
                        }else {
                           dataManager.updateToken(Token(access = token.access, refresh = token.refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
                        }

                        // 파일 정보 저장
                        val getUpdateTime = dataManager.getUpdateTime()
                        if(getUpdateTime == "") dataManager.insertUpdateTime("1900-01-01")

                        // 로그아웃인 경우 알람 재설정
                        resetAlarm(this@SignupActivity)

                        val intent = Intent(this@SignupActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                     }else {
                        Toast.makeText(this@SignupActivity, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                     }

                     isClickable = true
                  }
               }else {
                  Toast.makeText(this, "필수 이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show()
                  isClickable = true
               }
            }else {
               Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
               isClickable = true
            }
         }
      }
   }

   private fun showTermsDialog(title: String, id: Int) {
      val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
      val bottomSheetView = layoutInflater.inflate(R.layout.dialog_terms, null)

      val tvTitle = bottomSheetView.findViewById<TextView>(R.id.tvTitle)
      val clX = bottomSheetView.findViewById<ConstraintLayout>(R.id.clX)
      val terms1 = bottomSheetView.findViewById<TextView>(R.id.terms1)
      val terms2 = bottomSheetView.findViewById<ConstraintLayout>(R.id.terms2)
      val terms3 = bottomSheetView.findViewById<ConstraintLayout>(R.id.terms3)
      val terms4 = bottomSheetView.findViewById<TextView>(R.id.terms4)

      tvTitle.text = title

      clX.setOnClickListener { dialog.dismiss() }

      when(id) {
         1 -> terms1.visibility = View.VISIBLE
         2 -> terms2.visibility = View.VISIBLE
         3 -> terms3.visibility = View.VISIBLE
         4 -> terms4.visibility = View.VISIBLE
      }

      dialog.setContentView(bottomSheetView)
      dialog.show()
   }

   private fun setStatusBar() {
      window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK
         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }
   }
}