package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.util.UUID

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

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
   private val redirectUrl = "https://api.bodywell.dev/auth/apple/redirect"

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      /*val keyHash = Utility.getKeyHash(this)
      Log.d(TAG, keyHash)*/

      dataManager = DataManager(this)
      dataManager!!.open()

      initView()
   }

   private fun initView() {
      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         kakaoLogin()
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

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         naverLogin()
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
            Log.e(TAG, "로그인 실패 $error")
         }else if (token != null) { // 로그인 성공
            kakaoApollo(token)
         }
      }

      // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
      if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
         UserApiClient.instance.loginWithKakaoTalk(this) { token, error -> // 카카오톡으로 로그인
            if(error != null) { // 로그인 실패 부분
               Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
               Log.e(TAG, "로그인 실패 $error")

               // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우, 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리
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
      }else {
         UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오계정으로 로그인
      }
   }

   private fun kakaoApollo(token: OAuthToken) {
      UserApiClient.instance.me { user, error ->
         Log.d(TAG, "idtoken: ${token.idToken}")
         Log.d(TAG, "kakaoAccount: ${user?.kakaoAccount}")
         
         dataManager!!.insertUser(User(type = "kakao", idToken = token.idToken, email = user?.kakaoAccount?.email, name = user?.kakaoAccount?.name,
            nickname = user?.kakaoAccount?.profile?.nickname, profileImage = user?.kakaoAccount?.profile?.profileImageUrl))

         // 서버에 사용자 정보 저장
         lifecycleScope.launch {
            lifecycleScope.launch{
               val response = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                  idToken = gsa?.idToken.toString()
               ))).execute()
               Log.d(TAG, "CreateUserGoogle: ${response.data?.createUserGoogle}")
            }
         }
      }
      startActivity(Intent(this@LoginActivity, InputActivity::class.java))
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

            if(getUser.id == 0) { // 회원 가입
               // 로컬 DB 에 사용자 정보 저장
               dataManager!!.insertUser(User(type = "google", idToken = gsa?.idToken, email = gsa?.email, name = gsa?.displayName))

               // 서버에 사용자 정보 저장
               lifecycleScope.launch {
                  lifecycleScope.launch{
                     val response = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
                        idToken = gsa?.idToken.toString()
                     ))).execute()
                     Log.d(TAG, "CreateUserGoogle: ${response.data?.createUserGoogle}")
                  }
               }
               startActivity(Intent(this@LoginActivity, InputActivity::class.java))
            }else { // 로그인
               lifecycleScope.launch {
                  lifecycleScope.launch{
                     val response = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
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

   // 네이버 로그인 처리
   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val intent = Intent(this@LoginActivity, InputActivity::class.java)
                  intent.putExtra("naverAccessToken", NaverIdLoginSDK.getAccessToken())
                  intent.putExtra("naverEmail", result.profile?.email)
                  intent.putExtra("naverName", result.profile?.name)
                  intent.putExtra("naverNickname", result.profile?.nickname)
                  intent.putExtra("naverGender", result.profile?.gender)
                  intent.putExtra("naverBirthYear", result.profile?.birthYear)
                  intent.putExtra("naverBirthDay", result.profile?.birthday)
                  intent.putExtra("naverProfileImage", result.profile?.profileImage)
                  startActivity(intent)
               }
               override fun onError(errorCode: Int, message: String) {
                  Log.e(TAG, "$errorCode")
               }
               override fun onFailure(httpStatus: Int, message: String) {
                  Log.e(TAG, "$message")
               }
            })
         }
         override fun onError(errorCode: Int, message: String) {
            Log.e(TAG, "$message")
         }
         override fun onFailure(httpStatus: Int, message: String) {
            Log.e(TAG, "$message")
         }
      }
      NaverIdLoginSDK.initialize(this@LoginActivity, getString(R.string.naverClientId), getString(R.string.naverClientSecret), "Bodywell")
      NaverIdLoginSDK.authenticate(this@LoginActivity, oAuthLoginCallback)
   }

   // 애플 로그인 처리
   private fun appleLogin() {
      webView = WebView(this)

      // 웹뷰 세팅
      val webViewSettings = webView.settings
      webViewSettings.javaScriptEnabled = true

      webView.webChromeClient = object : WebChromeClient() {
         override fun onConsoleMessage(cmsg: ConsoleMessage): Boolean {
            Log.d(TAG, cmsg.message())
            return true
         }
      }

      webView.webViewClient = object : WebViewClient() {
         override fun onReceivedError(
            view: WebView, errorCode: Int,
            description: String, failingUrl: String
         ) {
            Log.e(TAG, description)
         }

         override fun onPageFinished(view: WebView, url: String) {
            view.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);")
         }
      }

      // 웹뷰 동작
      val url = createUrl()
      webView.loadUrl(url)

      setContentView(webView)
   }

   private fun decodeJWT(JWT: String): String {
      var decodedJson = ""
      try {
         val split = JWT.split("\\.".toRegex()).toTypedArray()

         // header 디코딩
         val decodedHeader = Base64.decode(split[0], Base64.URL_SAFE)
         Log.d(TAG, "header: ${String(decodedHeader, charset("UTF-8"))}")

         // body 디코딩
         val decodedBody = Base64.decode(split[1], Base64.URL_SAFE)
         decodedJson = String(decodedBody, charset("UTF-8"))
         Log.d(TAG, "body: $decodedJson")
      }catch (e: UnsupportedEncodingException){
         e.printStackTrace()
      }
      return decodedJson
   }

   // 요청 URL에 파라미터 붙이기
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

   companion object {
      val apolloClient = ApolloClient.Builder()
         .serverUrl("https://api.bodywell.dev/graphql")
         .build()
   }
}