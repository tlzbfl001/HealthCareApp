package com.makebodywell.bodywell.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig
import com.makebodywell.bodywell.BuildConfig.GOOGLE_WEB_CLIENT_ID
import com.makebodywell.bodywell.BuildConfig.NAVER_CLIENT_ID
import com.makebodywell.bodywell.BuildConfig.NAVER_CLIENT_SECRET
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.RemoveUserMutation
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_BODY
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DAILY_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_CHECK
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_DRUG_TIME
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_EXERCISE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_FOOD
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_IMAGE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_NOTE
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_SLEEP
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_TOKEN
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_WATER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSettingBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.util.AlarmReceiver
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.apolloClient
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.util.PermissionUtil.Companion.CAMERA_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.STORAGE_REQUEST_CODE
import com.makebodywell.bodywell.util.PermissionUtil.Companion.cameraRequest
import com.makebodywell.bodywell.util.PermissionUtil.Companion.getImageUriWithAuthority
import com.makebodywell.bodywell.util.PermissionUtil.Companion.randomFileName
import com.makebodywell.bodywell.util.PermissionUtil.Companion.saveFile
import com.makebodywell.bodywell.view.init.InitActivity
import com.makebodywell.bodywell.view.init.LoginActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.system.exitProcess

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var getUser = User()
   private var getToken = Token()
   private var dialog: Dialog? = null

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

      binding.ivUser.setOnClickListener {
         if(cameraRequest(requireActivity())) {
            dialog = Dialog(requireActivity())
            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.setContentView(R.layout.dialog_gallery)

            val clCamera = dialog!!.findViewById<ConstraintLayout>(R.id.clCamera)
            val clGallery = dialog!!.findViewById<ConstraintLayout>(R.id.clGallery)

            clCamera.setOnClickListener {
               val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
               startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }

            clGallery.setOnClickListener {
               val intent = Intent(Intent.ACTION_PICK)
               intent.type = MediaStore.Images.Media.CONTENT_TYPE
               startActivityForResult(intent, STORAGE_REQUEST_CODE)
            }

            dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setGravity(Gravity.BOTTOM)
            dialog!!.show()
         }
      }

      binding.tvAlarm.setOnClickListener {
         val intent = Intent()
         intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
         intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().packageName)
         startActivity(intent)
      }

      binding.tvConnect.setOnClickListener {
         replaceFragment1(requireActivity(), ConnectFragment())
      }

      binding.tvLogout.setOnClickListener {
         if(!networkStatusCheck(requireActivity())){
            Toast.makeText(context, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("로그아웃")
               .setMessage("정말 로그아웃하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  val getUser = dataManager!!.getUser()

                  when(getUser.type) {
                     "google" -> {
                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                           .requestEmail()
                           .build()
                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        gsc.signOut().addOnCompleteListener {
                           if(it.isSuccessful) {
                              MyApp.prefs.removePrefs("userId")

                              Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                              finishAffinity(requireActivity())
                              startActivity(Intent(requireActivity(), LoginActivity::class.java))
                              exitProcess(0)
                           }else {
                              Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                           }
                        }
                     }
                     "naver" -> {
                        NaverIdLoginSDK.initialize(requireActivity(), NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, getString(
                           R.string.app_name))
                        NaverIdLoginSDK.logout()
                        MyApp.prefs.removePrefs("userId")

                        Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        finishAffinity(requireActivity())
                        startActivity(Intent(requireActivity(), LoginActivity::class.java))
                        exitProcess(0)
                     }
                     "kakao" -> {
                        UserApiClient.instance.logout { error ->
                           if(error != null) {
                              Toast.makeText(requireActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                           }else {
                              MyApp.prefs.removePrefs("userId")

                              Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                              finishAffinity(requireActivity())
                              startActivity(Intent(requireActivity(), LoginActivity::class.java))
                              exitProcess(0)
                           }
                        }
                     }
                  }
               }
               .setNegativeButton("취소", null)
               .create()

            dialog.show()
         }
      }

      binding.tvResign.setOnClickListener {
         if(!networkStatusCheck(requireActivity())){
            Toast.makeText(context, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("회원탈퇴")
               .setMessage("해당 계정과 관련된 데이터도 함께 삭제됩니다. 정말 탈퇴하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  getUser = dataManager!!.getUser()
                  getToken = dataManager!!.getToken()

                  when(getUser.type) {
                     "google" -> {
                        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                           .requestEmail()
                           .build()
                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        if(account == null) {
                           Toast.makeText(context, "로그아웃 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                           lifecycleScope.launch {
                              if(!removeUser()) {
                                 Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                              }else {
                                 gsc.revokeAccess().addOnCompleteListener {
                                    if(it.isSuccessful) {
                                       removeData()
                                       MyApp.prefs.removePrefs("userId")

                                       Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                                       finishAffinity(requireActivity())
                                       startActivity(Intent(requireActivity(), InitActivity::class.java))
                                       exitProcess(0)
                                    }else {
                                       Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                                    }
                                 }
                              }
                           }
                        }
                     }
                     "naver" -> {
                        NaverIdLoginSDK.initialize(requireActivity(), NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, getString(
                           R.string.app_name))
                        if(NaverIdLoginSDK.getAccessToken() == null) {
                           Toast.makeText(context, "로그아웃 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                           lifecycleScope.launch {
                              if(!removeUser()) {
                                 Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                              }else {
                                 NidOAuthLogin().callDeleteTokenApi(requireActivity(), object : OAuthLoginCallback {
                                    override fun onSuccess() {
                                       removeData()
                                       MyApp.prefs.removePrefs("userId")

                                       Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                                       finishAffinity(requireActivity())
                                       startActivity(Intent(requireActivity(), InitActivity::class.java))
                                       exitProcess(0)
                                    }
                                    override fun onFailure(httpStatus: Int, message: String) {
                                       Log.e(TAG, "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                                       Toast.makeText(context, "로그아웃 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
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
                              Toast.makeText(context, "로그아웃 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                           }else if (token != null) {
                              UserApiClient.instance.unlink { error ->
                                 lifecycleScope.launch{
                                    if(error != null || !removeUser()) {
                                       Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
                                    }else {
                                       removeData()
                                       MyApp.prefs.removePrefs("userId")

                                       Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
                                       finishAffinity(requireActivity())
                                       startActivity(Intent(requireActivity(), InitActivity::class.java))
                                       exitProcess(0)
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

   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)

      if(resultCode == Activity.RESULT_OK){
         when(requestCode){
            CAMERA_REQUEST_CODE -> {
               if(data!!.extras?.get("data") != null){
                  val img = data.extras?.get("data") as Bitmap
                  val uri = saveFile(requireActivity(), "image/jpeg", img)

                  binding.ivUser.setImageURI(Uri.parse(uri.toString()))

                  if(uri.toString() != "") {
                     dataManager!!.updateUserStr(TABLE_USER, "profileImage", uri.toString())
                  }

                  dialog!!.dismiss()
               }
            }
            STORAGE_REQUEST_CODE -> {
               val uri = data!!.data
               val image = if(data.data!!.toString().contains("com.google.android.apps.photos.contentprovider")) {
                  getImageUriWithAuthority(requireActivity(), uri)
               }else {
                  uri.toString()
               }

               binding.ivUser.setImageURI(Uri.parse(uri.toString()))

               if(image != "") {
                  dataManager!!.updateUserStr(TABLE_USER, "profileImage", image.toString())
               }

               dialog!!.dismiss()
            }
         }
      }
   }

   private fun removeData() {
      val alarmReceiver = AlarmReceiver()

      val getDrugId = dataManager!!.getDrugId()
      for(i in 0 until getDrugId.size) {
         alarmReceiver.cancelAlarm(requireActivity(), getDrugId[i])
      }

      dataManager!!.deleteAll(TABLE_USER, "id")
      dataManager!!.deleteAll(TABLE_TOKEN, "userId")
      dataManager!!.deleteAll(TABLE_FOOD, "userId")
      dataManager!!.deleteAll(TABLE_DAILY_FOOD, "userId")
      dataManager!!.deleteAll(TABLE_WATER, "userId")
      dataManager!!.deleteAll(TABLE_EXERCISE, "userId")
      dataManager!!.deleteAll(TABLE_DAILY_EXERCISE, "userId")
      dataManager!!.deleteAll(TABLE_BODY, "userId")
      dataManager!!.deleteAll(TABLE_DRUG, "userId")
      dataManager!!.deleteAll(TABLE_DRUG_TIME, "userId")
      dataManager!!.deleteAll(TABLE_DRUG_CHECK, "userId")
      dataManager!!.deleteAll(TABLE_NOTE, "userId")
      dataManager!!.deleteAll(TABLE_SLEEP, "userId")
      dataManager!!.deleteAll(TABLE_IMAGE, "userId")
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