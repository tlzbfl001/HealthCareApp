package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
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
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.service.UserResponse
import com.makebodywell.bodywell.service.ApiObject
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager

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
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            binding.cl.visibility = View.GONE
            binding.webView.visibility = View.VISIBLE

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.userAgentString = "app_running_android"
            binding.webView.settings.saveFormData = false

            binding.webView.webChromeClient = object : WebChromeClient() {
               override fun onConsoleMessage(cmsg: ConsoleMessage): Boolean {
                  if(cmsg.message().contains("accessToken") && cmsg.message().contains("refreshToken")) {
                     binding.webView.visibility = View.GONE
                     val obj = JSONObject(cmsg.message())
                     val access = obj.get("accessToken").toString()
                     val refresh = obj.get("refreshToken").toString()
                     val uid = decodeToken(access)
                     val callGetUser = ApiObject.api.getUser(uid)

                     callGetUser.enqueue(object : Callback<UserResponse> {
                        override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                           if(response.isSuccessful) {
                              val getUser = dataManager.getUser("google", response.body()!!.email)

                              if(getUser.regDate == "") {
                                 val username = if(response.body()!!.username == null) "" else response.body()!!.username
                                 val user = UserResponse(uid = response.body()!!.uid, type = "google", email = response.body()!!.email, username = username)
                                 val token = Token(accessToken = access, refreshToken = refresh)

                                 val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                                 intent.putExtra("user", user)
                                 intent.putExtra("token", token)
                                 startActivity(intent)
                              }else {
                                 MyApp.prefs.setPrefs("userId", getUser.id)
                                 val getToken = dataManager.getToken(getUser.id)

                                 if(getToken.userId > 0) {
                                    dataManager.updateToken(Token(accessToken = access, refreshToken = refresh, accessTokenRegDate = LocalDateTime.now().toString(),
                                       refreshTokenRegDate = LocalDateTime.now().toString()))
                                 }else {
                                    dataManager.insertToken(Token(accessToken = access, refreshToken = refresh, accessTokenRegDate = LocalDateTime.now().toString(),
                                       refreshTokenRegDate = LocalDateTime.now().toString()))
                                 }

                                 startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                              }
                           }else {
                              binding.cl.visibility = View.VISIBLE
                              Toast.makeText(this@LoginActivity, "로그인 오류", Toast.LENGTH_SHORT).show()
                           }
                        }

                        override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                           binding.cl.visibility = View.VISIBLE
                           Toast.makeText(this@LoginActivity, "로그인 오류", Toast.LENGTH_SHORT).show()
                        }
                     })
                     binding.webView.destroy()
                  }

                  return super.onConsoleMessage(cmsg)
               }
            }

            binding.webView.webViewClient = object : WebViewClient() {
               override fun onPageFinished(view: WebView, url: String) {
                  super.onPageFinished(view, url)
                  view.loadUrl("javascript:console.log(document.body.getElementsByTagName('pre')[0].innerHTML);")
               }
            }

            binding.webView.loadUrl("https://api.bodywell.dev/auth/google")
         }
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {

         }
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         if(!networkStatusCheck(this)){
            Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {

         }
      }

//      Log.d(TAG, "getKeyHash: " + Utility.getKeyHash(this))
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