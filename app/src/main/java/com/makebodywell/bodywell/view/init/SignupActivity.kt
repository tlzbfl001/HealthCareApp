package com.makebodywell.bodywell.view.init

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivitySignupBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.service.UserResponse
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.MyApp
import java.time.LocalDate
import java.time.LocalDateTime

class SignupActivity : AppCompatActivity() {
   private var _binding: ActivitySignupBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var user = UserResponse()
   private var token = Token()
   private var isAll = true

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivitySignupBinding.inflate(layoutInflater)
      setContentView(binding.root)

      window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(this)
      dataManager.open()

      user = intent!!.getParcelableExtra("user")!!
      token = intent!!.getParcelableExtra("token")!!

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
         }else if(!isChecked && isAll) {
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
         if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
            dataManager.insertUser(User(uid = user.uid, type = user.type, email = user.email, name = user.username, regDate = LocalDate.now().toString())) // 사용자 정보 저장
            val getUser = dataManager.getUser(user.type, user.email)

            if(getUser.regDate == "") {
               Toast.makeText(this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }else {
               MyApp.prefs.setPrefs("userId", getUser.id) // 사용자 고유 Id 저장
               val getToken = dataManager.getToken(getUser.id)

               if(getToken.userId > 0) {
                  dataManager.updateToken(Token(accessToken = token.accessToken, refreshToken = token.refreshToken, accessTokenRegDate = LocalDateTime.now().toString(),
                     refreshTokenRegDate = LocalDateTime.now().toString()))
               }else {
                  dataManager.insertToken(Token(accessToken = token.accessToken, refreshToken = token.refreshToken, accessTokenRegDate = LocalDateTime.now().toString(),
                     refreshTokenRegDate = LocalDateTime.now().toString()))
               }

               signUpDialog()
            }
         }else {
            Toast.makeText(this, "필수 이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun signUpDialog() {
      val dialog = Dialog(this)
      dialog.setContentView(R.layout.dialog_signup)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

      btnConfirm.setOnClickListener {
         startActivity(Intent(this, InputActivity::class.java))
         dialog.dismiss()
      }

      dialog.show()
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

      clX.setOnClickListener {
         dialog.dismiss()
      }

      when(id) {
         1 -> terms1.visibility = View.VISIBLE
         2 -> terms2.visibility = View.VISIBLE
         3 -> terms3.visibility = View.VISIBLE
         4 -> terms4.visibility = View.VISIBLE
      }

      dialog.setContentView(bottomSheetView)
      dialog.show()
   }
}