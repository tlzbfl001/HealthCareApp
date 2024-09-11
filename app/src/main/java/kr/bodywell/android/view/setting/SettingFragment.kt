package kr.bodywell.android.view.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.api.RetrofitAPI
import kr.bodywell.android.database.DBHelper.Companion.BODY
import kr.bodywell.android.database.DBHelper.Companion.DAILY_EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.DAILY_FOOD
import kr.bodywell.android.database.DBHelper.Companion.DRUG
import kr.bodywell.android.database.DBHelper.Companion.DRUG_CHECK
import kr.bodywell.android.database.DBHelper.Companion.DRUG_TIME
import kr.bodywell.android.database.DBHelper.Companion.EXERCISE
import kr.bodywell.android.database.DBHelper.Companion.FOOD
import kr.bodywell.android.database.DBHelper.Companion.GOAL
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DBHelper.Companion.NOTE
import kr.bodywell.android.database.DBHelper.Companion.SLEEP
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.UNUSED
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.database.DBHelper.Companion.WATER
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSettingBinding
import kr.bodywell.android.model.Constant
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.networkStatusCheck
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.util.PermissionUtil.BT_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.BT_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkBtPermission
import kr.bodywell.android.view.home.MainFragment
import kr.bodywell.android.view.init.LoginActivity
import java.util.Calendar
import kotlin.system.exitProcess

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
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

      setStatusBar(requireActivity(), binding.constarint)

      pLauncher = registerForActivityResult(
         ActivityResultContracts.RequestMultiplePermissions()
      ){}

      dataManager = DataManager(activity)
      dataManager.open()

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      userProfile()

      binding.cvProfile.setOnClickListener {
         replaceFragment1(requireActivity(), ProfileFragment())
      }

      binding.tvAlarm.setOnClickListener {
         replaceFragment1(requireActivity(), AlarmFragment())
      }

      binding.tvConnect.setOnClickListener {
         // 블루투스 권한 설정
         if(!checkBtPermission(requireActivity())) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
               pLauncher.launch(BT_PERMISSION_2)
            }else {
               pLauncher.launch(BT_PERMISSION_1)
            }
         }else {
            replaceFragment1(requireActivity(), ConnectFragment())
         }
      }

      binding.tvLogout.setOnClickListener {
         if(!networkStatusCheck(requireActivity())){
            Toast.makeText(context, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("로그아웃")
               .setMessage("정말 로그아웃 하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  when(getUser.type) {
                     Constant.GOOGLE.name -> {
                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(resources.getString(R.string.googleWebClientId))
                           .requestEmail()
                           .build()

                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        gsc.signOut().addOnCompleteListener {
                           if(it.isSuccessful) logoutProcess() else Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }
                     }
                     Constant.NAVER.name -> {
                        NaverIdLoginSDK.initialize(requireActivity(), resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))
                        NaverIdLoginSDK.logout()
                        logoutProcess()
                     }
                     Constant.KAKAO.name -> {
                        UserApiClient.instance.logout { error ->
                           if(error != null) Toast.makeText(requireActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show() else logoutProcess()
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
               .setTitle("회원 탈퇴")
               .setMessage("정말 탈퇴하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  when(getUser.type) {
                     Constant.GOOGLE.name -> {
                        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(resources.getString(R.string.googleWebClientId))
                           .requestEmail()
                           .build()

                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        if(account == null) {
                           Toast.makeText(context, "로그아웃 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                           gsc.revokeAccess().addOnCompleteListener {
                              if(it.isSuccessful) deleteData() else Toast.makeText(context, "탈퇴 실패", Toast.LENGTH_SHORT).show()
                           }
                        }
                     }
                     Constant.NAVER.name -> {
                        NaverIdLoginSDK.initialize(requireActivity(), resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))

                        if(NaverIdLoginSDK.getAccessToken() == null) {
                           Toast.makeText(context, "로그아웃 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                           lifecycleScope.launch {
                              NidOAuthLogin().callDeleteTokenApi(requireActivity(), object : OAuthLoginCallback {
                                 override fun onSuccess() {
                                    deleteData()
                                 }

                                 override fun onFailure(httpStatus: Int, message: String) {
                                    Toast.makeText(context, "로그아웃 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                                 }

                                 override fun onError(errorCode: Int, message: String) {
                                    onFailure(errorCode, message)
                                 }
                              })
                           }
                        }
                     }
                     Constant.KAKAO.name -> {
                        UserApiClient.instance.accessTokenInfo { token, error ->
                           if(error != null) {
                              Toast.makeText(context, "로그아웃 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                           }else if (token != null) {
                              UserApiClient.instance.unlink { err ->
                                 lifecycleScope.launch{
                                    if(err == null) deleteData() else Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
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

   private fun userProfile() {
      if(getUser.name != "") binding.tvName.text = getUser.name
      if(getUser.profileImage != "") {
         val imgPath = requireActivity().filesDir.toString() + "/" + getUser.profileImage // 내부 저장소에 저장되어 있는 이미지 경로
         val bm = BitmapFactory.decodeFile(imgPath)
         binding.ivUser.setImageBitmap(bm)
      }

      if(getUser.birthday != "") {
         val current = Calendar.getInstance()
         val currentYear = current.get(Calendar.YEAR)
         val currentMonth = current.get(Calendar.MONTH) + 1
         val currentDay = current.get(Calendar.DAY_OF_MONTH)

         var age = currentYear - getUser.birthday!!.substring(0 until 4).toInt()
         if(getUser.birthday!!.substring(5 until 7).toInt() * 100 + getUser.birthday!!.substring(8 until 10).toInt() > currentMonth * 100 + currentDay) age--

         val gender = if(getUser.gender == Constant.Male.name) "남" else "여"

         binding.tvAge.text = "만${age}세 / $gender"
      }

      var height = "0"
      var weight = "0"

      if(getUser.height!! > 0) {
         val hSplit = getUser.height.toString().split(".")
         height = if(hSplit[1] == "0") hSplit[0] else getUser.height.toString()
      }

      if(getUser.weight!! > 0) {
         val wSplit = getUser.weight.toString().split(".")
         weight = if(wSplit[1] == "0") wSplit[0] else getUser.weight.toString()
      }

      binding.tvHeight.text = "${height}cm / ${weight}kg"
   }

   private fun logoutProcess() {
      val alarmReceiver = AlarmReceiver()
      val getDrugId = dataManager.getDrugId()

      for(i in 0 until getDrugId.size) alarmReceiver.cancelAlarm(requireActivity(), getDrugId[i])

      MyApp.prefs.removePrefs()

      requireActivity().runOnUiThread {
         Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
      }

      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), LoginActivity::class.java))
      exitProcess(0)
   }

   private fun resignProcess() {
      CoroutineScope(Dispatchers.IO).launch {
         val getUserUid = RetrofitAPI.api.getUser("Bearer ${getToken.access}")
         if(getUserUid.isSuccessful) {
            val response = RetrofitAPI.api.deleteUser("Bearer ${getToken.access}")
            if(response.isSuccessful) {
               deleteData()
            }else {
               requireActivity().runOnUiThread {
                  Toast.makeText(requireActivity(), "탈퇴 실패", Toast.LENGTH_SHORT).show()
               }
            }
         }
      }
   }

   private fun deleteData() {
      dataManager.deleteTable(USER, "id")
      dataManager.deleteTable(TOKEN, USER_ID)
      dataManager.deleteTable(FOOD, USER_ID)
      dataManager.deleteTable(DAILY_FOOD, USER_ID)
      dataManager.deleteTable(WATER, USER_ID)
      dataManager.deleteTable(EXERCISE, USER_ID)
      dataManager.deleteTable(DAILY_EXERCISE, USER_ID)
      dataManager.deleteTable(BODY, USER_ID)
      dataManager.deleteTable(DRUG, USER_ID)
      dataManager.deleteTable(DRUG_TIME, USER_ID)
      dataManager.deleteTable(DRUG_CHECK, USER_ID)
      dataManager.deleteTable(NOTE, USER_ID)
      dataManager.deleteTable(SLEEP, USER_ID)
      dataManager.deleteTable(GOAL, USER_ID)
      dataManager.deleteTable(IMAGE, USER_ID)
      dataManager.deleteTable(UNUSED, USER_ID)
      logoutProcess()
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}