package kr.bodywell.android.view.init

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.DeviceDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.database.DBHelper.Companion.TABLE_USER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivitySignupBinding
import kr.bodywell.android.model.Exercise
import kr.bodywell.android.model.Food
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.MyApp
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime

class SignupActivity : AppCompatActivity() {
   private var _binding: ActivitySignupBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var user = User()
   private var isAll = true
   private var isClickable = true
   private var access = ""
   private var refresh = ""
   private var userUid = ""

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

      Log.d(TAG, "user: $user")

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

            if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
               if(networkStatusCheck(this)) {
                  if(user.type != "" && user.email != "" && user.idToken != "" ) {
//                  registerUser2()

                     when(user.type) {
                        "google" -> {
                           CoroutineScope(Dispatchers.IO).launch {
                              val data = LoginDTO(user.idToken)

                              val response = RetrofitAPI.api.loginWithGoogle(data)
                              if(response.isSuccessful) {
                                 userUid = decodeToken(response.body()!!.accessToken)

                                 val deleteUser = RetrofitAPI.api.deleteUser("Bearer ${response.body()!!.accessToken}", userUid)

                                 if(deleteUser.isSuccessful) {
                                    Log.d(TAG, "deleteUser1: $deleteUser")

                                    val googleLogin = RetrofitAPI.api.loginWithGoogle(data)
                                    if(googleLogin.isSuccessful) {
                                       Log.d(TAG, "googleLogin: ${googleLogin.body()}")
                                       access = googleLogin.body()!!.accessToken
                                       refresh = googleLogin.body()!!.refreshToken
                                       userUid = decodeToken(access)

                                       registerUser1()
                                    }else {
                                       Log.e(TAG, "googleLogin1: $googleLogin")
                                       runOnUiThread { Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show() }
                                    }
                                 }else {
                                    Log.e(TAG, "deleteUser2: $deleteUser")
                                    runOnUiThread { Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show() }
                                 }
                              }else {
                                 Log.e(TAG, "googleLogin3: $response")
                                 runOnUiThread { Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show() }
                              }
                           }
                        }
                     }
                  }else Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }else Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            }else Toast.makeText(this, "필수 이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun registerUser1() {
      var getUser = dataManager.getUser(user.type, user.email)

      // 사용자 정보 저장
      if(getUser.created == "") {
         dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, created = LocalDate.now().toString()))
      }else {
         dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, created = LocalDate.now().toString()))
      }

      getUser = dataManager.getUser(user.type, user.email)

      CoroutineScope(Dispatchers.IO).launch {
         val manufacturer = if(Build.MANUFACTURER == null || Build.MANUFACTURER == "") "" else Build.MANUFACTURER
         val model = if(Build.MODEL == null || Build.MODEL == "") "" else Build.MODEL
         val hardwareVer = if(packageManager.getPackageInfo(packageName, 0).versionName == null || packageManager.getPackageInfo(packageName, 0).versionName == "") {
            "" }else packageManager.getPackageInfo(packageName, 0).versionName
         val softwareVer = if(Build.VERSION.RELEASE == null || Build.VERSION.RELEASE == "") "" else Build.VERSION.RELEASE
         val data = DeviceDTO("BodyWell${getUser.id}", "BodyWell-Android", "Android", manufacturer, model, hardwareVer, softwareVer)

         val createDevice = RetrofitAPI.api.createDevice("Bearer $access", data)
         val getProfile = RetrofitAPI.api.getProfile("Bearer $access")
         val getAllFood = RetrofitAPI.api.getAllFood("Bearer $access")
         val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer $access")

         Log.d(TAG, "createDevice: ${createDevice.body()}")
         Log.d(TAG, "getProfile: ${getProfile.body()}")
         Log.d(TAG, "getAllFood: ${getAllFood.body()}")
         Log.d(TAG, "getAllActivity: ${getAllActivity.body()}")

         if(createDevice.isSuccessful && getProfile.isSuccessful && getAllFood.isSuccessful && getAllActivity.isSuccessful) {
            MyApp.prefs.setPrefs("userId", getUser.id) // 사용자 Id 저장
            dataManager.updateUserStr("deviceUid", createDevice.body()!!.uid) // deviceUid 저장
            dataManager.updateUserStr("profileUid", getProfile.body()!!.uid) // profileUid 저장

            val getToken = dataManager.getToken()

            // 토큰 정보 저장
            if(getToken.accessCreated == "") {
               dataManager.insertToken(Token(access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
            }else {
               dataManager.updateToken(Token(access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
            }

            // 서버 데이터 저장
            for(i in 0 until getAllFood.body()!!.size) {
               dataManager.insertFood(Food(basic = 1, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
                  unit = getAllFood.body()!![i].volumeUnit, amount = getAllFood.body()!![i].volume, kcal = getAllFood.body()!![i].calories,
                  carbohydrate = getAllFood.body()!![i].carbohydrate, protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat,
                  useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
                  created = LocalDate.now().toString())
               )
            }

            for(i in 0 until getAllActivity.body()!!.size) {
               dataManager.insertExercise(Exercise(basic = 1, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name, intensity = "HIGH",
                  useDate = LocalDateTime.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth, 0, 0, 0).toString(),
                  created = LocalDate.now().toString()))
            }

            runOnUiThread{
               val dialog = Dialog(this@SignupActivity)
               dialog.setContentView(R.layout.dialog_signup)
               dialog.setCancelable(false)
               dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

               btnConfirm.setOnClickListener {
                  val intent = Intent(this@SignupActivity, InputActivity::class.java)
                  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                  startActivity(intent)
                  isClickable = true
                  dialog.dismiss()
               }

               dialog.show()
            }
         }else {
            val deleteUser = RetrofitAPI.api.deleteUser("Bearer $access", userUid)

            if(deleteUser.isSuccessful) {
               dataManager.deleteItem(TABLE_USER, "userId")
               Log.d(TAG, "deleteUser: $deleteUser")
            } else Log.e(TAG, "deleteUser: $deleteUser")

            runOnUiThread{
               Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
               isClickable = true
            }
         }
      }
   }

   private fun registerUser2() {
      val getUser = dataManager.getUser(user.type, user.email)
      val getToken = dataManager.getToken()

      // 사용자 정보 저장
      if(getUser.created == "") {
         dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, created = LocalDate.now().toString()))
      }else {
         dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, created = LocalDate.now().toString()))
      }

      val getUser2 = dataManager.getUser(user.type, user.email)

      MyApp.prefs.setPrefs("userId", getUser2.id) // 사용자 Id 저장

      // 토큰 정보 저장
      if(getToken.accessCreated == "") {
         dataManager.insertToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(),
            refreshCreated = LocalDateTime.now().toString()))
      }else {
         dataManager.updateToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(),
            refreshCreated = LocalDateTime.now().toString()))
      }

      val dialog = Dialog(this@SignupActivity)
      dialog.setContentView(R.layout.dialog_signup)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

      btnConfirm.setOnClickListener {
         val intent = Intent(this@SignupActivity, InputActivity::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
         startActivity(intent)
         dialog.dismiss()
      }

      dialog.show()
   }

   private fun decodeToken(token: String): String {
      val decodeData = String(Base64.decode(token.split(".")[1], Base64.URL_SAFE), charset("UTF-8"))
      val obj = JSONObject(decodeData)
      return obj.get("sub").toString()
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
}