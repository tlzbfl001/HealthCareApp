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
import kr.bodywell.android.adapter.BTItemAdapter
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp
import java.io.IOException


@SuppressLint("MissingPermission")
class ConnectFragment : Fragment(), BTItemAdapter.Listener {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private var bluetoothAdapter: BluetoothAdapter? = null
   private var bluetoothManager: BluetoothManager? = null
   private lateinit var itemAdapter: BTItemAdapter
   private lateinit var discoveryItemAdapter: BTItemAdapter
   private lateinit var btLauncher: ActivityResultLauncher<Intent>

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
               list.add(Bluetooth(it))
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
         when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
               val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

               val list = mutableSetOf<Bluetooth>()
               list.addAll(discoveryItemAdapter.currentList)

               try {
                  if(device?.name != null) {
                     list.add(Bluetooth(device))
                  }
               }catch(e: IOException) {
                  e.printStackTrace()
               }

               discoveryItemAdapter.submitList(list.toList())
               binding.recyclerView2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
               binding.progressBar2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> getPairedDevices()
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> binding.progressBar2.visibility=View.GONE
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