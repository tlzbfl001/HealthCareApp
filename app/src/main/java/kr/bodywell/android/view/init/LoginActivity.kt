package kr.bodywell.android.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.ActivityLoginBinding
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.service.MyApp
import kr.bodywell.android.util.RegisterUtil.googleLoginRequest
import kr.bodywell.android.view.home.MainActivity
import java.time.LocalDate

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null

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

      MyApp.prefs.removePrefs()

      binding.tv1.setOnClickListener {
         startActivity(Intent(this, SignupActivity::class.java))
      }

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         if(networkStatusCheck(this)) {
            googleLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         if(networkStatusCheck(this)) {
            naverLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         if(networkStatusCheck(this)) {
            kakaoLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      // Log.d(TAG, "getKeyHash: " + Utility.getKeyHash(this))
   }

   private fun googleLogin() {
      gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(resources.getString(R.string.googleWebClientId))
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
               if(getUser.createdAt == "") { // 초기 가입
//                  val intent = Intent(this, SignupActivity::class.java)
//                  intent.putExtra("user", User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!))
//                  startActivity(intent)

                  if(it.result.idToken != "" && it.result.idToken != null && it.result.email != "" && it.result.email != null) {
                     CoroutineScope(Dispatchers.IO).launch {
                        googleLoginRequest(this@LoginActivity, dataManager, it)
                     }
                  }else {
                     Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                  }
               }else { // 로그인
                  MyApp.prefs.setUserId("userId", getUser.id)
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
                  val getUser = dataManager.getUser("naver", result.profile?.email.toString())

                  if(getUser.createdAt == "") {
                     if(NaverIdLoginSDK.getAccessToken() == "" || NaverIdLoginSDK.getAccessToken() == null || result.profile?.email == "" || result.profile?.email == null) {
                        Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                     }else {
                        val user = User(type = "naver", idToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email!!, createdAt = LocalDate.now().toString())

                        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                     }
                  }else {
                     MyApp.prefs.setUserId("userId", getUser.id)
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
      NaverIdLoginSDK.initialize(this, resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))
      NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
   }

   private fun kakaoLogin() {
      val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if (error != null) Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show() else if (token != null) createKakaoUser(token)
      }

      // 카카오톡이 설치되어있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if(error != null) {
               Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
               if(error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                  return@loginWithKakaoTalk
               }else UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }else if(token != null) createKakaoUser(token)
         }
      }else UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
   }

   private fun createKakaoUser(token: OAuthToken) {
      UserApiClient.instance.me { user, error ->
         if(error != null) {
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
         }else {
            val getUser = dataManager.getUser("kakao", user?.kakaoAccount!!.email.toString())

            if(getUser.createdAt == "") { // 회원 가입
               if(token.idToken == "" || token.idToken == null || user.kakaoAccount?.email == "" || user.kakaoAccount?.email == null) {
                  Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }else {
                  val data = User(type = "kakao", idToken = token.idToken!!, email = user.kakaoAccount?.email!!, createdAt = LocalDate.now().toString())
                  val intent = Intent(this, SignupActivity::class.java)
                  intent.putExtra("user", data)
                  startActivity(intent)
               }
            }else { // 로그인
               MyApp.prefs.setUserId("userId", getUser.id)
               startActivity(Intent(this, MainActivity::class.java))
            }
         }
      }
   }

   override fun onDestroy() {
      super.onDestroy()
      binding.webView.destroy()
   }
}