package kr.bodywell.android.view.setting

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kr.bodywell.android.adapter.BtListAdapter
import kr.bodywell.android.databinding.FragmentConnectBinding
import kr.bodywell.android.model.Bluetooth
import kr.bodywell.android.util.PermissionUtil.Companion.BT_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.Companion.BT_PERMISSION_2

class ConnectFragment : Fragment() {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private var bluetoothAdapter: BluetoothAdapter? = null
   private var btListAdapter: BtListAdapter? = null
   private val btList = ArrayList<Bluetooth>()

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentConnectBinding.inflate(layoutInflater)

      if (requestPermission()){
         // 블루투스 설정
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
         getPairedDevices()
      }

      return binding.root
   }

   private fun getPairedDevices() {
      bluetoothAdapter?.let {
         if(it.isEnabled) {
            btList.clear()
            val pairedDevices = bluetoothAdapter!!.bondedDevices
            if(pairedDevices.isNotEmpty()) {
               pairedDevices.forEach { device ->
                  btList.add(Bluetooth(device.name, ""))
               }
               btListAdapter = BtListAdapter(btList)
               btListAdapter!!.notifyDataSetChanged()
               binding.rvPairedDevices.visibility = View.VISIBLE
               binding.rvPairedDevices.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
               binding.rvPairedDevices.adapter = btListAdapter
            }else {
               binding.tvBtStatus.visibility = View.VISIBLE
               binding.tvBtStatus.text = "페어링된 기기가 없습니다."
            }
         }else {
            binding.tvBtStatus.visibility = View.VISIBLE
            binding.tvBtStatus.text = "블루투스가 비활성화되어 있습니다."
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, BLUETOOTH_REQUEST_CODE)
         }
      }
   }


   private fun requestPermission(): Boolean {
      var check = true
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         for(permission in BT_PERMISSION_2) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*BT_PERMISSION_2), REQUEST_CODE)
               check = false
            }
         }
      }else {
         for(permission in BT_PERMISSION_1) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
               ActivityCompat.requestPermissions(requireActivity(), arrayOf(*BT_PERMISSION_1), REQUEST_CODE)
               check = false
            }
         }
      }
      return check
   }

   companion object {
      private const val REQUEST_CODE = 1
      private const val BLUETOOTH_REQUEST_CODE = 1
   }
}