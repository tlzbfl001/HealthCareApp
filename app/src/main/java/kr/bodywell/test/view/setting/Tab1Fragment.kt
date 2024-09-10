package kr.bodywell.test.view.setting

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.bodywell.test.R
import kr.bodywell.test.database.DataManager
import kr.bodywell.test.databinding.FragmentTab1Binding
import kr.bodywell.test.util.CustomUtil.TAG
import kr.bodywell.test.util.MyApp
import java.util.UUID

@SuppressLint("MissingPermission")
class Tab1Fragment : Fragment() {
	private var _binding: FragmentTab1Binding? = null
	private val binding get() = _binding!!

	private lateinit var dataManager: DataManager
	private var bluetoothAdapter: BluetoothAdapter? = null
	private var gatt: BluetoothGatt? = null
	private val bundle = Bundle()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentTab1Binding.inflate(layoutInflater)

		dataManager = DataManager(activity)
		dataManager.open()

		binding.btnConnect.setOnClickListener {
			if(MyApp.prefs.getDevice().isNotEmpty()) connect(MyApp.prefs.getDevice().elementAt(1))
		}

		binding.btnInit.setOnClickListener {
			writeCharacteristic("x")
		}

		binding.btnDoor1.setOnClickListener {
			writeCharacteristic("a")
		}

		binding.btnDoor2.setOnClickListener {
			writeCharacteristic("b")
		}

		binding.btnDoor3.setOnClickListener {
			writeCharacteristic("c")
		}

		return binding.root
	}

	private fun connect(address: String) {
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
}