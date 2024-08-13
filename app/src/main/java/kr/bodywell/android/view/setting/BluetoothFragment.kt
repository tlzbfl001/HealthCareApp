package kr.bodywell.android.view.setting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kakao.sdk.talk.TalkApi
import kr.bodywell.android.R
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentBluetoothBinding
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.MyApp
import java.lang.RuntimeException

class BluetoothFragment : Fragment() {
	private var _binding: FragmentBluetoothBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private lateinit var tab1: Tab1
	private lateinit var tab2: Tab2
	private lateinit var tab3: Tab3

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment3(requireActivity(), ConnectFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentBluetoothBinding.inflate(layoutInflater)

		Log.i(TAG, "getMacId: ${MyApp.prefs.getMacId()}")

		return binding.root
	}

//	class MyAdapter(activity: Activity): FragmentStateAdapter(activity) {
//		override fun getItemCount(): Int {
//			return 3
//		}
//
//		override fun createFragment(position: Int): Fragment {
//			return when(position) {
//				0 -> Tab1()
//				1 -> Tab2()
//				2 -> Tab3()
//				else -> throw RuntimeException("Invalid position : $position")
//			}
//		}
//	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}