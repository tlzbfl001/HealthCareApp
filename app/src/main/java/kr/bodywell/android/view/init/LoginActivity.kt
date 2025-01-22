package kr.bodywell.android.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.api.dto.KakaoLoginDTO
import kr.bodywell.android.api.dto.LoginDTO
import kr.bodywell.android.api.dto.NaverLoginDTO
import kr.bodywell.android.databinding.ActivityLoginBinding
import kr.bodywell.android.model.Constant.GOOGLE
import kr.bodywell.android.model.Constant.KAKAO
import kr.bodywell.android.model.Constant.NAVER
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.MyApp

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      setStatusBar()

      MyApp.prefs.removePrefs()

      binding.tv1.setOnClickListener {
         startActivity(Intent(this, SignupActivity::class.java))
      }

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
      if(requestCode == 1000) {
         GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener {
            try{
               if(it.result.idToken != "" && it.result.idToken != null && it.result.email != "" && it.result.email != null) {
                  val user = User(type = GOOGLE, email = it.result.email!!, idToken = it.result.idToken!!)

                  lifecycleScope.launch {
                     val loginWithGoogle = RetrofitAPI.api.loginWithGoogle(LoginDTO(user.idToken))
                     if(loginWithGoogle.isSuccessful) {
                        val access = loginWithGoogle.body()!!.accessToken
                        val refresh = loginWithGoogle.body()!!.refreshToken

                        val getUser = RetrofitAPI.api.getUser("Bearer $access")
                        if(getUser.isSuccessful) {
                           val newUser = User(type = user.type, idToken = user.idToken, email = user.email, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
                           val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                           intent.putExtra("user", newUser)
                           intent.putExtra("token", Token(access = access, refresh = refresh))
                           startActivity(intent)
                        }else {
                           Log.e(TAG, "getUser: $getUser")
                        }
                     }else {
                        Log.e(TAG, "loginWithGoogle: $loginWithGoogle")
                     }
                  }
               }else {
                  Log.e(TAG, "GoogleSignIn: result.idToken = null")
               }
            }catch (e: Exception) {
               Log.e(TAG, "GoogleSignIn: $e")
            }
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  if(NaverIdLoginSDK.getAccessToken() != "" && NaverIdLoginSDK.getAccessToken() != null && result.profile?.email != "" && result.profile?.email != null) {
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
                  }else {
                     Log.e(TAG, "NidOAuthLogin: NaverIdLoginSDK.getAccessToken() = null")
                  }
               }

               override fun onError(errorCode: Int, message: String) {
                  Log.e(TAG, "NidOAuthLogin onError1: $message")
               }

               override fun onFailure(httpStatus: Int, message: String) {
                  Log.e(TAG, "NidOAuthLogin onFailure1: $message")
               }
            })
         }

         override fun onError(errorCode: Int, message: String) {
            Log.e(TAG, "NidOAuthLogin onError2: $message")
         }

         override fun onFailure(httpStatus: Int, message: String) {
            Log.e(TAG, "NidOAuthLogin onFailure2: $message")
         }
      }

      // SDK 객체 초기화
      NaverIdLoginSDK.initialize(this, resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))
      NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
   }

   private fun kakaoLogin() {
      val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if(error != null) {
            Log.e(TAG, "kakaoLogin1: $error")
         }else if (token != null) createKakaoUser(token)
      }

      // 카카오톡이 설치되어있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if(error != null) {
               Log.e(TAG, "kakaoLogin2: $error")
               if(error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                  return@loginWithKakaoTalk
               }else UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }else if(token != null) createKakaoUser(token)
         }
      }else UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
   }

   private fun createKakaoUser(token: OAuthToken) {
      UserApiClient.instance.me { user, error ->
         if(error == null) {
            if(token.idToken != "" && token.idToken != null && user!!.kakaoAccount?.email != "" && user.kakaoAccount?.email != null) {
               lifecycleScope.launch {
                  val response = RetrofitAPI.api.loginWithKakao(KakaoLoginDTO(token.accessToken, token.idToken!!))
                  if(response.isSuccessful) {
                     val access = response.body()!!.accessToken
                     val refresh = response.body()!!.refreshToken

                     val getUser = RetrofitAPI.api.getUser("Bearer $access")
                     if(getUser.isSuccessful) {
                        val newUser = User(type = KAKAO, idToken = token.idToken!!, accessToken = token.accessToken, email = user.kakaoAccount?.email!!, username = getUser.body()!!.username, role = getUser.body()!!.role, uid = getUser.body()!!.id)
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
               Log.e(TAG, "UserApiClient: token.idToken = null")
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