package kr.bodywell.android.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.BluetoothItemAdapter
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp

@SuppressLint("MissingPermission")
class ConnectFragment : Fragment(), BluetoothItemAdapter.Listener {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bluetoothManager: BluetoothManager? = null
   private var bluetoothAdapter: BluetoothAdapter? = null
   private lateinit var itemAdapter: BluetoothItemAdapter
   private lateinit var discoveryItemAdapter: BluetoothItemAdapter
   private lateinit var btLauncher: ActivityResultLauncher<Intent>
   private val pairedList = mutableListOf<Bluetooth>()
   val discoveryList = mutableListOf<Bluetooth>()

   companion object {
      const val DEVICE_NAME = "BODYWELL_MEDICINE"
   }

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
      _binding = FragmentConnectBinding.inflate(layoutInflater)

      setStatusBar(requireActivity(), binding.mainLayout)

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity().supportFragmentManager, SettingFragment())
      }

      if(!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
         Toast.makeText(requireActivity(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
      }else {
         bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
         bluetoothAdapter = bluetoothManager!!.adapter
         val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner

         val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()

         val filters: MutableList<ScanFilter> = ArrayList()
         val scanFilter = ScanFilter.Builder()
            .setDeviceName(DEVICE_NAME)
            .build()
         filters.add(scanFilter)

         bluetoothLeScanner.startScan(filters, scanSettings, scanCallback)

         val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
         val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
         val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
         requireActivity().registerReceiver(bReceiver, f1)
         requireActivity().registerReceiver(bReceiver, f2)
         requireActivity().registerReceiver(bReceiver, f3)

         binding.recyclerView1.layoutManager = LinearLayoutManager(requireActivity())
         binding.recyclerView2.layoutManager = LinearLayoutManager(requireActivity())
         itemAdapter = BluetoothItemAdapter(this,  false, pairedList)
         discoveryItemAdapter = BluetoothItemAdapter(this, true, discoveryList)
         binding.recyclerView1.adapter=itemAdapter
         binding.recyclerView2.adapter=discoveryItemAdapter

         // 연결된 기기 가져오기
         btLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
               getPairedDevices()
            }else {
//               Log.d(TAG, "기기 가져오기 실패")
            }
         }

         btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

         try {
            if(bluetoothAdapter?.isEnabled == true) {
               bluetoothAdapter?.startDiscovery()
            }
         }catch (e: SecurityException) {
            e.printStackTrace()
         }
      }

      return binding.root
   }

   private fun getPairedDevices() {
      try{
         if(bluetoothAdapter?.bondedDevices != null) {
            val deviceList=bluetoothAdapter?.bondedDevices as Set<BluetoothDevice>

            deviceList.forEach {
               pairedList.add(Bluetooth(it))
               discoveryList.remove(Bluetooth(it))
            }

            binding.recyclerView1.visibility = if(pairedList.isEmpty()) View.GONE else View.VISIBLE
            binding.recyclerView2.visibility = if(discoveryList.isEmpty()) View.GONE else View.VISIBLE
            binding.progressBar.visibility = if(discoveryList.isEmpty()) View.GONE else View.VISIBLE
            itemAdapter.notifyDataSetChanged()
            discoveryItemAdapter.notifyDataSetChanged()
         }
      }catch(e: SecurityException) {
         e.printStackTrace()
      }
   }

   private val scanCallback: ScanCallback = object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult) {
         if(result.device.name != null) {
            var uuid = "null"

            if(result.scanRecord?.serviceUuids != null) {
               uuid = result.scanRecord!!.serviceUuids.toString()
            }
            addScanResult(result)
         }
      }

      override fun onScanFailed(errorCode: Int) {
         Log.e(TAG, "onScanFailed: $errorCode")
      }

      private fun addScanResult(result: ScanResult) {
         // 중복 체크
         for(dev in discoveryList) {
            if (dev.device.address == result.device.address) return
         }

         if(result.device.bondState != 12) discoveryList.add(Bluetooth(result.device))

         discoveryItemAdapter.notifyDataSetChanged()
         binding.recyclerView2.visibility = if(discoveryList.isEmpty()) View.GONE else View.VISIBLE
         binding.progressBar.visibility = if(discoveryList.isEmpty()) View.GONE else View.VISIBLE
      }
   }

   private val bReceiver = object : BroadcastReceiver() {
      override fun onReceive(p0: Context?, intent: Intent?) {
         when(intent?.action) {
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> getPairedDevices()
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> binding.progressBar.visibility=View.GONE
         }
      }
   }

   override fun onClick(device: Bluetooth) {
      val sets = HashSet<String>()
      sets.add(device.device.name)
      sets.add(device.device.address)
      MyApp.prefs.setDevice(sets)
      replaceFragment1(requireActivity().supportFragmentManager, BluetoothFragment())
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}