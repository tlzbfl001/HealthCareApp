package kr.bodywell.health.view.setting

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kr.bodywell.health.databinding.FragmentBluetoothBinding
import kr.bodywell.health.util.CustomUtil.replaceFragment3
import kr.bodywell.health.util.CustomUtil.setStatusBar
import kr.bodywell.health.util.MyApp
import java.lang.RuntimeException

class BluetoothFragment : Fragment() {
	private var _binding: FragmentBluetoothBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment3(requireActivity().supportFragmentManager, ConnectFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentBluetoothBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity().supportFragmentManager, ConnectFragment())
		}

		binding.viewPager.adapter = MyAdapter(requireActivity())
		TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, index ->
			tab.text = when(index) {
				0 -> "CONNECTION"
				1 -> "TERMINAL"
				2 -> "SERVICES"
				else -> throw RuntimeException("Invalid position : $index")
			}
		}.attach()

		binding.tvName.text = MyApp.prefs.getDevice().elementAt(0)

		return binding.root
	}

	class MyAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
		override fun getItemCount(): Int {
			return 3
		}

		override fun createFragment(position: Int): Fragment {
			return when(position) {
				0 -> Tab1Fragment()
				1 -> Tab2Fragment()
				2 -> Tab3Fragment()
				else -> throw RuntimeException("Invalid position : $position")
			}
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}