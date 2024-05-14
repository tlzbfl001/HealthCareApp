package kr.bodywell.android.view.setting

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.BuildConfig.GOOGLE_WEB_CLIENT_ID
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DBHelper.Companion.TABLE_BODY
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.TABLE_DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.TABLE_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_FOOD
import kr.bodywell.android.database.DBHelper.Companion.TABLE_IMAGE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_NOTE
import kr.bodywell.android.database.DBHelper.Companion.TABLE_SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TABLE_TOKEN
import kr.bodywell.android.database.DBHelper.Companion.TABLE_UNUSED
import kr.bodywell.android.database.DBHelper.Companion.TABLE_USER
import kr.bodywell.android.database.DBHelper.Companion.TABLE_WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSettingBinding
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.util.AlarmReceiver
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment1
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.home.MainActivity
import kr.bodywell.android.view.home.MainFragment
import kr.bodywell.android.view.init.InitActivity
import kr.bodywell.android.view.init.InputActivity
import kr.bodywell.android.view.init.LoginActivity
import kr.bodywell.android.view.init.SignupActivity
import java.time.LocalDate
import java.time.LocalDateTime
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

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

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
                  when(getUser.type) {
                     "google" -> {
                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                           .requestEmail()
                           .build()

                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)
                        gsc.signOut().addOnCompleteListener {
                           if(it.isSuccessful) {
                              logoutProcess()
                           }else {
                              Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                           }
                        }
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
                           gsc.revokeAccess().addOnCompleteListener {
                              if(it.isSuccessful) {
                                 resignProcess()
                              }else {
                                 Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                              }
                           }
                        }
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

   private fun userProfile() {
      if(getUser.name != "") binding.tvName.text = getUser.name
      if(getUser.image != "") binding.ivUser.setImageURI(Uri.parse(getUser.image))

      if(getUser.birthday != "") {
         val current = Calendar.getInstance()
         val currentYear = current.get(Calendar.YEAR)
         val currentMonth = current.get(Calendar.MONTH) + 1
         val currentDay = current.get(Calendar.DAY_OF_MONTH)

         var age = currentYear - getUser.birthday.substring(0 until 4).toInt()
         if (getUser.birthday.substring(5 until 7).toInt() * 100 + getUser.birthday.substring(8 until 10).toInt() > currentMonth * 100 + currentDay) age--

         val gender = if(getUser.gender == "MALE") "남" else "여"

         binding.tvAge.text = "만${age}세 / $gender"
      }

      var height = "0"
      var weight = "0"

      if(getUser.height > 0) {
         val hSplit = getUser.height.toString().split(".")
         height = if(hSplit[1] == "0") hSplit[0] else getUser.height.toString()
      }

      if(getUser.weight > 0) {
         val wSplit = getUser.weight.toString().split(".")
         weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight.toString()
      }

      binding.tvHeight.text = "${height}cm / ${weight}kg"
   }

   private fun logoutProcess() {
      MyApp.prefs.removePrefs("userId")
      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), LoginActivity::class.java))
      exitProcess(0)
   }

   private fun resignProcess() {
      CoroutineScope(Dispatchers.IO).launch {
         val response = RetrofitAPI.api.deleteUser("Bearer ${getToken.access}", getUser.userUid)
         if(response.isSuccessful) {
            dataManager.deleteTable(TABLE_USER, "id")
            dataManager.deleteTable(TABLE_TOKEN, "userId")
            dataManager.deleteTable(TABLE_FOOD, "userId")
            dataManager.deleteTable(TABLE_DAILY_FOOD, "userId")
            dataManager.deleteTable(TABLE_WATER, "userId")
            dataManager.deleteTable(TABLE_EXERCISE, "userId")
            dataManager.deleteTable(TABLE_DAILY_EXERCISE, "userId")
            dataManager.deleteTable(TABLE_BODY, "userId")
            dataManager.deleteTable(TABLE_DRUG, "userId")
            dataManager.deleteTable(TABLE_DRUG_TIME, "userId")
            dataManager.deleteTable(TABLE_DRUG_CHECK, "userId")
            dataManager.deleteTable(TABLE_NOTE, "userId")
            dataManager.deleteTable(TABLE_SLEEP, "userId")
            dataManager.deleteTable(TABLE_IMAGE, "userId")
            dataManager.deleteTable(TABLE_UNUSED, "userId")

            val alarmReceiver = AlarmReceiver()
            val getDrugId = dataManager.getDrugId()

            for(i in 0 until getDrugId.size) {
               alarmReceiver.cancelAlarm(requireActivity(), getDrugId[i])
            }

            MyApp.prefs.removePrefs("userId")

            requireActivity().runOnUiThread{
               Toast.makeText(context, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show()
            }

            finishAffinity(requireActivity())
            startActivity(Intent(requireActivity(), InitActivity::class.java))
            exitProcess(0)
         }else {
            requireActivity().runOnUiThread{
               Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
            }
         }
      }
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}