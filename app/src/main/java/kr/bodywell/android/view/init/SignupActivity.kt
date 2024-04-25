package kr.bodywell.android.view.init

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.databinding.ActivitySignupBinding
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.MyApp
import java.time.LocalDate

class SignupActivity : AppCompatActivity() {
   private var _binding: ActivitySignupBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var user = User()
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

      user = intent.getParcelableExtra("user")!!

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
         if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
            if(networkStatusCheck(this)) {
               if(user.idToken != "" && user.type != "" && user.email != null && user.email != "") {
                  val getUser = dataManager.getUser(user.type, user.email)

                  // 사용자 정보 저장
                  if(getUser.regDate == "") {
                     dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = "", deviceUid = "", bodyUid = "", name = "",
                        gender = "", birthday = "", image = "", height = 0.0, weight = 0.0, weightGoal = 0.0, kcalGoal = 0, waterGoal = 0, waterUnit = 0, regDate = LocalDate.now().toString()))
                  }else {
                     dataManager.updateUser(User(idToken = user.idToken, type = user.type, email = user.email, regDate = LocalDate.now().toString()))
                  }

                  val getUser2 = dataManager.getUser(user.type, user.email)

                  if(getUser2.type != "" && getUser2.email != "" && getUser2.idToken != "" && getUser2.regDate != "") {
                     MyApp.prefs.setPrefs("userId", getUser2.id) // 사용자 Id 저장
                     signUpDialog()
                  }else {
                     Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                  }
               }else {
                  Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }
            }else {
               Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
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