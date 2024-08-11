package kr.bodywell.android.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.BTItemAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.model.Constant
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.hideKeyboard
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.view.MainViewModel
import java.io.IOException

class ConnectFragment : Fragment(), BTItemAdapter.Listener {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private lateinit var callback: OnBackPressedCallback
   private lateinit var dataManager: DataManager
   private val viewModel: MainViewModel by activityViewModels()
   private lateinit var btLauncher: ActivityResultLauncher<Intent>
   private lateinit var itemAdapter: BTItemAdapter
   private lateinit var discoveryItemAdapter: BTItemAdapter
   private var bAdapter: BluetoothAdapter? = null

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

      viewModel.msgVM.observe(viewLifecycleOwner, Observer<String> {
         (binding.recyclerView1.adapter as BTItemAdapter).setData(it)
         Log.d(TAG, "msgVM: $it")
      })

      setUpBluetooth()

      return binding.root
   }

   @SuppressLint("MissingPermission")
   private fun setUpBluetooth() {
      /*val f1 = IntentFilter(BluetoothDevice.ACTION_FOUND)
      val f2 = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
      val f3 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
      requireActivity().registerReceiver(bReceiver, f1)
      requireActivity().registerReceiver(bReceiver, f2)
      requireActivity().registerReceiver(bReceiver, f3)*/

      setBluetoothAdapter()

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
         if(bAdapter?.isEnabled == true) {
            bAdapter?.startDiscovery()
//            binding.progressBar2.visibility=View.VISIBLE
         }
      }catch (e: SecurityException) {
         e.printStackTrace()
      }
   }

   private fun setBluetoothAdapter() {
      val bManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
      bAdapter = bManager.adapter
   }

   private fun getPairedDevices() {
      try{
         val list = ArrayList<Bluetooth>()
         val deviceList=bAdapter?.bondedDevices as Set<BluetoothDevice>

         deviceList.forEach {
            list.add(Bluetooth(it, MyApp.prefs.getMacId()==it.address))
         }

         binding.tvStatus1.visibility = if(list.isEmpty()) View.VISIBLE else View.GONE
         binding.clPaired.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE
         itemAdapter.submitList(list)
      }catch(e: SecurityException) {
         e.printStackTrace()
      }
   }

   private val bReceiver = object : BroadcastReceiver() {
      override fun onReceive(p0: Context?, intent: Intent?) {
         when(intent?.action) {
            BluetoothDevice.ACTION_FOUND -> {
               val device = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
               }else {
                  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
               }

               val list = mutableSetOf<Bluetooth>()
               list.addAll(discoveryItemAdapter.currentList)

               try {
                  list.add(Bluetooth(device!!, false))
               }catch(e: IOException) {
                  e.printStackTrace()
               }

               discoveryItemAdapter.submitList(list.toList())

//               binding.progressBar2.visibility = if(list.isEmpty()) View.GONE else View.VISIBLE

               try{
                  Log.d(TAG, "Device: ${device?.name}")
               }catch(e:SecurityException) {
                  e.printStackTrace()
               }
            }
            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> getPairedDevices()
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> binding.progressBar2.visibility = View.GONE
         }
      }
   }

   override fun onClick(device: Bluetooth) {
      MyApp.prefs.setMacId(Constant.BT_PREFS.name, device.device.address)
      if(bAdapter != null) viewModel.btConnect(bAdapter!!)
   }
}