package kr.bodywell.android.view.setting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.bodywell.android.R
import kr.bodywell.android.databinding.FragmentTab1Binding
import kr.bodywell.android.util.MyApp
import kr.bodywell.android.util.PermissionUtil.checkBluetoothPermission
import java.util.UUID

@SuppressLint("MissingPermission")
class Tab1Fragment : Fragment() {
	private var _binding: FragmentTab1Binding? = null
	private val binding get() = _binding!!

	private var bluetoothAdapter: BluetoothAdapter? = null
	private var gatt: BluetoothGatt? = null
//	private var bluetoothService: BluetoothLeService? = null
	private var command = ""
	private var isOn = false

	companion object {
		const val COMMAND_INITIAL = "x"
		const val COMMAND_INIT_DOOR_1 = "f"
		const val COMMAND_INIT_DOOR_2 = "g"
		const val COMMAND_INIT_DOOR_3 = "h"
		const val COMMAND_DOOR_1_COUNT = "c"
		const val COMMAND_DOOR_2_COUNT = "d"
		const val COMMAND_DOOR_3_COUNT = "e"
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentTab1Binding.inflate(layoutInflater)

//		bluetoothService = BluetoothLeService()
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

		binding.btnConnect.setOnClickListener {
			if(MyApp.prefs.getDevice().isNotEmpty()) {
				connect(MyApp.prefs.getDevice().elementAt(1))
			}
		}

		binding.btnInit.setOnClickListener {
			command = COMMAND_INITIAL
			writeCharacteristic(command)
		}

		binding.btnDoor1.setOnClickListener {
			command = COMMAND_INIT_DOOR_1
			writeCharacteristic(command)
		}

		binding.btnDoor2.setOnClickListener {
			command = COMMAND_INIT_DOOR_2
			writeCharacteristic(command)
		}

		binding.btnDoor3.setOnClickListener {
			command = COMMAND_INIT_DOOR_3
			writeCharacteristic(command)
		}

		binding.btnGetCount1.setOnClickListener {
			command = COMMAND_DOOR_1_COUNT
			writeCharacteristic(command)
		}

		binding.btnGetCount2.setOnClickListener {
			command = COMMAND_DOOR_2_COUNT
			writeCharacteristic(command)
		}

		binding.btnGetCount3.setOnClickListener {
			command = COMMAND_DOOR_3_COUNT
			writeCharacteristic(command)
		}

		binding.btnOnOff.setOnClickListener {
			val hex = if(isOn) 0x31 else 0x30
			writeCharacteristic(hex.toChar().toString())
			isOn = !isOn
		}

		return binding.root
	}

	private fun connect(address: String) {
		val device = bluetoothAdapter?.getRemoteDevice(address)
		if(device == null) {
			Toast.makeText(requireActivity(), "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
			return
		}else {
			gatt = device.connectGatt(requireActivity(), false, btGattCallback)
		}
	}

	private val btGattCallback = object: BluetoothGattCallback() {
		override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
			super.onConnectionStateChange(gatt, status, newState)
			if (newState == BluetoothGatt.STATE_CONNECTED) {
//				Log.i(TAG, "Connected to GATT server.")
				gatt.discoverServices()
			} else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
//				Log.i(TAG, "Disconnected from GATT server.")
			}
		}

		override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
			super.onServicesDiscovered(gatt, status)
			if(status == BluetoothGatt.GATT_SUCCESS) {
				setCharacteristicNotification()
			}else {
//				Log.w(TAG, "onServicesDiscovered: $status")
			}
		}

		// 안드로이드 13이상에서 호출
		override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
			broadcastUpdate(characteristic)
			throw RuntimeException("Stub!")
		}

		// 안드로이드 12까지 호출
		override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
			super.onCharacteristicChanged(gatt, characteristic)
			val data: ByteArray? = characteristic?.value
			broadcastUpdate(characteristic!!)
		}

		override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
			super.onCharacteristicRead(gatt, characteristic, value, status)
			if (characteristic.uuid == UUID.fromString(resources.getString(R.string.rxCharacteristicUUID))) {
//				Log.i(TAG, "read : ${characteristic.value}")
			}
		}
	}

	fun setCharacteristicNotification() {
		/*// descriptor 확인
		service!!.characteristics.forEach {
			val listDescriptor:List<BluetoothGattDescriptor> = it.descriptors
			if(listDescriptor.isNotEmpty()){
				listDescriptor.forEach {
					Log.d(TAG, "it: ${it.uuid}")
				}
			}
		}*/

		val service = gatt?.getService(UUID.fromString(resources.getString(R.string.serviceUUID)))
		val characteristic = service?.getCharacteristic(UUID.fromString(resources.getString(R.string.txCharacteristicUUID)))
		if (characteristic != null) {
			gatt?.setCharacteristicNotification(characteristic, true)
			val desc = characteristic.getDescriptor(UUID.fromString(resources.getString(R.string.cccdUUID)))
			desc?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
			gatt?.writeDescriptor(desc)
		}
	}

	private fun writeCharacteristic(msg: String) {
		val service = gatt?.getService(UUID.fromString(resources.getString(R.string.serviceUUID)))
		val characteristic = service?.getCharacteristic(UUID.fromString(resources.getString(R.string.rxCharacteristicUUID)))

		if(characteristic != null) {
			characteristic.value = msg.toByteArray()
			val success = gatt?.writeCharacteristic(characteristic)
//			Log.i(TAG, "writeCharacteristic: $success")
		}
	}

	private fun broadcastUpdate(characteristic: BluetoothGattCharacteristic) {
		// 프로필 정보 핸들링
		val data: ByteArray? = characteristic.value
		if(data?.isNotEmpty() == true) {
			val hexString: String = data.joinToString(separator = " ") {
				String.format("%02X", it)
			}
//			Log.i(TAG, "hexString: $hexString")

			var result = 0
			var shift = 0
			for(byte in data) {
				result = result or (byte.toInt() shl shift)
				shift += 8
			}

			when(command) {
				COMMAND_DOOR_1_COUNT -> binding.tvCount1.text = "Door 1 Count : $result"
				COMMAND_DOOR_2_COUNT -> binding.tvCount2.text = "Door 2 Count : $result"
				COMMAND_DOOR_3_COUNT -> binding.tvCount3.text = "Door 3 Count : $result"
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		if(!checkBluetoothPermission(requireActivity())) gatt?.disconnect()
	}
}