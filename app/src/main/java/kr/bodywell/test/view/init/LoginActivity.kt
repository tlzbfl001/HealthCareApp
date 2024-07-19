package kr.bodywell.test.view.init

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import kr.bodywell.test.BuildConfig.GOOGLE_WEB_CLIENT_ID
import kr.bodywell.test.BuildConfig.NAVER_CLIENT_ID
import kr.bodywell.test.BuildConfig.NAVER_CLIENT_SECRET
import kr.bodywell.test.R
import kr.bodywell.test.api.RetrofitAPI
import kr.bodywell.test.api.dto.LoginDTO
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.ActivityLoginBinding
import kr.bodywell.test.model.Body
import kr.bodywell.test.model.Exercise
import kr.bodywell.test.model.Food
import kr.bodywell.test.model.Image
import kr.bodywell.test.model.Sleep
import kr.bodywell.test.model.Token
import kr.bodywell.test.model.User
import kr.bodywell.test.model.Water
import kr.bodywell.test.util.CustomUtil.Companion.TAG
import kr.bodywell.test.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.test.util.MyApp
import kr.bodywell.test.view.home.MainActivity
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginActivity : AppCompatActivity() {
   private var _binding: ActivityLoginBinding? = null
   private val binding get() = _binding!!

   private lateinit var dataManager: DataManager
   private var gsc: GoogleSignInClient? = null
   private var gso: GoogleSignInOptions? = null
   private var user = User()
   private var access = ""
   private var refresh = ""
   private var userUid = ""
   private var check = false

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
         if(networkStatusCheck(this)) googleLogin() else Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
      }

      // 네이버 로그인
      binding.clNaver.setOnClickListener {
         if(networkStatusCheck(this)) naverLogin() else Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
      }

      // 카카오 로그인
      binding.clKakao.setOnClickListener {
         if(networkStatusCheck(this)) kakaoLogin() else Toast.makeText(this, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
      }

      // Log.d(TAG, "getKeyHash: " + Utility.getKeyHash(this))
   }

   private fun googleLogin() {
      gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
         .requestIdToken(GOOGLE_WEB_CLIENT_ID)
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

               Log.d(TAG, "idToken: ${it.result.idToken}")

               if(getUser.created == "") { // 초기 가입
                  if(it.result.idToken != "" && it.result.idToken != null && it.result.email != "" && it.result.email != null) {
                     val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                     intent.putExtra("user", User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!))
                     startActivity(intent)
                     
                     /*CoroutineScope(Dispatchers.IO).launch {
                        val getAllUser = RetrofitAPI.api.getAllUser()
                        if(getAllUser.isSuccessful) {
                           for(i in 0 until getAllUser.body()!!.size) if(getAllUser.body()!![i].email == it.result.email.toString()) check = true
                        }

                        if(check) {
                           runOnUiThread{
                              AlertDialog.Builder(this@LoginActivity, R.style.AlertDialogStyle)
                                 .setTitle("회원가입")
                                 .setMessage("이미 존재하는 회원입니다. 기존 데이터를 가져오시겠습니까?")
                                 .setPositiveButton("확인") { _, _ ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                       val loginDTO = LoginDTO(it.result.idToken!!)
                                       val loginWithGoogle = RetrofitAPI.api.loginWithGoogle(loginDTO)

                                       if(loginWithGoogle.isSuccessful) {
                                          user = User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!)
                                          userUid = decodeToken(loginWithGoogle.body()!!.accessToken)
                                          access = loginWithGoogle.body()!!.accessToken
                                          refresh = loginWithGoogle.body()!!.refreshToken
                                          registerUser()
                                       }else Log.e(TAG, "googleLogin: $loginWithGoogle")
                                    }
                                 }.setNegativeButton("취소", null).create().show()
                           }
                        }else {
                           val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                           intent.putExtra("user", User(type = "google", email = it.result.email!!, idToken = it.result.idToken!!))
                           startActivity(intent)
                        }
                     }*/
                  }else Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }else { // 로그인
                  MyApp.prefs.setPrefs("userId", getUser.id)
                  startActivity(Intent(this, MainActivity::class.java))
               }
            }else Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun naverLogin() {
      val oAuthLoginCallback = object : OAuthLoginCallback {
         override fun onSuccess() {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
               override fun onSuccess(result: NidProfileResponse) {
                  val getUser = dataManager.getUser("naver", result.profile?.email.toString())

                  if(getUser.created == "") {
                     if(NaverIdLoginSDK.getAccessToken() == "" || NaverIdLoginSDK.getAccessToken() == null || result.profile?.email == "" || result.profile?.email == null) {
                        Toast.makeText(this@LoginActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                     }else {
                        val user = User(type = "naver", idToken = NaverIdLoginSDK.getAccessToken().toString(), email = result.profile?.email!!, created = LocalDate.now().toString())

                        val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                     }
                  }else {
                     MyApp.prefs.setPrefs("userId", getUser.id)
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
      NaverIdLoginSDK.initialize(this, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, getString(R.string.app_name))
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

            if(getUser.created == "") { // 회원 가입
               if(token.idToken == "" || token.idToken == null || user.kakaoAccount?.email == "" || user.kakaoAccount?.email == null) {
                  Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
               }else {
                  val data = User(type = "kakao", idToken = token.idToken!!, email = user.kakaoAccount?.email!!, created = LocalDate.now().toString())
                  val intent = Intent(this, SignupActivity::class.java)
                  intent.putExtra("user", data)
                  startActivity(intent)
               }
            }else { // 로그인
               MyApp.prefs.setPrefs("userId", getUser.id)
               startActivity(Intent(this, MainActivity::class.java))
            }
         }
      }
   }

   private fun registerUser() {
      CoroutineScope(Dispatchers.IO).launch {
         val getDevices = RetrofitAPI.api.getAllDevice("Bearer $access")
         val getProfile = RetrofitAPI.api.getProfile("Bearer $access")

         val deviceUid = if(getDevices.isSuccessful) getDevices.body()!![0].uid else ""
         val profileUid = if(getProfile.isSuccessful) getProfile.body()!!.uid else ""

         if(deviceUid != "" && profileUid != "") {
            val getUser = dataManager.getUser(user.type, user.email)
            val getToken = dataManager.getToken()

            val gender = if(getProfile.body()!!.gender == null) "Female" else getProfile.body()!!.gender
            val birthday = if(getProfile.body()!!.birth == null) LocalDate.now().toString() else getProfile.body()!!.birth
            val height = if(getProfile.body()!!.height == null) 0.0 else getProfile.body()!!.height!!.toDouble()
            val weight = if(getProfile.body()!!.weight == null) 0.0 else getProfile.body()!!.weight!!.toDouble()

            // 사용자 정보 저장
            if(getUser.created == "") {
               dataManager.insertUser(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, profileUid = profileUid,
                  deviceUid = deviceUid, name = getProfile.body()!!.name, gender = gender, birthday = birthday, height = height,
                  weight = weight, created = getProfile.body()!!.createdAt.substring(0, 10), updated = getProfile.body()!!.updatedAt))
            }else {
               dataManager.updateUser2(User(type = user.type, email = user.email, idToken = user.idToken, userUid = userUid, profileUid = profileUid,
                  deviceUid = deviceUid, name = getProfile.body()!!.name, gender = gender, birthday = birthday, height = height,
                  weight = weight, created = getProfile.body()!!.createdAt.substring(0, 10), updated = getProfile.body()!!.updatedAt))
            }

            val getUser2 = dataManager.getUser(user.type, user.email)

            MyApp.prefs.setPrefs("userId", getUser2.id)

            // 토큰 정보 저장
            if(getToken.accessCreated == "") {
               dataManager.insertToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
            }else {
               dataManager.updateToken(Token(userId = getUser2.id, access = access, refresh = refresh, accessCreated = LocalDateTime.now().toString(), refreshCreated = LocalDateTime.now().toString()))
            }

            // 서버 데이터 저장
            val getAllFood = RetrofitAPI.api.getAllFood("Bearer $access")

            if(getAllFood.isSuccessful) {
               for(i in 0 until getAllFood.body()!!.size) {
                  var useCount = 0
                  var useDate = ""

                  if(getAllFood.body()!![i].foodUsages[0].uid != "") {
                     val getDiet = RetrofitAPI.api.getDiet("Bearer $access", getAllFood.body()!![i].foodUsages[0].uid)
                     useCount = getAllFood.body()!![i].foodUsages[0].usageCount
                     useDate = getDiet.body()!!.date
                  }

                  if(getAllFood.body()!![i].registerType == "ADMIN") {
                     dataManager.insertFood(Food(userId = getUser2.id, basic = 1, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
                        unit = getAllFood.body()!![i].quantityUnit, amount = getAllFood.body()!![i].quantity, kcal = getAllFood.body()!![i].calories,
                        protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useCount = useCount, useDate = useDate))
                  }else {
                     dataManager.insertFood(Food(userId = getUser2.id, basic = 0, uid = getAllFood.body()!![i].uid, name = getAllFood.body()!![i].foodName,
                        unit = getAllFood.body()!![i].quantityUnit, amount = getAllFood.body()!![i].quantity, kcal = getAllFood.body()!![i].calories,
                        protein = getAllFood.body()!![i].protein, fat = getAllFood.body()!![i].fat, useCount = useCount, useDate = useDate))
                  }
               }
            }

            val getAllDiet = RetrofitAPI.api.getAllDiet("Bearer $access")

            if(getAllDiet.isSuccessful) {
               for(i in 0 until getAllDiet.body()!!.size) {
                  dataManager.insertDailyFood(Food(userId = getUser2.id, uid = getAllDiet.body()!![i].uid, type = getAllDiet.body()!![i].mealTime,
                     name = getAllDiet.body()!![i].foodName, unit = getAllDiet.body()!![i].volumeUnit, amount = getAllDiet.body()!![i].volume,
                     kcal = getAllDiet.body()!![i].calories, carbohydrate = getAllDiet.body()!![i].carbohydrate, protein = getAllDiet.body()!![i].protein,
                     fat = getAllDiet.body()!![i].fat, count = getAllDiet.body()!![i].quantity, created = getAllDiet.body()!![i].date))

                  if(getAllDiet.body()!![i].photos.size > 0) {
                     for(j in 0 until getAllDiet.body()!![i].photos.size) {
                        dataManager.insertImage(Image(userId = getUser2.id, type = getAllDiet.body()!![i].mealTime, dataId = getAllDiet.body()!![i].itemId.toInt(),
                           imageUri = getAllDiet.body()!![i].photos[j], created = getAllDiet.body()!![i].date))
                     }
                  }
               }
            }

            val getAllWater = RetrofitAPI.api.getAllWater("Bearer $access")

            if(getAllWater.isSuccessful) {
               for(i in 0 until getAllWater.body()!!.size) {
                  dataManager.insertWater(Water(userId = getUser2.id, uid = getAllWater.body()!![i].uid, count = getAllWater.body()!![i].count,
                     volume = getAllWater.body()!![i].mL, created = getAllWater.body()!![i].date))
               }
            }

            val getAllActivity = RetrofitAPI.api.getAllActivity("Bearer $access")

            if(getAllActivity.isSuccessful) {
               for(i in 0 until getAllActivity.body()!!.size) {
                  var useCount = 0
                  var useDate = ""

                  if(getAllActivity.body()!![i].activityUsages[0].uid != "") {
                     val getWorkout = RetrofitAPI.api.getWorkout("Bearer $access", getAllActivity.body()!![i].activityUsages[0].uid)
                     useCount = getAllActivity.body()!![i].activityUsages[0].usageCount
                     useDate = getWorkout.body()!!.date
                  }

                  if(getAllActivity.body()!![i].registerType == "ADMIN") {
                     dataManager.insertExercise(Exercise(userId = getUser2.id, basic = 1, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name,
                        useCount = useCount, useDate = useDate))
                  }else {
                     dataManager.insertExercise(Exercise(userId = getUser2.id, basic = 0, uid = getAllActivity.body()!![i].uid, name = getAllActivity.body()!![i].name,
                        useCount = useCount, useDate = useDate))
                  }
               }
            }

            val getAllWorkout = RetrofitAPI.api.getAllWorkout("Bearer $access")

            if(getAllWorkout.isSuccessful) {
               for(i in 0 until getAllWorkout.body()!!.size) {
                  dataManager.insertDailyExercise(Exercise(uid = getAllWorkout.body()!![i].uid, name = getAllWorkout.body()!![i].name,
                     intensity = getAllWorkout.body()!![i].intensity, workoutTime = getAllWorkout.body()!![i].time,
                     kcal = getAllWorkout.body()!![i].calories, created = getAllWorkout.body()!![i].date))
               }
            }

            val getAllBody = RetrofitAPI.api.getAllBody("Bearer $access")

            if(getAllBody.isSuccessful) {
               for(i in 0 until getAllBody.body()!!.size) {
                  dataManager.insertBody(Body(userId = getUser2.id, uid = getAllBody.body()!![i].uid, height = getAllBody.body()!![i].height, weight = getAllBody.body()!![i].weight,
                     intensity = getAllBody.body()!![i].workoutIntensity, fat = getAllBody.body()!![i].bodyFatPercentage, muscle = getAllBody.body()!![i].skeletalMuscleMass,
                     bmi = getAllBody.body()!![i].bodyMassIndex, bmr = getAllBody.body()!![i].basalMetabolicRate, created = getAllBody.body()!![i].createdAt))
               }
            }

            val getAllSleep = RetrofitAPI.api.getAllSleep("Bearer $access")

            if(getAllSleep.isSuccessful) {
               for(i in 0 until getAllSleep.body()!!.size) {
                  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                  val bedTime = LocalDateTime.parse(getAllSleep.body()!![i].starts, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"))
                  val wakeTime = LocalDateTime.parse(getAllSleep.body()!![i].ends, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'"))
                  val regDate = LocalDateTime.parse(getAllSleep.body()!![i].starts, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'")).format(formatter)

                  val startDT = LocalDateTime.of(bedTime.year, bedTime.monthValue, bedTime.dayOfMonth, bedTime.hour, bedTime.minute)
                  val endDT = LocalDateTime.of(wakeTime.year, wakeTime.monthValue, wakeTime.dayOfMonth, wakeTime.hour, wakeTime.minute)
                  val diff = Duration.between(startDT, endDT)

                  dataManager.insertSleep(Sleep(uid = getAllSleep.body()!![i].uid, startTime = getAllSleep.body()!![i].starts,
                     endTime = getAllSleep.body()!![i].ends, total = diff.toMinutes().toInt(), created = regDate.toString()))
               }
            }

            /*val getMedicine = RetrofitAPI.api.getAllMedicine("Bearer $access")

            if(getMedicine.isSuccessful) {
               for(i in 0 until getMedicine.body()!!.size) {
                  dataManager.insertDrug(Drug(uid = getMedicine.body()!![i].uid, type = getMedicine.body()!![i].category,
                     name = getMedicine.body()!![i].name, amount = getMedicine.body()!![i].amount, unit = getMedicine.body()!![i].unit,
                     startDate = getMedicine.body()!![i].starts, endDate = getMedicine.body()!![i].ends))
               }
            }*/

            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
         }else Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
      }
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