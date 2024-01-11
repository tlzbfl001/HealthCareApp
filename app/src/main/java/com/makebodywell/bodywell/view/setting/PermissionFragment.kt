package com.makebodywell.bodywell.view.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.databinding.FragmentPermissionBinding
import com.makebodywell.bodywell.databinding.FragmentSettingBinding
import com.makebodywell.bodywell.util.CustomUtil
import com.makebodywell.bodywell.view.home.food.FoodFragment

class PermissionFragment : Fragment() {
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(layoutInflater)

        binding.ivBack.setOnClickListener {
            CustomUtil.replaceFragment1(requireActivity(), SettingFragment())
        }

        binding.cvConfirm.setOnClickListener {
            val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                Uri.parse("package:" + requireActivity().packageName)
            )
            startActivity(intent)
        }

        return binding.root
    }
}