package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var apolloClient: ApolloClient? = null
   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var gsa: GoogleSignInAccount? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      dataManager = DataManager(this)
      dataManager!!.open()

      apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         googleLogin()
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         naverLogin()
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         kakaoLogin()
      }
   }

   private fun googleLogin() {
      gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(getString(R.string.googleWebClientId))
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
         val task = GoogleSignIn.getSignedInAccountFromIntent(data)
         try {
            task.getResult(ApiException::class.java)
            gsa = GoogleSignIn.getLastSignedInAccount(this)

            val getUser = dataManager!!.getUser("google", gsa?.email.toString())

            if(getUser.regDate == "") { // 초기 가입 작업
               val user = User(type = "google", idToken = gsa?.idToken, email = gsa?.email, name = gsa?.displayName, regDate = LocalDate.now().toString())
               val intent = Intent(this, InputActivity::class.java)
               intent.putExtra("user", user)
               startActivity(intent)
            }else { // 로그인
               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                     idToken = gsa?.idToken.toString()
                  ))).execute()

                  if(response.data!!.loginUserGoogle.success) {
                     val getUser = dataManager!!.getUser("google", gsa?.email.toString())
                     dataManager!!.updateToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserGoogle.accessToken.toString(),
                        refreshToken = response.data!!.loginUserGoogle.refreshToken.toString(), regDate = LocalDate.now().toString()))

                     startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                  }else {
                     Toast.makeText(this@LoginActivity, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                  }
               }
            }
         }catch (e: ApiException) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val getUser = dataManager!!.getUser("naver", result.profile?.email.toString())

                  if(getUser.regDate == "") { // 초기 가입 작업
                     val user = User(type = "naver", idToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email, name = result.profile?.name,
                        nickname = result.profile?.nickname, gender = result.profile?.gender, birthday = result.profile?.birthYear + "-" + result.profile?.birthday,
                        profileImage = result.profile?.profileImage, regDate = LocalDate.now().toString())

                     val intent = Intent(this@LoginActivity, InputActivity::class.java)
                     intent.putExtra("user", user)
                     startActivity(intent)
                  }else { // 로그인
                     lifecycleScope.launch{
                        val response = apolloClient!!.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
                           accessToken = NaverIdLoginSDK.getAccessToken().toString()
                        ))).execute()

                        if(response.data!!.loginUserNaver.success) {
                           val getUser = dataManager!!.getUser("naver", result.profile?.email.toString())
                           dataManager!!.updateToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserNaver.accessToken.toString(),
                              refreshToken = response.data!!.loginUserNaver.refreshToken.toString(), regDate = LocalDate.now().toString()))

                           startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        }else {
                           Toast.makeText(this@LoginActivity, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                        }
                     }
                  }
               }
               override fun onError(errorCode: Int, message: String) {
                  Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
               }
               override fun onFailure(httpStatus: Int, message: String) {
                  Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
               }
            })
         }
         override fun onError(errorCode: Int, message: String) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
         }
         override fun onFailure(httpStatus: Int, message: String) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
         }
      }

      // SDK 객체 초기화
      NaverIdLoginSDK.initialize(this, getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(R.string.app_name))
      NaverIdLoginSDK.authenticate(this, oAuthLoginCallback)
   }

   private fun kakaoLogin() {
      val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if (error != null) {
            when {
               error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                  Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                  Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                  Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없습니다.", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                  Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                  Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                  Toast.makeText(this, "설정(android key hash)이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.ServerError.toString() -> {
                  Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
               }
               error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                  Toast.makeText(this, "앱이 요청 권한이 없습니다.", Toast.LENGTH_SHORT).show()
               }
               else -> { // Unknown
                  Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()
               }
            }
         }else if (token != null) { // 로그인 성공
            kakaoApollo(token)
         }
      }

      // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error -> // 카카오톡으로 로그인
            if(error != null) { // 로그인 실패 부분
               Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()

               // 사용자가 카카오톡 설치 후 디바이스 권한요청 화면에서 로그인을 취소한 경우, 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리
               if(error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                  return@loginWithKakaoTalk
               }else {
                  // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                  UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
               }
            }else if(token != null) { // 로그인 성공
               kakaoApollo(token)
            }
         }
      }else { // 카카오계정으로 로그인
         UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
      }
   }

   private fun kakaoApollo(token: OAuthToken) {
      UserApiClient.instance.me { user, error ->
         val getUser = dataManager!!.getUser("kakao", user?.kakaoAccount?.email.toString()) // 사용자 가입여부 체크

         if(getUser.regDate == "") { // 초기 가입 작업
            val user = User(type = "kakao", idToken = token.idToken, email = user?.kakaoAccount?.email, name = user?.kakaoAccount?.name,
               nickname = user?.kakaoAccount?.profile?.nickname, profileImage = user?.kakaoAccount?.profile?.profileImageUrl, regDate = LocalDate.now().toString())

            val intent = Intent(this, InputActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
         }else { // 로그인
            lifecycleScope.launch{
               val response = apolloClient!!.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
                  idToken = token.idToken.toString()
               ))).execute()

               if(response.data!!.loginUserKakao.success) {
                  val getUser = dataManager!!.getUser("kakao", user?.kakaoAccount?.email.toString())
                  dataManager!!.updateToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserKakao.accessToken.toString(),
                     refreshToken = response.data!!.loginUserKakao.refreshToken.toString(), regDate = LocalDate.now().toString()))

                  startActivity(Intent(this@LoginActivity, MainActivity::class.java))
               }else {
                  Toast.makeText(this@LoginActivity, "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
               }
            }
         }
      }
   }

   private fun getKeyHash() {
      val keyHash = Utility.getKeyHash(applicationContext)
      Log.d(TAG, keyHash)
   }
}