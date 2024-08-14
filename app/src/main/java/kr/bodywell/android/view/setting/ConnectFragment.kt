package kr.bodywell.android.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
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
import kr.bodywell.android.R
import kr.bodywell.android.adapter.BTItemAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.model.Constant
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectFragment : Fragment(), BTItemAdapter.Listener {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private var bluetoothAdapter: BluetoothAdapter? = null
   private var bluetoothManager: BluetoothManager? = null
   private var gatt: BluetoothGatt? = null
   private var mHandler = Handler()
   private lateinit var itemAdapter: BTItemAdapter
   private lateinit var discoveryItemAdapter: BTItemAdapter
   private lateinit var btLauncher: ActivityResultLauncher<Intent>
   private val list = ArrayList<BluetoothDevice>()

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

      setStatusBar(requireActivity(), binding.mainLayout)

      dataManager = DataManager(activity)
      dataManager.open()

      binding.mainLayout.setOnTouchListener { _, _ ->
         hideKeyboard(requireActivity())
         true
      }

      binding.clBack.setOnClickListener {
         replaceFragment3(requireActivity(), SettingFragment())
      }

      if (!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
         Toast.makeText(requireActivity(), "BLE 미지원", Toast.LENGTH_SHORT).show()
      }else {
         bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
         bluetoothAdapter = bluetoothManager!!.adapter

         val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
         val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
         val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
         requireActivity().registerReceiver(bReceiver, f1)
         requireActivity().registerReceiver(bReceiver, f2)
         requireActivity().registerReceiver(bReceiver, f3)

         binding.recyclerView1.layoutManager = LinearLayoutManager(requireActivity())
         binding.recyclerView2.layoutManager = LinearLayoutManager(requireActivity())
         itemAdapter = BTItemAdapter(this,  false)
         discoveryItemAdapter= BTItemAdapter(this, true)
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
         val list = ArrayList<Bluetooth>()

         if(bluetoothAdapter?.bondedDevices != null) {
            val deviceList=bluetoothAdapter?.bondedDevices as Set<BluetoothDevice>

            deviceList.forEach {
               list.add(Bluetooth(it, MyApp.prefs.getMacId()==it.address))
            }

            binding.recyclerView1.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
            itemAdapter.submitList(list)
         }
      }catch(e: SecurityException) {
         e.printStackTrace()
      }
   }

   private val bReceiver = object : BroadcastReceiver() {
      override fun onReceive(p0: Context?, intent: Intent?) {
         if(intent?.action == BluetoothDevice.ACTION_FOUND) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

            val list = mutableSetOf<Bluetooth>()
            list.addAll(discoveryItemAdapter.currentList)

            try {
               if(device?.name != null) {
                  list.add(Bluetooth(device, false))
               }
            }catch(e: IOException) {
               e.printStackTrace()
            }

            discoveryItemAdapter.submitList(list.toList())
            binding.recyclerView2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
            binding.progressBar2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
         }else if(intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            getPairedDevices()
         }else if(intent?.action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            binding.progressBar2.visibility=View.GONE
         }
      }
   }

   private fun scanLeDevice(enable: Boolean) {
      when(enable) {
         true -> {
            mHandler.postDelayed({
               stopScanBLE()
            }, 5000)  // 5초 이후에 주변 블루투스 장치 스캔 중지시키기
            startScanBLE()
         }
         else -> {
            stopScanBLE()
         }
      }
   }

   private fun startScanBLE(){
      val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
      bluetoothAdapter!!.bluetoothLeScanner.startScan(null, scanSettings, scanCallback)
      binding.progressBar2.visibility=View.VISIBLE
   }

   private fun stopScanBLE(){
      bluetoothAdapter!!.bluetoothLeScanner.stopScan(scanCallback)
      binding.progressBar2.visibility=View.GONE
   }

   private val scanCallback = object : ScanCallback() {
      override fun onScanResult(callbackType: Int, result: ScanResult?) {
         super.onScanResult(callbackType, result)

         val device: BluetoothDevice? = result?.device
         val deviceName: String = device?.name ?: ""
         val deviceAddress: String = device?.address ?: "not address"

         result?.let {
            if(!list.contains(it.device) && it.device.name!=null) {
               list.add(it.device)
            }
         }

         if(result!!.device.name == "BODYWELL_MEDICINE") {
//            connect(deviceAddress)
         }
      }
   }

   fun connect(address: String) {
      val device = bluetoothAdapter!!.getRemoteDevice(address)
      if (device == null) {
         Log.e(TAG, "Device not found.")
         return
      }
      gatt = device.connectGatt(requireActivity(), false, btGattCallback)
   }

   private val btGattCallback = object: BluetoothGattCallback() {
      override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
         super.onConnectionStateChange(gatt, status, newState)
         if (newState == BluetoothGatt.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.")
            gatt.discoverServices()
         } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            Log.i(TAG, "Disconnected from GATT server.")
         }
      }

      override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
         super.onServicesDiscovered(gatt, status)
         if (status == BluetoothGatt.GATT_SUCCESS) {
            startReceivingPasswordUpdates()
         }
      }

      override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
         super.onCharacteristicChanged(gatt, characteristic)
         Log.i(TAG, characteristic.value.contentToString())
      }

      override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
         super.onCharacteristicWrite(gatt, characteristic, status)
         if (characteristic.uuid == UUID.fromString(resources.getString(R.string.characteristicUUID))) {
            Log.i(TAG, "Write status: $status")
         }
      }

      override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
         super.onCharacteristicRead(gatt, characteristic, status)
         if (characteristic.uuid == UUID.fromString(resources.getString(R.string.characteristicUUID))) {
            Log.i(TAG, String(characteristic.value))
         }
      }
   }

   fun startReceivingPasswordUpdates() {
      val service = gatt?.getService(UUID.fromString(resources.getString(R.string.serviceUUID)))
      val characteristic = service?.getCharacteristic(UUID.fromString(resources.getString(R.string.characteristicUUID)))
      if (characteristic != null) {
         gatt?.setCharacteristicNotification(characteristic, true)
         val desc = characteristic.getDescriptor(UUID.fromString(resources.getString(R.string.cccdUUID)))
         desc?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
         gatt?.writeDescriptor(desc)
      }
   }

   private fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID) {
      val service = gatt?.getService(serviceUUID)
      val characteristic = service?.getCharacteristic(characteristicUUID)

      if(characteristic != null) {
         val success = gatt?.readCharacteristic(characteristic)
         Log.i(TAG, "Read status: $success")
      }
   }

   private fun writeCharacteristic(msg: String) {
      val service = gatt?.getService(UUID.fromString(resources.getString(R.string.serviceUUID)))
      val characteristic = service?.getCharacteristic(UUID.fromString(resources.getString(R.string.characteristicUUID)))

      if (characteristic != null) {
         characteristic.value = msg.toByteArray()
         val success = gatt?.writeCharacteristic(characteristic)
         Log.i(TAG, "Read status: $success")
      }
   }

   override fun onClick(device: Bluetooth) {
      MyApp.prefs.setMacId(Constant.BT_PREFS.name, device.device.address)
      replaceFragment1(requireActivity(), BluetoothFragment())
   }

   override fun onDetach() {
      super.onDetach()
      callback.remove()
   }
}