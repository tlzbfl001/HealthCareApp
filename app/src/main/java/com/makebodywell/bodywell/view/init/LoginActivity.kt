package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import java.time.LocalDate

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private val bundle = Bundle()
   private var dataManager: DataManager? = null
   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var gsa: GoogleSignInAccount? = null

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
      dataManager!!.open()

      binding.tv1.setOnClickListener {
         startActivity(Intent(this, SignupActivity::class.java))
      }

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            googleLogin()
         }
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            naverLogin()
         }
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            kakaoLogin()
         }
      }
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

   // 구글 로그인 처리
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1000) {
         GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
            if(it.isSuccessful) {
               val getUser1 = dataManager!!.getUser("google", it.result.email.toString())

               if(getUser1.regDate == "") { // 초기 가입 작업
                  val user = User(type = "google", idToken = it.result.idToken!!, email = it.result.email!!, name = it.result.displayName!!,
                     regDate = LocalDate.now().toString())

                  val intent = Intent(this, SignupActivity::class.java)
                  intent.putExtra("user", user)
                  startActivity(intent)
               }else { // 로그인
                  val getUser2 = dataManager!!.getUser("google", it.result.email.toString())
                  MyApp.prefs.setPrefs("userId", getUser2.id)

                  startActivity(Intent(this, MainActivity::class.java))
               }
            }else {
               Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  if(NaverIdLoginSDK.getAccessToken() != null && NaverIdLoginSDK.getAccessToken() != "") {
                     val getUser1 = dataManager!!.getUser("naver", result.profile?.email.toString())

                     if(getUser1.regDate == "") {
                        val user = User(type = "naver", idToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email!!, name = result.profile?.name!!,
                           nickname = result.profile?.nickname!!, gender = result.profile?.gender!!, birthday = result.profile?.birthYear + "-" + result.profile?.birthday,
                           profileImage = result.profile?.profileImage!!, regDate = LocalDate.now().toString())

                        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                     }else {
                        val getUser2 = dataManager!!.getUser("naver", result.profile?.email.toString())
                        MyApp.prefs.setPrefs("userId", getUser2.id)

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                     }
                  }else {
                     Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
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
         }else if (token != null) { // 로그인 성공
            createKakaoUser(token)
         }
      }

      // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
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
            val getUser1 = dataManager!!.getUser("kakao", user?.kakaoAccount?.email.toString()) // 사용자 가입여부 체크

            if(getUser1.regDate == "") { // 초기 가입 작업
               val user2 = User(type = "kakao", idToken = token.idToken!!, email = user!!.kakaoAccount?.email!!, name = user.kakaoAccount?.name,
                  nickname = user.kakaoAccount?.profile?.nickname, profileImage = user.kakaoAccount?.profile?.profileImageUrl, regDate = LocalDate.now().toString())
               bundle.putParcelable("user", user2)

               val intent = Intent(this, SignupActivity::class.java)
               intent.putExtra("user", user)
               startActivity(intent)
            }else { // 로그인
               val getUser2 = dataManager!!.getUser("kakao", user?.kakaoAccount?.email.toString())
               MyApp.prefs.setPrefs("userId", getUser2.id)

               startActivity(Intent(this, MainActivity::class.java))
            }
         }
      }
   }
}