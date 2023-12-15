package com.makebodywell.bodywell.view.setting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.makebodywell.bodywell.adapter.BtListAdapter
import com.makebodywell.bodywell.databinding.FragmentConnectBinding
import com.makebodywell.bodywell.model.Bluetooth

class ConnectFragment : Fragment() {
   private var _binding: FragmentConnectBinding? = null
   private val binding get() = _binding!!

   private var bluetoothAdapter: BluetoothAdapter? = null
   private var btListAdapter: BtListAdapter? = null
   private val btList = ArrayList<Bluetooth>()
   private val enableBluetooth = 1

   override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
   ): View {
      _binding = FragmentConnectBinding.inflate(layoutInflater)

      // 블루투스 설정
      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
      getPairedDevices()

      return binding.root
   }

   @SuppressLint("MissingPermission")
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
            startActivityForResult(enableIntent, enableBluetooth)
         }
      }
   }
}