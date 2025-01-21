package kr.bodywell.android.view.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.database.DBHelper
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE
import kr.bodywell.android.database.DBHelper.Companion.MEDICINE_TIME
import kr.bodywell.android.database.DBHelper.Companion.TOKEN
import kr.bodywell.android.database.DBHelper.Companion.UPDATE_TIME
import kr.bodywell.android.database.DBHelper.Companion.USER
import kr.bodywell.android.database.DBHelper.Companion.USER_ID
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentSettingBinding
import kr.bodywell.android.model.Constant.GOOGLE
import kr.bodywell.android.model.Constant.KAKAO
import kr.bodywell.android.model.Constant.NAVER
import kr.bodywell.android.model.Constant.MALE
import kr.bodywell.android.model.Token
import kr.bodywell.android.model.User
import kr.bodywell.android.service.AlarmReceiver
import kr.bodywell.android.util.CustomUtil.networkStatus
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.util.MyApp.Companion.dataManager
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.util.PermissionUtil.BT_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.BT_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkBluetoothPermission
import kr.bodywell.android.util.PermissionUtil.checkMediaPermission
import kr.bodywell.android.view.MainFragment
import kr.bodywell.android.view.init.LoginActivity
import java.io.File
import java.util.Calendar
import kotlin.system.exitProcess

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
//   private lateinit var dataManager: DataManager
   private var getUser = User()
   private var getToken = Token()

      override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment1(requireActivity().supportFragmentManager, MainFragment())
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

      getUser = dataManager.getUser()
      getToken = dataManager.getToken()

      showProfile()

      binding.tvProfile.setOnClickListener {
         replaceFragment1(requireActivity().supportFragmentManager, ProfileFragment())
      }

      binding.tvAlarm.setOnClickListener {
         replaceFragment1(requireActivity().supportFragmentManager, AlarmFragment())
      }

      binding.tvConnect.setOnClickListener {
         if(!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(requireActivity(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
         }else {
            if(!checkBluetoothPermission(requireActivity())) {
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                  pLauncher.launch(BT_PERMISSION_2)
               }else {
                  pLauncher.launch(BT_PERMISSION_1)
               }
            }else {
               replaceFragment1(requireActivity().supportFragmentManager, ConnectFragment())
            }
         }
      }

      binding.tvLogout.setOnClickListener {
         if(!networkStatus(requireActivity())){
            Toast.makeText(context, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("로그아웃")
               .setMessage("정말 로그아웃 하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  when(getUser.type) {
                     GOOGLE -> {
                        val gso = GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                           .requestIdToken(resources.getString(R.string.googleWebClientId))
                           .requestEmail()
                           .build()

                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        gsc.signOut().addOnCompleteListener {
                           logoutProcess()
                        }
                     }
                     NAVER -> {
                        NaverIdLoginSDK.initialize(requireActivity(), resources.getString(R.string.naverClientId), resources.getString(R.string.naverClientSecret), getString(R.string.app_name))
                        NaverIdLoginSDK.logout()
                        logoutProcess()
                     }
                     KAKAO -> {
                        UserApiClient.instance.logout { error ->
                           logoutProcess()
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
         if(!networkStatus(requireActivity())){
            Toast.makeText(context, "네트워크에 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
         }else {
            val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
               .setTitle("회원 탈퇴")
               .setMessage("정말 탈퇴하시겠습니까?")
               .setPositiveButton("확인") { _, _ ->
                  when(getUser.type) {
                     GOOGLE -> {
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
                     NAVER -> {
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
                     KAKAO -> {
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

   private fun showProfile() {
      lifecycleScope.launch {
         val getProfile = powerSync.getProfile()

         if(checkMediaPermission(requireActivity())) {
            val getFile = powerSync.getFile(getProfile.id)
            if(getFile.name != "") {
               val imgPath = requireActivity().filesDir.toString() + "/" + getFile.name
               val file = File(imgPath)
               if(file.exists()){
                  val bm = BitmapFactory.decodeFile(imgPath)
                  binding.ivUser.setImageBitmap(bm)
               }
            }
         }

         if(getProfile.name != "") binding.tvName.text = getProfile.name

         if(getProfile.birth != "") {
            val current = Calendar.getInstance()
            val currentYear = current.get(Calendar.YEAR)
            val currentMonth = current.get(Calendar.MONTH) + 1
            val currentDay = current.get(Calendar.DAY_OF_MONTH)

            var age = currentYear - getProfile.birth!!.substring(0 until 4).toInt()
            if(getProfile.birth!!.substring(5 until 7).toInt() * 100 + getProfile.birth!!.substring(8 until 10).toInt() > currentMonth * 100 + currentDay) age--

            val gender = if(getProfile.gender == MALE) "남" else "여"

            binding.tvAge.text = "만${age}세 / $gender"
         }

         var height = "0"
         var weight = "0"

         if(getProfile.height!! > 0) {
            val hSplit = getProfile.height.toString().split(".")
            height = if(hSplit[1] == "0") hSplit[0] else getProfile.height.toString()
         }

         if(getProfile.weight!! > 0) {
            val wSplit = getProfile.weight.toString().split(".")
            weight = if(wSplit[1] == "0") wSplit[0] else getProfile.weight.toString()
         }

         binding.tvHeight.text = "${height}cm / ${weight}kg"
      }
   }

   private fun logoutProcess() {
      MyApp.prefs.removePrefs()

      requireActivity().runOnUiThread {
         Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
      }

      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), LoginActivity::class.java))
      exitProcess(0)
   }

   private fun deleteData() {
      // 알람 삭제
      val alarmReceiver = AlarmReceiver()
      val getMedicines = dataManager.getMedicines()
      for(i in getMedicines.indices) alarmReceiver.cancelAlarm(requireActivity(), getMedicines[i].id)

      // 테이블 삭제
      dataManager.deleteTable(USER, "id")
      dataManager.deleteTable(TOKEN, USER_ID)
      dataManager.deleteTable(MEDICINE, USER_ID)
      dataManager.deleteTable(MEDICINE_TIME, USER_ID)
      dataManager.deleteTable(UPDATE_TIME, USER_ID)

      // preference 정보 삭제
      MyApp.prefs.removePrefs()

      requireActivity().runOnUiThread {
         Toast.makeText(context, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
      }

      finishAffinity(requireActivity())
      startActivity(Intent(requireActivity(), LoginActivity::class.java))
      exitProcess(0)
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}