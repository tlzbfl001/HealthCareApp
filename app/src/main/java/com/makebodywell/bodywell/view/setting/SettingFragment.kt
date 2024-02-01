package com.makebodywell.bodywell.view.setting

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.kakao.sdk.user.UserApiClient
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentSettingBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.util.CustomUtil.Companion.TAG
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment1
import com.makebodywell.bodywell.util.MyApp
import com.makebodywell.bodywell.view.init.LoginActivity
import com.navercorp.nid.NaverIdLoginSDK

class SettingFragment : Fragment() {
   private var _binding: FragmentSettingBinding? = null
   private val binding get() = _binding!!

   private var dataManager: DataManager? = null

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentSettingBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.GRAY
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.cl1.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager!!.open()

      binding.tvConnect.setOnClickListener {
         replaceFragment1(requireActivity(), ConnectFragment())
      }

      binding.tvLogout.setOnClickListener {
         val dialog = AlertDialog.Builder(context)
            .setMessage("정말 로그아웃하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
               val getUser = dataManager!!.getUser(MyApp.prefs.getId())

               when(getUser.type) {
                  "google" -> {
                     val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
                     if(account != null) {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                           .requestIdToken(getString(R.string.googleWebClientId))
                           .requestEmail()
                           .build()
                        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

                        gsc.signOut().addOnCompleteListener {
                           if (it.isSuccessful) {
                              MyApp.prefs.removePrefs("userId")

                              Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                              startActivity(Intent(requireActivity(), LoginActivity::class.java))
                           } else {
                              Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                           }
                        }
                     }
                  }
                  "naver" -> {
                     NaverIdLoginSDK.initialize(requireActivity(), getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(
                        R.string.app_name))
                     NaverIdLoginSDK.logout()

                     MyApp.prefs.removePrefs("userId")

                     Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                     startActivity(Intent(requireActivity(), LoginActivity::class.java))
                  }
                  "kakao" -> {
                     UserApiClient.instance.logout { error ->
                        if (error != null) {
                           Toast.makeText(requireActivity(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                        }else {
                           MyApp.prefs.removePrefs("userId")

                           Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                           startActivity(Intent(requireActivity(), LoginActivity::class.java))
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
}