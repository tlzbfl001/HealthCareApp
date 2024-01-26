package com.makebodywell.bodywell.view.init

import android.app.Dialog
import android.content.Intent
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
import com.makebodywell.bodywell.CreateUserAppleMutation
import com.makebodywell.bodywell.CreateUserGoogleMutation
import com.makebodywell.bodywell.CreateUserKakaoMutation
import com.makebodywell.bodywell.CreateUserNaverMutation
import com.makebodywell.bodywell.LoginUserAppleMutation
import com.makebodywell.bodywell.LoginUserGoogleMutation
import com.makebodywell.bodywell.LoginUserKakaoMutation
import com.makebodywell.bodywell.LoginUserNaverMutation
import com.makebodywell.bodywell.MeQuery
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DBHelper
import com.makebodywell.bodywell.database.DBHelper.Companion.TABLE_USER
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentInputTermsBinding
import com.makebodywell.bodywell.model.Token
import com.makebodywell.bodywell.model.User
import com.makebodywell.bodywell.type.CreateAppleOauthInput
import com.makebodywell.bodywell.type.CreateGoogleOauthInput
import com.makebodywell.bodywell.type.CreateKakaoOauthInput
import com.makebodywell.bodywell.type.CreateNaverOauthInput
import com.makebodywell.bodywell.type.LoginAppleOauthInput
import com.makebodywell.bodywell.type.LoginGoogleOauthInput
import com.makebodywell.bodywell.type.LoginKakaoOauthInput
import com.makebodywell.bodywell.type.LoginNaverOauthInput
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment2
import com.navercorp.nid.NaverIdLoginSDK
import kotlinx.coroutines.launch
import java.time.LocalDate

class InputTermsFragment : Fragment() {
   private var _binding: FragmentInputTermsBinding? = null
   private val binding get() = _binding!!

   private val bundle = Bundle()
   private var dataManager: DataManager? = null
   private var apolloClient: ApolloClient? = null

   private var isAll = true

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputTermsBinding.inflate(layoutInflater)

      dataManager = DataManager(requireActivity())
      dataManager!!.open()

      apolloClient = ApolloClient.Builder().serverUrl("https://api.bodywell.dev/graphql").build()

      binding.ivBack.setOnClickListener {
         startActivity(Intent(activity, LoginActivity::class.java))
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
         showBottomDialog("서비스 이용 약관 동의", 1)
      }

      binding.tvView2.setOnClickListener {
         showBottomDialog("개인정보처리방침 동의", 2)
      }

      binding.tvView3.setOnClickListener {
         showBottomDialog("민감정보 수집 및 이용 동의", 3)
      }

      binding.tvView4.setOnClickListener {
         showBottomDialog("마케팅 수신 동의", 4)
      }

      binding.cvContinue.setOnClickListener {
         if(binding.cb1.isChecked && binding.cb2.isChecked && binding.cb3.isChecked) {
            val user = arguments?.getParcelable<User>("user")!!
            when(user.type) {
               "google" -> googleSignIn(user)
               "naver" -> naverSignIn(user)
               "kakao" -> kakaoSignIn(user)
               "apple" -> appleSignIn(user)
            }
         }else {
            Toast.makeText(requireActivity(), "필수 이용약관에 체크해주세요.", Toast.LENGTH_SHORT).show()
         }
      }

      return binding.root
   }

   private fun googleSignIn(user: User) {
      dataManager!!.insertUser(user)

      lifecycleScope.launch{
         // 서버 회원가입
         val response = apolloClient!!.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()
      }

      lifecycleScope.launch{
         // 서버 로그인
         val response = apolloClient!!.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         if(response.data!!.loginUserGoogle.success) {
            val getUser = dataManager!!.getUser()

            // 로컬 DB 에 토큰 저장
            dataManager!!.insertToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserGoogle.accessToken.toString(),
               refreshToken = response.data!!.loginUserGoogle.refreshToken.toString(), regDate = LocalDate.now().toString()))

            val response2 = apolloClient!!.query(MeQuery()).addHttpHeader(
               "Authorization",
               "Bearer ${response.data!!.loginUserGoogle.accessToken.toString()}"
            ).execute()

            // 로컬 DB 에 userId 저장
            if(response2.data!!.me.user.userId != "") {
               dataManager?.updateString(TABLE_USER, "userId", response2.data!!.me.user.userId, getUser.id)
               signInDialog()
            }else {
               Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
            }
         }else {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun naverSignIn(user: User) {
      lifecycleScope.launch{
         val response = apolloClient!!.mutation(CreateUserNaverMutation(CreateNaverOauthInput(
            accessToken = NaverIdLoginSDK.getAccessToken().toString()
         ))).execute()
      }

      lifecycleScope.launch{
         val response = apolloClient!!.mutation(LoginUserNaverMutation(LoginNaverOauthInput(
            accessToken = user.idToken.toString()
         ))).execute()

         if(response.data!!.loginUserNaver.success) {
            val getUser = dataManager!!.getUser()
            dataManager!!.insertToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserNaver.accessToken.toString(),
               refreshToken = response.data!!.loginUserNaver.refreshToken.toString(), regDate = LocalDate.now().toString()))

            signInDialog()
         }else {
            Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
         }
      }
   }

   private fun kakaoSignIn(user: User) {
      dataManager!!.insertUser(User(type = "kakao", idToken = user.idToken, email = user.email, name = user.name, nickname = user.nickname,
         profileImage = user.profileImage, regDate = LocalDate.now().toString()))

      lifecycleScope.launch{
         val response = apolloClient!!.mutation(CreateUserKakaoMutation(CreateKakaoOauthInput(
            idToken = user.idToken.toString()
         ))).execute()
      }

      var loginSuccess = false

      lifecycleScope.launch{
         val response = apolloClient!!.mutation(LoginUserKakaoMutation(LoginKakaoOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         loginSuccess = if(response.data!!.loginUserKakao.success) {
            val getUser = dataManager!!.getUser()
            dataManager!!.insertToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserKakao.accessToken.toString(),
               refreshToken = response.data!!.loginUserKakao.refreshToken.toString(), regDate = LocalDate.now().toString()))
            true
         }else false
      }

      if(loginSuccess) {
         replaceInputFragment(requireActivity(), InputInfoFragment())
      }else {
         Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
      }
   }

   private fun appleSignIn(user: User) {
      dataManager!!.insertUser(User(type = "apple", idToken = user.idToken, email = user.email, regDate = LocalDate.now().toString()))

      lifecycleScope.launch{
         val response = apolloClient!!.mutation(CreateUserAppleMutation(CreateAppleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()
      }

      var loginSuccess = false

      lifecycleScope.launch{
         val response = apolloClient!!.mutation(LoginUserAppleMutation(LoginAppleOauthInput(
            idToken = user.idToken.toString()
         ))).execute()

         loginSuccess = if(response.data!!.loginUserApple.success) {
            val getUser = dataManager!!.getUser()
            dataManager!!.updateToken(Token(userId = getUser.id, accessToken = response.data!!.loginUserApple.accessToken.toString(),
               refreshToken = response.data!!.loginUserApple.refreshToken.toString(), regDate = LocalDate.now().toString()))
            true
         }else false
      }

      if(loginSuccess) {
         replaceInputFragment(requireActivity(), InputInfoFragment())
      }else {
         Toast.makeText(requireActivity(), "오류가 발생하였습니다. 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
      }
   }

   private fun signInDialog() {
      val getUser = dataManager!!.getUser()

      val dialog = Dialog(requireActivity())
      dialog.setContentView(R.layout.dialog_signup)
      dialog.setCancelable(false)
      dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      val btnConfirm = dialog.findViewById<TextView>(R.id.btnConfirm)

      btnConfirm.setOnClickListener {
         bundle.putParcelable("user", getUser)
         replaceInputFragment2(requireActivity(), InputInfoFragment(), bundle)
         dialog.dismiss()
      }

      dialog.show()
   }

   private fun showBottomDialog(title: String, id: Int) {
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