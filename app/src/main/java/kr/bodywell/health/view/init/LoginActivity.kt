package kr.bodywell.health.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
import kotlinx.coroutines.launch
import kr.bodywell.health.R
import kr.bodywell.health.api.RetrofitAPI
import kr.bodywell.health.api.dto.KakaoLoginDTO
import kr.bodywell.health.api.dto.LoginDTO
import kr.bodywell.health.api.dto.NaverLoginDTO
import kr.bodywell.health.databinding.ActivityLoginBinding
import kr.bodywell.health.model.Constant.GOOGLE
import kr.bodywell.health.model.Constant.KAKAO
import kr.bodywell.health.model.Constant.NAVER
import kr.bodywell.health.model.Token
import kr.bodywell.health.model.User
import kr.bodywell.health.util.CustomUtil.TAG
import kr.bodywell.health.util.CustomUtil.networkStatus
import kr.bodywell.health.util.MyApp

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      setStatusBar()

      MyApp.prefs.removePrefs()

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         if(networkStatus(this)) {
            googleLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         if(networkStatus(this)) {
            naverLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         if(networkStatus(this)) {
            kakaoLogin()
         }else {
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }
      }

//      Log.i(TAG, "${Utility.getKeyHash(this)}")
   }

   private fun googleLogin() {
      val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(resources.getString(R.string.googleWebClientId))
         .requestEmail()
         .build()
      val gsc = GoogleSignIn.getClient(this, gso)

      val signInIntent = gsc.signInIntent
      startActivityForResult(signInIntent, 1000)
   }

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)

      if(requestCode == 1000) {
         val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
         if(result!!.isSuccess) {
            val acct = result.signInAccount!!
            val user = User(type = GOOGLE, email = acct.email!!, idToken = acct.idToken!!)

            lifecycleScope.launch {
               val loginWithGoogle = RetrofitAPI.api.loginWithGoogle(LoginDTO(acct.idToken!!))
               if(loginWithGoogle.isSuccessful) {
                  val token = Token(access = loginWithGoogle.body()!!.accessToken, refresh = loginWithGoogle.body()!!.refreshToken)
                  val getUser = RetrofitAPI.api.getUser("Bearer ${token.access}")
                  if(getUser.isSuccessful) {
                     val newUser = User(type = user.type, idToken = user.idToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
                     val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                     intent.putExtra("user", newUser)
                     intent.putExtra("token", token)
                     startActivity(intent)
                  }else {
                     Log.e(TAG, "getUser: $getUser")
                  }
               }else {
                  Log.e(TAG, "loginWithGoogle: $loginWithGoogle")
               }
            }
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val user = User(type = NAVER, accessToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email!!)
                  lifecycleScope.launch {
                     val response = RetrofitAPI.api.loginWithNaver(NaverLoginDTO(user.accessToken))
                     if(response.isSuccessful) {
                        val access = response.body()!!.accessToken
                        val refresh = response.body()!!.refreshToken
                        val getUser = RetrofitAPI.api.getUser("Bearer $access")

                        if(getUser.isSuccessful) {
                           val newUser = User(type = user.type, accessToken = user.accessToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
                           val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                           intent.putExtra("user", newUser)
                           intent.putExtra("token", Token(access = access, refresh = refresh))
                           startActivity(intent)
                        }else {
                           Log.e(TAG, "getUser: $getUser")
                        }
                     }else {
                        Log.e(TAG, "response: $response")
                     }
                  }
               }

               override fun onError(errorCode: Int, message: String) {
                  Log.e(TAG, "naverLogin err1: $message")
               }

               override fun onFailure(httpStatus: Int, message: String) {
                  Log.e(TAG, "naverLogin err2: $message")
               }
            })
         }

         override fun onError(errorCode: Int, message: String) {
            Log.e(TAG, "naverLogin err3: $message")
         }

         override fun onFailure(httpStatus: Int, message: String) {
            Log.e(TAG, "naverLogin err4: $message")
         }
      }

      // SDK 객체 초기화
      NaverIdLoginSDK.initialize(this, resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))
      NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
   }

   private fun kakaoLogin() {
      val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if(error != null) Log.e(TAG, "kakaoLogin err1: $error") else if (token != null) createKakaoUser(token)
      }

      // 카카오톡이 설치되어있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if(error != null) {
               Log.e(TAG, "kakaoLogin err2: $error")
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
         if(error == null) {
            lifecycleScope.launch {
               val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(token.accessToken, token.idToken!!))
               if(response.isSuccessful) {
                  val access = response.body()!!.accessToken
                  val refresh = response.body()!!.refreshToken

                  val getUser = RetrofitAPI.api.getUser("Bearer $access")
                  if(getUser.isSuccessful) {
                     val newUser = User(type = KAKAO, idToken = token.idToken!!, accessToken = token.accessToken, email = user!!.kakaoAccount?.email!!,
                        username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
                     val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                     intent.putExtra("user", newUser)
                     intent.putExtra("token", Token(access = access, refresh = refresh))
                     startActivity(intent)
                  }else {
                     Log.e(TAG, "getUser: $getUser")
                  }
               }else {
                  Log.e(TAG, "response: $response")
               }
            }
         }else {
            Log.e(TAG, "UserApiClient: $error")
         }
      }
   }

   private fun setStatusBar() {
      this.window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK
         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }
   }

   override fun onDestroy() {
      super.onDestroy()
      binding.webView.destroy()
   }
}