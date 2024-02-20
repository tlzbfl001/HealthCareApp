package com.makebodywell.bodywell.view.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig
import com.makebodywell.bodywell.RemoveUserMutation
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSettingBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.apolloClient
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.init.LoginActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.launch
import java.util.Calendar

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var getUser = User()
   private var getToken = Token()

   @SuppressLint("InternalInsetResource", "DiscouragedApi")
   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSettingBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.cl1.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      userProfile()

      binding.tvConnect.setOnClickListener {
         replaceFragment1(requireActivity(), ConnectFragment())
      }

      binding.tvLogout.setOnClickListener {
         val dialog = AlertDialog.Builder(context)
            .setMessage("정말 로그아웃하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               val getUser = dataManager!!.getUser()

               when(getUser.type) {
                  "google" -> {
                     val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        .requestEmail()
                        .build()
                     val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                     gsc.signOut().addOnCompleteListener {
                        if (it.isSuccessful) {
                           MyApp.prefs.removePrefs("userId")
                           Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                           startActivity(Intent(requireActivity(), LoginActivity::class.java))
                           requireActivity().finish()
                        } else {
                           Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }
                     }
                  }
                  "naver" -> {
                     NaverIdLoginSDK.logout()
                     MyApp.prefs.removePrefs("userId")
                     Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                     startActivity(Intent(requireActivity(), LoginActivity::class.java))
                     requireActivity().finish()
                  }
                  "kakao" -> {
                     UserApiClient.instance.logout { error ->
                        if (error != null) {
                           Toast.makeText(requireActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }else {
                           MyApp.prefs.removePrefs("userId")
                           Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                           startActivity(Intent(requireActivity(), LoginActivity::class.java))
                           requireActivity().finish()
                        }
                     }
                  }
               }
            }
            .setNegativeButton("취소", null)
            .create()

         dialog.show()
      }

      binding.tvResign.setOnClickListener {
         val dialog = AlertDialog.Builder(context)
            .setMessage("해당 계정과 관련된 데이터도 함께 삭제됩니다. 정말 탈퇴하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               getUser = dataManager!!.getUser()
               getToken = dataManager!!.getToken()

               when(getUser.type) {
                  "google" -> {
                     val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

                     val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                        .requestEmail()
                        .build()
                     val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                     if(account == null) {
                        Toast.makeText(context, "재로그인 후 탈퇴할 수 있습니다.", Toast.LENGTH_SHORT).show()
                     }else {
                        lifecycleScope.launch {
                           if(!removeUser()) {
                              Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                           }else {
                              gsc.revokeAccess().addOnCompleteListener {
                                 if (it.isSuccessful) {
                                    removeData()
                                    MyApp.prefs.removePrefs("userId")
                                    Toast.makeText(context, "탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                    requireActivity().finish()
                                 } else {
                                    Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                                 }
                              }
                           }
                        }
                     }
                  }
                  "naver" -> {
                     if(NaverIdLoginSDK.getAccessToken() == null) {
                        NidOAuthLogin().callRefreshAccessTokenApi(requireActivity(), object : OAuthLoginCallback {
                           override fun onSuccess() {
                              NidOAuthLogin().callDeleteTokenApi(requireActivity(), object : OAuthLoginCallback {
                                 override fun onSuccess() {
                                    removeData()
                                    MyApp.prefs.removePrefs("userId")

                                    Toast.makeText(context, "탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                    requireActivity().finish()
                                 }
                                 override fun onFailure(httpStatus: Int, message: String) {
                                    Log.d(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                                    Toast.makeText(context, "재로그인 후 탈퇴할 수 있습니다.", Toast.LENGTH_SHORT).show()
                                 }
                                 override fun onError(errorCode: Int, message: String) {
                                    onFailure(errorCode, message)
                                 }
                              })
                           }

                           override fun onFailure(httpStatus: Int, message: String) {
                              Log.d(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                              Toast.makeText(context, "재로그인 후 탈퇴할 수 있습니다.", Toast.LENGTH_SHORT).show()
                           }

                           override fun onError(errorCode: Int, message: String) {
                              onFailure(errorCode, message)
                           }
                        })
                     }else {
                        lifecycleScope.launch {
                           if(!removeUser()) {
                              Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                           }else {
                              NidOAuthLogin().callDeleteTokenApi(requireActivity(), object : OAuthLoginCallback {
                                 override fun onSuccess() {
                                    removeData()
                                    MyApp.prefs.removePrefs("userId")

                                    Toast.makeText(context, "탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                    requireActivity().finish()
                                 }
                                 override fun onFailure(httpStatus: Int, message: String) {
                                    Log.d(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                                    Toast.makeText(context, "재로그인 후 탈퇴할 수 있습니다.", Toast.LENGTH_SHORT).show()
                                 }
                                 override fun onError(errorCode: Int, message: String) {
                                    onFailure(errorCode, message)
                                 }
                              })
                           }
                        }
                     }
                  }
                  "kakao" -> {
                     UserApiClient.instance.accessTokenInfo { token, error ->
                        if(error != null) {
                           Toast.makeText(context, "재로그인 후 탈퇴할 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }else if (token != null) {
                           lifecycleScope.launch{
                              if(!removeUser()) {
                                 Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                              }else {
                                 UserApiClient.instance.unlink { error ->
                                    if(error != null) {
                                       Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                                    }else {
                                       removeData()
                                       MyApp.prefs.removePrefs("userId")

                                       Toast.makeText(context, "탈퇴에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                       startActivity(Intent(requireActivity(), LoginActivity::class.java))
                                       requireActivity().finish()
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
            .setNegativeButton("취소", null)
            .create()

         dialog.show()
      }

      return binding.root
   }

   @SuppressLint("SetTextI18n")
   private fun userProfile() {
      val getUser = dataManager!!.getUser()

      if(getUser.name != "") binding.tvName.text = getUser.name

      if(getUser.profileImage != "") binding.ivUser.setImageURI(Uri.parse(getUser.profileImage))

      if(getUser.birthday != "") {
         val current = Calendar.getInstance()
         val currentYear = current.get(Calendar.YEAR)
         val currentMonth = current.get(Calendar.MONTH) + 1
         val currentDay = current.get(Calendar.DAY_OF_MONTH)

         var age: Int = currentYear - getUser.birthday!!.substring(0 until 4).toInt()
         if (getUser.birthday!!.substring(5 until 7).toInt() * 100 + getUser.birthday!!.substring(8 until 10).toInt() > currentMonth * 100 + currentDay)
            age--

         val gender = if(getUser.gender == "MALE" || getUser.gender == "M") "남" else "여"

         binding.tvAge.text = "만${age}세 / $gender"
      }

      var height = "0"
      var weight = "0"

      if(getUser.height != "0") {
         val hSplit = getUser.height!!.split(".")
         height = if(hSplit[1] == "0") hSplit[0] else getUser.height!!
      }

      if(getUser.weight != "0") {
         val wSplit = getUser.weight!!.split(".")
         weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight!!
      }

      binding.tvHeight.text = "${height}cm / ${weight}kg"
   }

   private fun removeData() {
      dataManager!!.deleteAll(DBHelper.TABLE_USER, "id")
      dataManager!!.deleteAll(DBHelper.TABLE_TOKEN, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_FOOD, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_WATER, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_EXERCISE, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_BODY, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_DRUG, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_DRUG_TIME, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_DRUG_CHECK, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_NOTE, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_SLEEP, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_DAILY_DATA, "userId")
      dataManager!!.deleteAll(DBHelper.TABLE_IMAGE, "userId")
   }

   private suspend fun removeUser(): Boolean {
      val removeUser = apolloClient.mutation(RemoveUserMutation(
         userId = getUser.userId!!
      )).addHttpHeader(
         "Authorization", "Bearer ${getToken.accessToken}"
      ).execute()

      return removeUser.data != null
   }
}