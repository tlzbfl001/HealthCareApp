package kr.bodywell.android.view.init

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.BuildConfig
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivityLoginBinding
import kr.bodywell.android.model.Sleep
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.home.MainActivity
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var user = User()
   private var access = ""
   private var refresh = ""
   private var userUid = ""

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(this)
      dataManager.open()

      binding.tv1.setOnClickListener {
         startActivity(Intent(this, SignupActivity::class.java))
      }

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         if(!networkStatusCheck(this)) {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            googleLogin()
         }
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {}

      // 카카오 로그인
      binding.clKakao.setOnClickListener {}

//      Log.d(TAG, "getKeyHash: " + Utility.getKeyHash(this))
   }

   private fun googleLogin() {
      gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
         .requestEmail()
         .build()
      gsc = GoogleSignIn.getClient(this, gso!!)

      val signInIntent = gsc!!.signInIntent
      startActivityForResult(signInIntent, 1000)
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1000) {
         GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
            if(it.isSuccessful) {
               val getUser = dataManager.getUser("google", it.result.email.toString())

               if(getUser.regDate == "") { // 초기 가입
                  if(it.result.idToken != "" && it.result.idToken != null && it.result.email != "" && it.result.email != null) {
                     var check = false

                     CoroutineScope(Dispatchers.IO).launch {
                        val response = RetrofitAPI.api.getUsers()
                        if(response.isSuccessful) {
                           for(i in 0 until response.body()!!.users.size) {
                              if(response.body()!!.users[i].email == it.result.email.toString()) {
                                 check = true
                              }
                           }
                        }

                        Log.e(TAG, "check: $check")

                        if(check) {
                           Log.e(TAG, "check: 1")

                           runOnUiThread{
                              AlertDialog.Builder(this@LoginActivity, R.style.AlertDialogStyle)
                                 .setTitle("회원가입")
                                 .setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
                                 .setPositiveButton("확인") { _, _ ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                       val response = RetrofitAPI.api.googleLogin(it.result.idToken!!)
                                       if(response.isSuccessful) {
                                          user = User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!)
                                          userUid = decodeToken(response.body()!!.accessToken)
                                          access = response.body()!!.accessToken
                                          refresh = response.body()!!.refreshToken
                                          registerUser()
                                       }else {
                                          Log.e(TAG, "googleLogin: $response")
                                       }
                                    }
                                 }
                                 .setNegativeButton("취소", null)
                                 .create().show()
                           }
                        }else {
                           Log.e(TAG, "check: 2")
                           val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                           intent.putExtra("user", User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!))
                           startActivity(intent)
                        }
                     }
                  }else {
                     Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                  }
               }else { // 로그인
                  MyApp.prefs.setPrefs("userId", getUser.id)
                  startActivity(Intent(this, MainActivity::class.java))
               }
            }else {
               Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
         }
      }
   }

   /*private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val getUser = dataManager.getUser("naver", result.profile?.email.toString())

                  if(getUser.regDate == "") {
                     if(NaverIdLoginSDK.getAccessToken() == "" || NaverIdLoginSDK.getAccessToken() == null || result.profile?.email == "" || result.profile?.email == null) {
                        Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                     }else {
                        val name = if(result.profile?.name == "" || result.profile?.name == null) "" else result.profile?.name
                        val nickname = if(result.profile?.nickname == "" || result.profile?.nickname == null) "" else result.profile?.nickname
                        val birthday = if(result.profile?.birthYear == "" || result.profile?.birthYear == null || result.profile?.birthday == "" ||
                           result.profile?.birthday == null) "" else result.profile?.birthYear + "-" + result.profile?.birthday
                        val profileImage = if(result.profile?.profileImage == "" || result.profile?.profileImage == null) "" else result.profile?.profileImage

                        val user = User(type = "naver", idToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email!!, name = name,
                           nickname = nickname, birthday = birthday, profileImage = profileImage!!, regDate = LocalDate.now().toString())

                        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                     }
                  }else {
                     MyApp.prefs.setPrefs("userId", getUser.id)
                     startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                  }
               }

               override fun onError(errorCode: Int, message: String) {
                  Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
               }

               override fun onFailure(httpStatus: Int, message: String) {
                  Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
               }
            })
         }

         override fun onError(errorCode: Int, message: String) {
            Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
         }

         override fun onFailure(httpStatus: Int, message: String) {
            Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
         }
      }

      // SDK 객체 초기화
      NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, getString(
         R.string.app_name))
      NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
   }

   private fun kakaoLogin() {
      val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if (error != null) {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
         }else if (token != null) {
            createKakaoUser(token)
         }
      }

      // 카카오톡이 설치되어있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if(error != null) {
               Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
               if(error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                  return@loginWithKakaoTalk
               }else {
                  UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
               }
            }else if(token != null) {
               createKakaoUser(token)
            }
         }
      }else {
         UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
      }
   }

   private fun createKakaoUser(token: OAuthToken) {
      UserApiClient.instance.me { user, error ->
         if(error != null) {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
         }else {
            val getUser = dataManager.getUser("kakao", user?.kakaoAccount!!.email.toString()) // 사용자 가입여부 체크
            if(getUser.regDate == "") { // 초기 가입 작업
               if(token.idToken == "" || token.idToken == null || user.kakaoAccount?.email == "" || user.kakaoAccount?.email == null) {
                  Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }else {
                  val name = if(user.kakaoAccount?.name == "" || user.kakaoAccount?.name == null) "" else user.kakaoAccount?.name
                  val nickname = if(user.kakaoAccount?.profile?.nickname == "" || user.kakaoAccount?.profile?.nickname == null) "" else user.kakaoAccount?.profile?.nickname
                  val profileImage = if(user.kakaoAccount?.profile?.profileImageUrl == "" || user.kakaoAccount?.profile?.profileImageUrl == null){
                     ""
                  }else user.kakaoAccount?.profile?.profileImageUrl

                  val data = User(type = "kakao", idToken = token.idToken!!, email = user.kakaoAccount?.email!!, name = name, nickname = nickname, profileImage = profileImage,
                     regDate = LocalDate.now().toString())
                  val intent = Intent(this, SignupActivity::class.java)
                  intent.putExtra("user", data)
                  startActivity(intent)
               }
            }else { // 로그인
               MyApp.prefs.setPrefs("userId", getUser.id)
               startActivity(Intent(this, MainActivity::class.java))
            }
         }
      }
   }*/

   private fun registerUser() {
      CoroutineScope(Dispatchers.IO).launch {
         var deviceUid = ""

         val getDevices = RetrofitAPI.api.getDevices("Bearer $access")
         if(getDevices.isSuccessful) deviceUid = getDevices.body()!!.devices[0].uid

         if(deviceUid != "") {
            val getUser = dataManager.getUser(user.type, user.email)
            val getToken = dataManager.getToken()

            // 사용자 정보 저장
            if(getUser.regDate == "") {
               dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, deviceUid = deviceUid, regDate = LocalDate.now().toString()))
            }else {
               dataManager.updateUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, deviceUid = deviceUid, regDate = LocalDate.now().toString()))
            }

            val getUser2 = dataManager.getUser(user.type, user.email)
            MyApp.prefs.setPrefs("userId", getUser2.id)

            // 토큰 정보 저장
            if(getToken.accessRegDate == "") {
               dataManager.insertToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessRegDate = LocalDateTime.now().toString(),
                  refreshRegDate = LocalDateTime.now().toString()))
            }else {
               dataManager.updateToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessRegDate = LocalDateTime.now().toString(),
                  refreshRegDate = LocalDateTime.now().toString()))
            }

            val response = RetrofitAPI.api.getSleeps("Bearer $access")
            if(response.isSuccessful) {
               for(i in 0 until response.body()!!.sleeps.size) {
                  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                  val bedTime = LocalDateTime.parse(response.body()!!.sleeps[i].starts, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"))
                  val wakeTime = LocalDateTime.parse(response.body()!!.sleeps[i].ends, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"))
                  val regDate = LocalDateTime.parse(response.body()!!.sleeps[i].starts, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'")).format(formatter)

                  val startDT = LocalDateTime.of(bedTime.year, bedTime.monthValue, bedTime.dayOfMonth, bedTime.hour, bedTime.minute)
                  val endDT = LocalDateTime.of(wakeTime.year, wakeTime.monthValue, wakeTime.dayOfMonth, wakeTime.hour, wakeTime.minute)
                  val diff = Duration.between(startDT, endDT)

                  dataManager.insertSleep(Sleep(uid = response.body()!!.sleeps[i].uid, startTime = response.body()!!.sleeps[i].starts,
                     endTime = response.body()!!.sleeps[i].ends, total = diff.toMinutes().toInt(), regDate = regDate.toString()))
               }
            }

            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
         }else {
            Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun decodeToken(token: String): String {
      val decodeData = String(Base64.decode(token.split(".")[1], Base64.URL_SAFE), charset("UTF-8"))
      val obj = JSONObject(decodeData)
      return obj.get("sub").toString()
   }

   override fun onDestroy() {
      super.onDestroy()
      binding.webView.destroy()
   }
}