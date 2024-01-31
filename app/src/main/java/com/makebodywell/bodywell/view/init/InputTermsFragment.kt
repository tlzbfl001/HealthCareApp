package com.makebodywell.bodywell.view.init

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.apollographql.apollo3.ApolloClient
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
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceLoginFragment1
import com.makebodywell.bodywell.util.MyApp
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.launch
import java.time.LocalDate

class InputTermsFragment : Fragment() {
   private var _binding: FragmentInputTermsBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null
   private var apolloClient: ApolloClient? = null
   private var user = User()
   private var isAll = true

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputTermsBinding.inflate(layoutInflater)

      dataManager = DataManager(requireActivity())
      dataManager!!.open()

      apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      user = arguments?.getParcelable("user")!!
      Log.d(TAG, "InputTermsFragment user: $user")

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
         // 회원가입
         val signIn = apolloClient!!.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 사용자정보 저장
            dataManager!!.insertUser(user)

            // 로그인
            val login = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            Log.d(TAG, "loginUserGoogle: ${login.data!!.loginUserGoogle}")

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               val getUser = dataManager!!.getUser(user.type.toString(), user.email.toString())

               MyApp.prefs.setPrefs("userId", getUser.id)

               // 토큰 저장
               dataManager!!.insertToken(Token(userId = getUser.id, accessToken = login.data!!.loginUserGoogle.accessToken.toString(),
                  refreshToken = login.data!!.loginUserGoogle.refreshToken.toString(), regDate = LocalDate.now().toString()))

               val getUserId = apolloClient!!.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer ${login.data!!.loginUserGoogle.accessToken.toString()}"
               ).execute()

               Log.d(TAG, "userId: ${getUserId.data!!.me.user.userId}")

               // userId 저장
               if(getUserId.data != null) {
                  dataManager?.updateString(TABLE_USER, "userId", getUserId.data!!.me.user.userId, getUser.id)
                  signInDialog()
               }
            }
         }
      }
   }

   private fun naverSignIn() {
      lifecycleScope.launch{
         // 회원가입
         val signIn = apolloClient!!.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
            accessToken = NaverIdLoginSDK.getAccessToken().toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 사용자정보 저장
            dataManager!!.insertUser(user)

            // 로그인
            val login = apolloClient!!.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
               accessToken = user.idToken.toString()
            ))).execute()

            Log.d(TAG, "loginUserNaver: ${login.data!!.loginUserNaver}")

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               val getUser = dataManager!!.getUser(user.type.toString(), user.email.toString())

               MyApp.prefs.setPrefs("userId", getUser.id)

               // 토큰 저장
               dataManager!!.insertToken(Token(userId = getUser.id, accessToken = login.data!!.loginUserNaver.accessToken.toString(),
                  refreshToken = login.data!!.loginUserNaver.refreshToken.toString(), regDate = LocalDate.now().toString()))

               val getUserId = apolloClient!!.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer ${login.data!!.loginUserNaver.accessToken.toString()}"
               ).execute()

               Log.d(TAG, "userId: ${getUserId.data!!.me.user.userId}")

               // userId 저장
               if(getUserId.data != null) {
                  dataManager?.updateString(TABLE_USER, "userId", getUserId.data!!.me.user.userId, getUser.id)
                  signInDialog()
               }
            }
         }
      }
   }

   private fun kakaoSignIn() {
      lifecycleScope.launch{
         // 회원가입
         val signIn = apolloClient!!.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         if(signIn.data == null) {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }else {
            // 사용자정보 저장
            dataManager!!.insertUser(user)

            // 로그인
            val login = apolloClient!!.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
               idToken = user.idToken.toString()
            ))).execute()

            Log.d(TAG, "loginUserKakao: ${login.data!!.loginUserKakao}")

            if(login.data == null) {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }else {
               val getUser = dataManager!!.getUser(user.type.toString(), user.email.toString())

               MyApp.prefs.setPrefs("userId", getUser.id)

               // DB 에 토큰 저장
               dataManager!!.insertToken(Token(userId = getUser.id, accessToken = login.data!!.loginUserKakao.accessToken.toString(),
                  refreshToken = login.data!!.loginUserKakao.refreshToken.toString(), regDate = LocalDate.now().toString()))

               val getUserId = apolloClient!!.query(MeQuery()).addHttpHeader(
                  "Authorization",
                  "Bearer ${login.data!!.loginUserKakao.accessToken.toString()}"
               ).execute()

               Log.d(TAG, "userId: ${getUserId.data!!.me.user.userId}")

               // userId 저장
               if(getUserId.data != null) {
                  dataManager?.updateString(TABLE_USER, "userId", getUserId.data!!.me.user.userId, getUser.id)
                  signInDialog()
               }
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

      dialog.show()
      dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.window!!.setGravity(Gravity.BOTTOM)
   }
}