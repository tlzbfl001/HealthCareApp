package com.makebodywell.bodywell.view.init

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.makebodywell.bodywell.CreateActivityMutation
import com.makebodywell.bodywell.CreateBodyMeasurementMutation
import com.makebodywell.bodywell.CreateDeviceMutation
import com.makebodywell.bodywell.CreateHealthMutation
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.CreateUserKakaoMutation
import com.makebodywell.bodywell.CreateUserNaverMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.MeQuery
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputTermsBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateActivityInput
import com.makebodywell.bodywell.type.CreateBodyMeasurementInput
import com.makebodywell.bodywell.type.CreateDeviceInput
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.apolloClient
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1
import com.makebodywell.bodywell.util.MyApp
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.log

class InputTermsFragment : Fragment() {
   private var _binding: FragmentInputTermsBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var user = User()
   private var isAll = true

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputTermsBinding.inflate(layoutInflater)

      dataManager = DataManager(requireActivity())
      dataManager!!.open()

      user = arguments?.getParcelable("user")!!

      binding.ivBack.setOnClickListener {
        replaceLoginFragment1(requireActivity(), LoginFragment())
      }

      binding.cbAll.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            binding.cb1.isChecked = true
            binding.cb2.isChecked = true
            binding.cb3.isChecked = true
            binding.cb4.isChecked = true
            isAll = true
         }else if(!isChecked && isAll) {
            binding.cb1.isChecked = false
            binding.cb2.isChecked = false
            binding.cb3.isChecked = false
            binding.cb4.isChecked = false
         }
      }

      binding.cb1.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb2.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb3.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.cb4.setOnCheckedChangeListener { _, _ ->
         if (binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked && binding.cb4.isChecked) {
            binding.cbAll.isChecked = true
         }else {
            isAll = false
            binding.cbAll.isChecked = false
         }
      }

      binding.tvView1.setOnClickListener {
         showTermsDialog("서비스 이용 약관 동의", 1)
      }

      binding.tvView2.setOnClickListener {
         showTermsDialog("개인정보처리방침 동의", 2)
      }

      binding.tvView3.setOnClickListener {
         showTermsDialog("민감정보 수집 및 이용 동의", 3)
      }

      binding.tvView4.setOnClickListener {
         showTermsDialog("마케팅 수신 동의", 4)
      }

      binding.cvContinue.setOnClickListener {
         if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
            when(user.type) {
               "google" -> googleSignIn()
               "naver" -> naverSignIn()
               "kakao" -> kakaoSignIn()
            }
         }else {
            Toast.makeText(requireActivity(), "필수 이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show()
         }
      }

      return binding.root
   }

   private fun googleSignIn() {
      lifecycleScope.launch{
         val signIn = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            val login = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               createUser(login.data!!.loginUserGoogle.accessToken!!, login.data!!.loginUserGoogle.refreshToken!!)
            }
         }
      }
   }

   private fun naverSignIn() {
      lifecycleScope.launch{
         val signIn = apolloClient.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
            accessToken = NaverIdLoginSDK.getAccessToken().toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요1.", Toast.LENGTH_SHORT).show()
         }else {
            val login = apolloClient.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
               accessToken = user.idToken.toString()
            ))).execute()

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요2.", Toast.LENGTH_SHORT).show()
            }else {
               createUser(login.data!!.loginUserNaver.accessToken!!, login.data!!.loginUserNaver.refreshToken!!)
            }
         }
      }
   }

   private fun kakaoSignIn() {
      lifecycleScope.launch{
         val signIn = apolloClient.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            val login = apolloClient.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               createUser(login.data!!.loginUserKakao.accessToken!!, login.data!!.loginUserKakao.refreshToken!!)
            }
         }
      }
   }

   @SuppressLint("HardwareIds")
   private suspend fun createUser(access: String, refresh: String) {
      val me1 = apolloClient.query(MeQuery()).addHttpHeader(
         "Authorization",
         "Bearer $access"
      ).execute()

      if(me1.data == null) {
         Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요3.", Toast.LENGTH_SHORT).show()
      }else {
         val userId = me1.data!!.me.user.userId
         val device = if(Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID) != null) {
            Settings.Secure.getString(requireActivity().contentResolver, Settings.Secure.ANDROID_ID)} else ""
         val model = if(Build.MODEL != null) {Build.MODEL} else ""
         val manufacturer = if(Build.MANUFACTURER != null) {Build.MANUFACTURER} else ""
         val ver = if(Build.VERSION.RELEASE != null) {Build.VERSION.RELEASE} else ""

         Log.d(TAG, "access: $access")
         Log.d(TAG, "userId: $userId")
         Log.d(TAG, "deviceLabel: $device")
         Log.d(TAG, "deviceHardwareVersion: $ver")
         Log.d(TAG, "deviceManufacturer: $manufacturer")
         Log.d(TAG, "deviceModel: $model")
         Log.d(TAG, "deviceName: $manufacturer")
         Log.d(TAG, "deviceSoftwareVersion: $ver")

         val createDevice = apolloClient.mutation(CreateDeviceMutation(userId = userId, CreateDeviceInput(
            deviceHardwareVersion = ver, deviceLabel = device, deviceManufacturer = manufacturer, deviceModel = model, deviceName = manufacturer, deviceSoftwareVersion = ver
         ))).addHttpHeader(
            "Authorization", "Bearer $access"
         ).execute()

         if(createDevice.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요4.", Toast.LENGTH_SHORT).show()
         }else {
            val createHealth = apolloClient.mutation(CreateHealthMutation(userId = userId)).addHttpHeader(
               "Authorization", "Bearer $access"
            ).execute()

            if(createHealth.data == null){
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요45.", Toast.LENGTH_SHORT).show()
            }else{
               val me2 = apolloClient.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer $access"
               ).execute()

               if(me2.data == null) {
                  Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요5.", Toast.LENGTH_SHORT).show()
               }else {
                  val healthId = me2.data!!.me.user.health!!.healthId
                  val deviceId = me2.data!!.me.user.devices[0].deviceId

                  val createActivity = apolloClient.mutation(CreateActivityMutation(
                     healthId = healthId, deviceId = deviceId,
                     CreateActivityInput(startedAt = LocalDate.now().toString(), endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer $access"
                  ).execute()

                  val createBodyMeasurement = apolloClient.mutation(CreateBodyMeasurementMutation(
                     healthId = me2.data!!.me.user.health!!.healthId, deviceId = me2.data!!.me.user.devices[0].deviceId,
                     CreateBodyMeasurementInput(startedAt = LocalDate.now().toString(), endedAt = LocalDate.now().toString())
                  )).addHttpHeader(
                     "Authorization", "Bearer $access"
                  ).execute()

                  if(createActivity.data == null || createBodyMeasurement.data == null) {
                     Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요6.", Toast.LENGTH_SHORT).show()
                  }else {
                     val me3 = apolloClient.query(MeQuery()).addHttpHeader(
                        "Authorization",
                        "Bearer $access"
                     ).execute()

                     if(me3.data == null) {
                        Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요7.", Toast.LENGTH_SHORT).show()
                     }else {
                        dataManager!!.insertUser(user) // 사용자 정보 저장
                        val getUser = dataManager!!.getUser(user.type!!, user.email!!)

                        if(getUser.id != 0) {
                           MyApp.prefs.setPrefs("userId", getUser.id) // 사용자 고유 Id 저장
                           dataManager!!.insertToken(Token(userId = getUser.id, accessToken = access, refreshToken = refresh, regDate = LocalDate.now().toString())) // 토큰 저장
                           dataManager!!.updateUser(User(id = getUser.id, userId = userId, deviceId = deviceId, healthId = healthId,
                              activityId = me3.data!!.me.user.health!!.activities[0].activityId,
                              bodyMeasurementId = me3.data!!.me.user.health!!.bodyMeasurements[0].bodyMeasurementId)) // 사용자정보 저장
                        }else {
                           Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요8.", Toast.LENGTH_SHORT).show()
                        }
                     }
                  }
               }

               signInDialog()
            }
         }
      }
   }

   private fun signInDialog() {
      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_signup)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

      btnConfirm.setOnClickListener {
         replaceLoginFragment1(requireActivity(), InputInfoFragment())
         dialog.dismiss()
      }

      dialog.show()
   }

   private fun showTermsDialog(title: String, id: Int) {
      val dialog = Dialog(requireActivity())
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.setContentView(R.layout.dialog_terms)

      val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
      val clX = dialog.findViewById<ConstraintLayout>(R.id.clX)
      val terms1 = dialog.findViewById<TextView>(R.id.terms1)
      val terms2 = dialog.findViewById<ConstraintLayout>(R.id.terms2)
      val terms3 = dialog.findViewById<ConstraintLayout>(R.id.terms3)
      val terms4 = dialog.findViewById<TextView>(R.id.terms4)

      tvTitle.text = title

      clX.setOnClickListener {
         dialog.dismiss()
      }

      when(id) {
         1 -> terms1.visibility = View.VISIBLE
         2 -> terms2.visibility = View.VISIBLE
         3 -> terms3.visibility = View.VISIBLE
         4 -> terms4.visibility = View.VISIBLE
      }

      dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.window!!.setGravity(Gravity.BOTTOM)
      dialog.show()
   }
}