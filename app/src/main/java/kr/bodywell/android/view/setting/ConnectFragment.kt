package kr.bodywell.android.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.BTItemAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.util.BluetoothUtil
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.hideKeyboard
import kr.bodywell.android.util.CustomUtil.Companion.replaceFragment3
import kr.bodywell.android.view.MainViewModel
import java.io.IOException

class ConnectFragment : Fragment(), BTItemAdapter.Listener {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val viewModel: MainViewModel by activityViewModels()

   private var pref: SharedPreferences? = null
   private var bAdapter: BluetoothAdapter?=null
   private lateinit var btLauncher: ActivityResultLauncher<Intent>
   private lateinit var itemAdapter: BTItemAdapter
   private lateinit var discoveryItemAdapter: BTItemAdapter

   override fun onAttach(context: Context) {
      super.onAttach(context)
      callback = object : OnBackPressedCallback(true) {
         override fun handleOnBackPressed() {
            replaceFragment3(requireActivity(), SettingFragment())
         }
      }

      requireActivity().onBackPressedDispatcher.addCallback(this, callback)
   }

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentConnectBinding.inflate(layoutInflater)

      requireActivity().window?.apply {
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
         statusBarColor = Color.TRANSPARENT
         navigationBarColor = Color.BLACK

         val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
         val statusBarHeight = if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else { 0 }
         binding.mainLayout.setPadding(0, statusBarHeight, 0, 0)
      }

      dataManager = DataManager(activity)
      dataManager.open()

      pref = requireActivity().getSharedPreferences(BluetoothUtil.PREFERENCES, Context.MODE_PRIVATE)

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), SettingFragment())
      }

      viewModel.msgVM.observe(viewLifecycleOwner, Observer<String> {
         (binding.recyclerView1.adapter as BTItemAdapter).setData(it)
         Log.d(TAG, "msgVM: $it")
      })

      settingBluetooth()

      return binding.root
   }

   @SuppressLint("MissingPermission")
   private fun settingBluetooth() {
      val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
      val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
      val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
      requireActivity().registerReceiver(bReceiver, f1)
      requireActivity().registerReceiver(bReceiver, f2)
      requireActivity().registerReceiver(bReceiver, f3)

      binding.recyclerView1.layoutManager = LinearLayoutManager(requireActivity())
      binding.recyclerView2.layoutManager = LinearLayoutManager(requireActivity())
      itemAdapter = BTItemAdapter(requireActivity(),this,  false)
      discoveryItemAdapter= BTItemAdapter(requireActivity(), this, true)
      binding.recyclerView1.adapter=itemAdapter
      binding.recyclerView2.adapter=discoveryItemAdapter

      btLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
         if(it.resultCode == Activity.RESULT_OK) {
            getPairedDevices() // 연결된 기기 가져오기
         }else {
            Log.d(TAG, "기기 가져오기 실패")
         }
      }

      btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

      val bManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
      bAdapter = bManager.adapter

      try {
         if(bAdapter?.isEnabled == true) {
            bAdapter?.startDiscovery()
            binding.progressBar2.visibility=View.VISIBLE
         }
      }catch (e: SecurityException) {
         e.printStackTrace()
      }
   }

   private fun getPairedDevices() {
      try{
         val list = ArrayList<Bluetooth>()

         if(bAdapter?.bondedDevices != null) {
            val deviceList=bAdapter?.bondedDevices as Set<BluetoothDevice>

            // pref 에 저장된 주소가 리스트값과 같은경우 리스트에 데이터 저장
            deviceList.forEach {
               list.add(Bluetooth(it, pref?.getString(BluetoothUtil.MAC,"")==it.address))
            }

            binding.tvStatus1.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
            itemAdapter.submitList(list)
         }
      }catch(e: SecurityException) {
         e.printStackTrace()
      }
   }

   private val bReceiver = object : BroadcastReceiver() {
      @SuppressLint("MissingPermission")
      override fun onReceive(p0: Context?, intent: Intent?) {
         if(intent?.action == BluetoothDevice.ACTION_FOUND) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

            val list = mutableSetOf<Bluetooth>()
            list.addAll(discoveryItemAdapter.currentList)

            try {
               list.add(Bluetooth(device!!, false))
            }catch(e: IOException) {
               e.printStackTrace()
            }

            discoveryItemAdapter.submitList(list.toList())

            binding.progressBar2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE

            try{
               Log.d(TAG, "Device: ${device?.name}")
            }catch(e:SecurityException) {
               e.printStackTrace()
            }
         }else if(intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            getPairedDevices()
         }else if(intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            binding.progressBar2.visibility=View.GONE
         }
      }
   }

   override fun onClick(device: Bluetooth) {
      val editor=pref?.edit()
      editor?.putString(BluetoothUtil.MAC, device.device.address)
      editor?.apply()
      viewModel.connect(bAdapter!!)
   }
}