package com.makebodywell.bodywell.view.setting

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
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.BuildConfig.GOOGLE_WEB_CLIENT_ID
import com.makebodywell.bodywell.BuildConfig.NAVER_CLIENT_ID
import com.makebodywell.bodywell.BuildConfig.NAVER_CLIENT_SECRET
import com.makebodywell.bodywell.R
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
import com.makebodywell.bodywell.util.CustomUtil.Companion.networkStatusCheck
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.home.MainFragment
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

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var getUser = User()
   private var getToken = Token()

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity(), MainFragment())
         }
      }

      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

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
      dataManager.open()

      userProfile()

      binding.cvProfile.setOnClickListener {
         replaceFragment1(requireActivity(), ProfileFragment())
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
                  val getUser = dataManager.getUser()

                  when(getUser.type) {
                     "google" -> {
                        logoutProcess()
                     }
                     "naver" -> {
                        logoutProcess()
                     }
                     "kakao" -> {
                        logoutProcess()
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
                  getUser = dataManager.getUser()
                  getToken = dataManager.getToken()

                  when(getUser.type) {
                     "google" -> {
                        resignProcess()
                     }
                     "naver" -> {
                        resignProcess()
                     }
                     "kakao" -> {
                        resignProcess()
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

   private fun logoutProcess() {
      MyApp.prefs.removePrefs("userId")
      Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), LoginActivity::class.java))
      exitProcess(0)
   }

   private fun resignProcess() {
      removeData()
      MyApp.prefs.removePrefs("userId")

      Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), InitActivity::class.java))
      exitProcess(0)
   }

   private fun userProfile() {
      val getUser = dataManager.getUser()
      if(getUser.name != null && getUser.name != "") binding.tvName.text = getUser.name
      if(getUser.image != null && getUser.image != "") binding.ivUser.setImageURI(Uri.parse(getUser.image))

      if(getUser.birthday != null && getUser.birthday != "") {
         val current = Calendar.getInstance()
         val currentYear = current.get(Calendar.YEAR)
         val currentMonth = current.get(Calendar.MONTH) + 1
         val currentDay = current.get(Calendar.DAY_OF_MONTH)

         var age: Int = currentYear - getUser.birthday!!.substring(0 until 4).toInt()
         if (getUser.birthday!!.substring(5 until 7).toInt() * 100 + getUser.birthday!!.substring(8 until 10).toInt() > currentMonth * 100 + currentDay)
            age--

         val gender = if(getUser.gender == "MALE") "남" else "여"

         binding.tvAge.text = "만${age}세 / $gender"
      }

      var height = "0"
      var weight = "0"

      if(getUser.height != null && getUser.height != "0") {
         val hSplit = getUser.height!!.split(".")
         height = if(hSplit[1] == "0") hSplit[0] else getUser.height!!
      }

      if(getUser.weight != null && getUser.weight != "0") {
         val wSplit = getUser.weight!!.split(".")
         weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight!!
      }

      binding.tvHeight.text = "${height}cm / ${weight}kg"
   }

   private fun removeData() {
      val alarmReceiver = AlarmReceiver()

      val getDrugId = dataManager.getDrugId()
      for(i in 0 until getDrugId.size) {
         alarmReceiver.cancelAlarm(requireActivity(), getDrugId[i])
      }

      dataManager.deleteAll(TABLE_USER, "id")
      dataManager.deleteAll(TABLE_TOKEN, "userId")
      dataManager.deleteAll(TABLE_FOOD, "userId")
      dataManager.deleteAll(TABLE_DAILY_FOOD, "userId")
      dataManager.deleteAll(TABLE_WATER, "userId")
      dataManager.deleteAll(TABLE_EXERCISE, "userId")
      dataManager.deleteAll(TABLE_DAILY_EXERCISE, "userId")
      dataManager.deleteAll(TABLE_BODY, "userId")
      dataManager.deleteAll(TABLE_DRUG, "userId")
      dataManager.deleteAll(TABLE_DRUG_TIME, "userId")
      dataManager.deleteAll(TABLE_DRUG_CHECK, "userId")
      dataManager.deleteAll(TABLE_NOTE, "userId")
      dataManager.deleteAll(TABLE_SLEEP, "userId")
      dataManager.deleteAll(TABLE_IMAGE, "userId")
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}