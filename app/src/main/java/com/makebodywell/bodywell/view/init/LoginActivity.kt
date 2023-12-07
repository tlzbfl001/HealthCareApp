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
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.ActivityLoginBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import java.io.UnsupportedEncodingException
import java.util.UUID

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private var googleSignInClient: GoogleSignInClient? = null
   private var googleSignInOptions: GoogleSignInOptions? = null
   private var googleSignInAccount: GoogleSignInAccount? = null

   private lateinit var webView: WebView
   private val authEndpoint = "https://appleid.apple.com/auth/authorize"
   private val responseType = "code%20id_token"
   private val responseMode = "form_post"
   private lateinit var clientId: String
   private val scope = "name%20email"
   private val state = UUID.randomUUID().toString()
   private val redirectUrl = "https://api.bodywell.dev/auth/apple/redirect"

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      _binding = ActivityLoginBinding.inflate(layoutInflater)
      setContentView(binding.root)

      /*val keyHash = Utility.getKeyHash(this)
      Log.d(TAG, keyHash)*/

      // 카카오 로그인
      val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
         if(error != null) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "$error")
         }else if(token != null) {
            // 화면 이동
            val intent = Intent(this@LoginActivity, InputActivity::class.java)
            intent.putExtra("kakaoIdToken", token.idToken)
            startActivity(intent)
         }
      }

      binding.clKakao.setOnClickListener {
         if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
         } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
         }
      }

      // 구글 로그인
      googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(getString(R.string.googleWebClientId))
         .requestEmail()
         .build()
      googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions!!)

      binding.clGoogle.setOnClickListener {
         val signInIntent = googleSignInClient!!.signInIntent
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

   // 구글로그인 처리
   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      if (requestCode == 1000) {
         val task = GoogleSignIn.getSignedInAccountFromIntent(data)
         try {
            task.getResult(ApiException::class.java)
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

            val googleName = googleSignInAccount!!.displayName
            val googleEmail = googleSignInAccount!!.email
            val googleIdToken = googleSignInAccount!!.idToken
            val intent = Intent(this@LoginActivity, InputActivity::class.java)

            intent.putExtra("googleType", "google")
            intent.putExtra("googleName", googleName)
            intent.putExtra("googleEmail", googleEmail)
            intent.putExtra("googleIdToken", googleIdToken)

            startActivity(intent)
         } catch (e: ApiException) {
            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
         }
      }
   }

   // 네이버로그인 처리
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
}