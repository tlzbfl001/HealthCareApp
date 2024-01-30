package com.makebodywell.bodywell.view.setting

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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

                  }
                  "naver" -> {
                     NaverIdLoginSDK.initialize(requireActivity(), getString(R.string.naverClientId), getString(R.string.naverClientSecret), getString(
                        R.string.app_name))
                     Log.d(TAG, "NaverIdLoginSDK: ${NaverIdLoginSDK.getAccessToken()}")
                     NaverIdLoginSDK.logout()
                  }
                  "kakao" -> {

                  }
               }

               MyApp.prefs.removePrefs("userId")
               Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

               startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }
            .setNegativeButton("취소", null)
            .create()
         dialog.show()
      }

      return binding.root
   }
}