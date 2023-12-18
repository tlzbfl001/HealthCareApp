package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.CreateUserKakaoMutation
import com.makebodywell.bodywell.CreateUserNaverMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
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
import java.net.URISyntaxException
import java.time.LocalDate
import java.util.UUID
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var backWait:Long = 0

   private var dataManager: DataManager? = null

   private var apolloClient: ApolloClient? = null

   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var gsa: GoogleSignInAccount? = null

   private lateinit var webView: WebView
   private val authEndpoint = "https://appleid.apple.com/auth/authorize"
   private val responseType = "code%20id_token"
   private val responseMode = "form_post"
   private var clientId = ""
   private val scope = "name%20email"
   private val state = UUID.randomUUID().toString()
   private val redirectUrl = "https://domainone.store/redirect"

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      dataManager = DataManager(this)
      dataManager!!.open()

      initView()
   }

   private fun initView() {
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
         gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.googleWebClientId))
            .requestEmail()
            .build()
         gsc = GoogleSignIn.getClient(this, gso!!)

         val signInIntent = gsc!!.signInIntent
         startActivityForResult(signInIntent, 1000)
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
         // 사용자 가입여부 체크
         val getUser = dataManager!!.getUser("kakao", user?.kakaoAccount?.email.toString())

         if(getUser.regDate == "") { // 회원 가입
            dataManager!!.insertUser(User(type = "kakao", idToken = token.idToken, email = user?.kakaoAccount?.email, name = user?.kakaoAccount?.name,
               nickname = user?.kakaoAccount?.profile?.nickname, profileImage = user?.kakaoAccount?.profile?.profileImageUrl, regDate = LocalDate.now().toString()))

            // 서버에 사용자 정보 저장
            lifecycleScope.launch {
               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
                     idToken = token.idToken.toString()
                  ))).execute()
                  Log.d(TAG, "createUserKakao: ${response.data?.createUserKakao}")
               }
            }

            startActivity(Intent(this@LoginActivity, InputActivity::class.java))
         }else { // 로그인
            lifecycleScope.launch {
               lifecycleScope.launch{
                  val response = apolloClient!!.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
                     idToken = token.idToken.toString()
                  ))).execute()
                  Log.d(TAG, "loginUserKakao: ${response.data?.loginUserKakao}")
               }
            }

            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
         }
      }
   }

   // 네이버 로그인 처리
   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val getUser = dataManager!!.getUser("naver", result.profile?.email.toString())

                  if(getUser.regDate == "") {
                     dataManager!!.insertUser(User(type = "naver", accessToken = NaverIdLoginSDK.getAccessToken(), email = result.profile?.email, name = result.profile?.name,
                        nickname = result.profile?.nickname, gender = result.profile?.gender, birthYear = result.profile?.birthYear, birthDay = result.profile?.birthday,
                        profileImage = result.profile?.profileImage, regDate = LocalDate.now().toString()))

                     lifecycleScope.launch {
                        lifecycleScope.launch{
                           val response = apolloClient!!.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
                              accessToken = NaverIdLoginSDK.getAccessToken().toString()
                           ))).execute()
                           Log.d(TAG, "createUserNaver: ${response.data?.createUserNaver}")
                        }
                     }

                     startActivity(Intent(this@LoginActivity, InputActivity::class.java))
                  }else {
                     lifecycleScope.launch {
                        lifecycleScope.launch{
                           val response = apolloClient!!.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
                              accessToken = NaverIdLoginSDK.getAccessToken().toString()
                           ))).execute()
                           Log.d(TAG, "loginUserNaver: ${response.data?.loginUserNaver}")
                        }
                     }

                     startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                  }
               }
               override fun onError(errorCode: Int, message: String) {
                  Log.e(TAG, message)
               }
               override fun onFailure(httpStatus: Int, message: String) {
                  Log.e(TAG, message)
               }
            })
         }
         override fun onError(errorCode: Int, message: String) {
            Log.e(TAG, message)
         }
         override fun onFailure(httpStatus: Int, message: String) {
            Log.e(TAG, message)
         }
      }

      // SDK 객체 초기화
      NaverIdLoginSDK.initialize(this@LoginActivity, getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(R.string.app_name))
      NaverIdLoginSDK.authenticate(this@LoginActivity, oAuthLoginCallback)
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

               lifecycleScope.launch {
                  lifecycleScope.launch{
                     val response = apolloClient!!.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                        idToken = gsa?.idToken.toString()
                     ))).execute()
                     Log.d(TAG, "CreateUserGoogle: ${response.data?.createUserGoogle}")
                  }
               }

               startActivity(Intent(this@LoginActivity, InputActivity::class.java))
            }else { // 로그인
               lifecycleScope.launch {
                  lifecycleScope.launch{
                     val response = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
                        idToken = gsa?.idToken.toString()
                     ))).execute()
                     Log.d(TAG, "LoginUserGoogle: ${response.data?.loginUserGoogle}")
                  }
               }

               startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
         }catch (e: ApiException) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
         }
      }
   }

   private fun appleLogin() {
      webView = WebView(this)

      // 웹뷰 세팅
      webView.apply {
         settings.javaScriptEnabled = true
         settings.allowFileAccessFromFileURLs = true
         settings.allowUniversalAccessFromFileURLs = true
      }

      // 웹뷰 동작
      webView.loadUrl(createUrl())

      webView.webChromeClient = object : WebChromeClient() {
         override fun onConsoleMessage(cmsg: ConsoleMessage): Boolean {
            Log.d(TAG, cmsg.message())

            return true
         }
      }

      webView.webViewClient = object : WebViewClient() {
         override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            Log.e(TAG, description)
         }

         override fun onPageFinished(view: WebView, url: String) {
            view.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);")
         }
      }

      setContentView(webView)
   }

   private fun createUrl(): String{
      clientId = getString(R.string.appleServiceId)
      return (authEndpoint
              + "?response_type=$responseType"
              + "&response_mode=$responseMode"
              + "&client_id=$clientId"
              + "&scope=$scope"
              + "&state=$state"
              + "&redirect_uri=$redirectUrl")
   }

   private fun getKeyHash() {
      val keyHash = Utility.getKeyHash(applicationContext)
      Log.d(TAG, keyHash)
   }

   override fun onBackPressed() {
      if(System.currentTimeMillis() - backWait >=2000 ) {
         backWait = System.currentTimeMillis()
         Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
      } else {
         ActivityCompat.finishAffinity(this)
         System.runFinalization()
         exitProcess(0)
      }
   }
}