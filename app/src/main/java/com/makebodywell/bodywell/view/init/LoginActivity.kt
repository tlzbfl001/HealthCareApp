package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.CreateUserAppleMutation
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.CreateUserKakaoMutation
import com.makebodywell.bodywell.CreateUserNaverMutation
import com.makebodywell.bodywell.LoginUserAppleMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.model.Body
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateAppleOauthInput
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
import com.makebodywell.bodywell.type.LoginAppleOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.view.home.body.BodyRecordFragment
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

   private var backWait:Long = 0

   private val bundle = Bundle()
   private var dataManager: DataManager? = null

   private var apolloClient: ApolloClient? = null

   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var gsa: GoogleSignInAccount? = null

   private lateinit var oauthProvider: OAuthProvider.Builder
   private lateinit var firebaseAuth: FirebaseAuth

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      dataManager = DataManager(this)
      dataManager!!.open()

      apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         kakaoLogin()
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         naverLogin()
      }

      // 구글 로그인
      binding.clGoogle.setOnClickListener {
         googleLogin()
      }

      // 애플 로그인
      binding.clApple.setOnClickListener {
         appleLogin()
      }
   }

   private fun kakaoLogin() {
      val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if(error != null) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
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
         if(getUser.regDate == "") { // 회원 가입
            dataManager!!.insertUser(User(type = "kakao", idToken = token.idToken, email = user?.kakaoAccount?.email, name = user?.kakaoAccount?.name,
               nickname = user?.kakaoAccount?.profile?.nickname, profileImage = user?.kakaoAccount?.profile?.profileImageUrl, regDate = LocalDate.now().toString()))

            // 서버에 사용자 정보 저장
            lifecycleScope.launch{
               val response = apolloClient!!.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
                  idToken = token.idToken.toString()
               ))).execute()
            }

            startActivity(Intent(this, InputActivity::class.java))
         }else { // 로그인
            lifecycleScope.launch{
               val response = apolloClient!!.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
                  idToken = token.idToken.toString()
               ))).execute()
            }

            startActivity(Intent(this, MainActivity::class.java))
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val getUser = dataManager!!.getUser("naver", result.profile?.email.toString())
                  if(getUser.regDate == "") {
                     dataManager!!.insertUser(User(type = "naver", email = result.profile?.email, name = result.profile?.name, nickname = result.profile?.nickname,
                        gender = result.profile?.gender, birthDay = result.profile?.birthYear + "-" + result.profile?.birthday,
                        profileImage = result.profile?.profileImage, regDate = LocalDate.now().toString()))

                     lifecycleScope.launch{
                        val response = apolloClient!!.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
                           accessToken = NaverIdLoginSDK.getAccessToken().toString()
                        ))).execute()
                     }

                     startActivity(Intent(this@LoginActivity, InputActivity::class.java))
                  }else {
                     lifecycleScope.launch{
                        val response = apolloClient!!.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
                           accessToken = NaverIdLoginSDK.getAccessToken().toString()
                        ))).execute()
                     }

                     startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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
            if(getUser.regDate == "") {
               dataManager!!.insertUser(User(type = "google", idToken = gsa?.idToken, email = gsa?.email, name = gsa?.displayName, regDate = LocalDate.now().toString()))

               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                     idToken = gsa?.idToken.toString()
                  ))).execute()
               }

               var loginSuccess = false
               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                     idToken = gsa?.idToken.toString()
                  ))).execute()

                  loginSuccess = if(response.data!!.loginUserGoogle.success) {
                     val getUser = dataManager!!.getUser()
                     dataManager!!.insertToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserGoogle.accessToken.toString(),
                        refreshToken = response.data!!.loginUserGoogle.refreshToken.toString(), regDate = LocalDate.now().toString()))
                     true
                  }else {
                     false
                  }
               }

               if(loginSuccess) {
                  startActivity(Intent(this, InputActivity::class.java))
               }else {
                  Toast.makeText(this, "서버에 이상이 있습니다.", Toast.LENGTH_SHORT).show()
               }
            }else { // 로그인
               var loginSuccess = false
               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                     idToken = gsa?.idToken.toString()
                  ))).execute()

                  loginSuccess = if(response.data!!.loginUserGoogle.success) {
                     val getUser = dataManager!!.getUser()
                     dataManager!!.updateToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserGoogle.accessToken.toString(),
                        refreshToken = response.data!!.loginUserGoogle.refreshToken.toString(), regDate = LocalDate.now().toString()))
                     true
                  }else {
                     false
                  }
               }

               if(loginSuccess) {
                  startActivity(Intent(this, InputActivity::class.java))
               }else {
                  Toast.makeText(this, "서버에 이상이 있습니다.", Toast.LENGTH_SHORT).show()
               }

               startActivity(Intent(this, MainActivity::class.java))
            }
         }catch (e: ApiException) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
         }
      }
   }

   private fun appleLogin() {
      // 인증 API 초기화
      oauthProvider = OAuthProvider.newBuilder("apple.com")
      oauthProvider.scopes = listOf("email", "name")
      oauthProvider.addCustomParameter("locale", "ko")
      firebaseAuth = FirebaseAuth.getInstance()

      // 이미 받은 응답이 있는지 확인
      val pending = firebaseAuth.pendingAuthResult
      if(pending != null) {
         pending.addOnSuccessListener { authResult ->
            appleApollo(authResult)
         }.addOnFailureListener { e ->
            e.printStackTrace()
         }
      }else {
         firebaseAuth.startActivityForSignInWithProvider(this, oauthProvider.build()).addOnSuccessListener { authResult ->
            appleApollo(authResult)
         }.addOnFailureListener { e ->
            e.printStackTrace()
         }
      }
   }

   private fun appleApollo(authResult: AuthResult) {
      val idToken = (authResult.credential as OAuthCredential?)!!.idToken.toString()
      val getUser = dataManager!!.getUser("apple", authResult.user?.email.toString())
      if(getUser.regDate == "") {
         dataManager!!.insertUser(User(type = "apple", idToken = idToken,
            email = authResult.user?.email.toString(), regDate = LocalDate.now().toString()))

         lifecycleScope.launch{
            val response = apolloClient!!.mutation(CreateUserAppleMutation(CreateAppleOauthInput(
               idToken = idToken
            ))).execute()
         }

         startActivity(Intent(this, InputActivity::class.java))
      }else { // 로그인
         lifecycleScope.launch{
            val response = apolloClient!!.mutation(LoginUserAppleMutation(LoginAppleOauthInput(
               idToken = idToken
            ))).execute()
         }

         startActivity(Intent(this, MainActivity::class.java))
      }
   }

   private fun getKeyHash() {
      val keyHash = Utility.getKeyHash(applicationContext)
      Log.d(TAG, keyHash)
   }

   override fun onBackPressed() {
      if(System.currentTimeMillis() - backWait >= 2000 ) {
         backWait = System.currentTimeMillis()
         Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
      } else {
         ActivityCompat.finishAffinity(this)
         System.runFinalization()
         exitProcess(0)
      }
   }
}