package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.bodywell.android.CreateUserAppleMutation
import com.bodywell.android.type.CreateAppleOauthInput
import com.makebodywell.bodywell.databinding.FragmentInputTermsBinding
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceInputFragment
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class InputTermsFragment : Fragment() {
   private var _binding: FragmentInputTermsBinding? = null
   private val binding get() = _binding!!

   private var kakaoIdToken = ""
   private var googleType = ""
   private var googleName = ""
   private var googleEmail = ""
   private var googleIdToken = ""
   private var appleIdToken = ""

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentInputTermsBinding.inflate(layoutInflater)

      initView()
      loginInfo()
      setupApollo()

      return binding.root
   }

   private fun initView() {
      binding.ivBack.setOnClickListener {
         startActivity(Intent(activity, LoginActivity::class.java))
      }
      binding.cvContinue.setOnClickListener {
         replaceInputFragment(requireActivity(), InputInfoFragment())
      }
      binding.cbAll.setOnCheckedChangeListener { _, isChecked ->
         if(isChecked) {
            binding.cb1.isChecked = true
            binding.cb2.isChecked = true
            binding.cb3.isChecked = true
            binding.cb4.isChecked = true
         }else {
            binding.cb1.isChecked = false
            binding.cb2.isChecked = false
            binding.cb3.isChecked = false
            binding.cb4.isChecked = false
         }
      }
   }

   private fun loginInfo() {
      val extrasData = requireActivity().intent.extras
      if (extrasData != null) {
         // 카카오 로그인
         if(extrasData.getString("kakaoIdToken") != null) {
            UserApiClient.instance.me { user, error ->
               if(error != null) {
                  Log.e(TAG, "$error")
               }else if(user != null) {
                  Log.d(TAG, "user: ${user.kakaoAccount.toString()}")
                  kakaoIdToken = extrasData.getString("kakaoIdToken")!!
                  Log.d(TAG, "kakaoIdToken: $kakaoIdToken")
               }
            }
         }

         // 네이버 로그인
         if(extrasData.getString("naverAccessToken") != null) {
            Log.d(TAG, "naverAccessToken: ${extrasData.getString("naverAccessToken")}")
            Log.d(TAG, "naverEmail: ${extrasData.getString("naverEmail")}")
            Log.d(TAG, "naverName: ${extrasData.getString("naverName")}")
            Log.d(TAG, "naverNickname: ${extrasData.getString("naverNickname")}")
            Log.d(TAG, "naverGender: ${extrasData.getString("naverGender")}")
            Log.d(TAG, "naverBirthYear: ${extrasData.getString("naverBirthYear")}")
            Log.d(TAG, "naverBirthDay: ${extrasData.getString("naverBirthDay")}")
            Log.d(TAG, "naverProfileImage: ${extrasData.getString("naverProfileImage")}")
         }

         // 구글 로그인
         if(extrasData.getString("googleType") != null) {
            googleType = extrasData.getString("googleType").toString()
            googleName = extrasData.getString("googleName").toString()
            googleEmail = extrasData.getString("googleEmail").toString()
            googleIdToken = extrasData.getString("googleIdToken").toString()
         }

         // 애플 로그인
         if(extrasData.getString("appleIdToken") != null) {
            appleIdToken = extrasData.getString("appleIdToken").toString()
            Log.d(TAG, "appleIdToken: $appleIdToken")
         }
      }
   }

   private fun setupApollo() {
      val apolloClient = ApolloClient.Builder()
         .serverUrl("https://api.bodywell.dev/graphql")
         .build()

      // CreateUserGoogle
//      lifecycleScope.launch{
//         val response = apolloClient.mutation(CreateUserGoogleMutation(CreateGoogleOauthInput(
//            idToken = googleIdToken
//         ))).execute()
//
//         Log.d(TAG, "CreateUserGoogle: ${response.data?.createUserGoogle}")
//      }

//      lifecycleScope.launch{
//         // LoginUserGoogle
//         val response1 = apolloClient.mutation(LoginUserGoogleMutation(LoginGoogleOauthInput(
//            idToken = googleIdToken
//         ))).execute()
//         Log.d(TAG, "LoginUserGoogle: ${response1.data?.loginUserGoogle}")
//
//         // Me
//         val result = apolloClient.query(MeQuery()).addHttpHeader(
//            "Authorization",
//            "Bearer ${response1.data?.loginUserGoogle?.accessToken}"
//         ).execute()
//         Log.d(TAG, "Me: ${result.data?.me}")
//      }

//      // CreateUserGoogle
//      lifecycleScope.launch{
//         val response = apolloClient.mutation(CreateUserAppleMutation(CreateAppleOauthInput(
//            idToken = appleIdToken
//         ))).execute()
//
//         Log.d(TAG, "createUserApple: ${response.data?.createUserApple}")
//      }
   }
}