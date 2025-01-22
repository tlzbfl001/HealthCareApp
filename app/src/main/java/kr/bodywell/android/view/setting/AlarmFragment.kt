package kr.bodywell.android.view.setting

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import kr.bodywell.android.databinding.FragmentAlarmBinding
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2

class AlarmFragment : Fragment() {
	private var _binding: FragmentAlarmBinding? = null
	val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentAlarmBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
		}

		return binding.root
	}

	override fun onResume() {
		super.onResume()

		if(checkAlarmPermission1(requireActivity())) {
			binding.tvPerm1.text = "켜짐"
			binding.tvPerm1.setTextColor(Color.parseColor("#A38FF1"))
		}else {
			binding.tvPerm1.text = "꺼짐"
			binding.tvPerm1.setTextColor(Color.parseColor("#CCCCCC"))
		}

		binding.btn1.setOnClickListener {
			val intent = Intent()
			intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
			intent.putExtra("android.provider.extra.APP_PACKAGE", requireActivity().packageName)
			startActivity(intent)
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			if(checkAlarmPermission2(requireActivity())) {
				binding.tvPerm2.text = "켜짐"
				binding.tvPerm2.setTextColor(Color.parseColor("#A38FF1"))
			}else {
				binding.tvPerm2.text = "꺼짐"
				binding.tvPerm2.setTextColor(Color.parseColor("#CCCCCC"))
			}

			binding.btn2.setOnClickListener {
				val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
				startActivity(intent)
			}
		}else {
			binding.btn2.visibility = View.GONE
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}