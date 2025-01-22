package kr.bodywell.android.service

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.nfc.NfcAdapter.EXTRA_DATA
import android.os.Binder
import android.os.IBinder
import kr.bodywell.android.R
import java.util.UUID

class BluetoothLeService : Service() {
   private val binder: Binder = LocalBinder()
   var connectionState = STATE_DISCONNECTED
   private var bluetoothAdapter: BluetoothAdapter? = null
   private var bluetoothGatt: BluetoothGatt? = null

   override fun onBind(intent: Intent): IBinder {
      return binder
   }

   internal class LocalBinder : Binder() {
      val service: BluetoothLeService
         get() = BluetoothLeService()
   }

   @SuppressLint("MissingPermission")
   fun connect(address:String) : Boolean{
      bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
      if (bluetoothAdapter == null || address == null) {
//         Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
         return false
      }

      try {
         val device = bluetoothAdapter!!.getRemoteDevice(address)
         // connect to the GATT server on the device
         bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
         return true;
      }catch (e : IllegalArgumentException) {
//         Log.w(TAG, "Device not found with provided address.")
         return false
      }
   }

   private fun broadcastUpdate(action: String) {
      val intent = Intent(action)
      sendBroadcast(intent)
   }

   fun getSupportedGattServices(): List<BluetoothGattService>? {
      return if (bluetoothGatt == null) null else bluetoothGatt!!.services
   }

   private val bluetoothGattCallback = object : BluetoothGattCallback() {
      @SuppressLint("MissingPermission")
      override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
         if (newState == BluetoothProfile.STATE_CONNECTED) {
            // successfully connected to the GATT Server
            connectionState = STATE_CONNECTED;
            broadcastUpdate(ACTION_GATT_CONNECTED);
            // Attempts to discover services after successful connection.
            bluetoothGatt!!.discoverServices();
         } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // disconnected from the GATT Server
            connectionState = STATE_DISCONNECTED;
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
         }
      }

      override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
         if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
         } else {
//            Log.w(TAG, "onServicesDiscovered received: $status")
         }
      }

      override fun onCharacteristicRead(
         gatt: BluetoothGatt,
         characteristic: BluetoothGattCharacteristic,
         status: Int
      ) {
         if (status == BluetoothGatt.GATT_SUCCESS) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
         }
      }

      override fun onCharacteristicChanged(
         gatt: BluetoothGatt,
         characteristic: BluetoothGattCharacteristic
      ) {
         broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
      }
   }

   private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
      val intent = Intent(action)

//      Log.d(TAG, "characteristic.properties: ${characteristic.properties}")
//      Log.d(TAG, "characteristic.uuid: ${characteristic.uuid}")
      // This is special handling for the Heart Rate Measurement profile. Data
      // parsing is carried out as per profile specifications.
      when (characteristic.uuid) {
         UUID.fromString(resources.getString(R.string.rxCharacteristicUUID)) -> {
            val flag = characteristic.properties
            val format = when (flag and 0x01) {
               0x01 -> {
//                  Log.d(TAG, "Heart rate format UINT16.")
                  BluetoothGattCharacteristic.FORMAT_UINT16
               }
               else -> {
//                  Log.d(TAG, "Heart rate format UINT8.")
                  BluetoothGattCharacteristic.FORMAT_UINT8
               }
            }
            val heartRate = characteristic.getIntValue(format, 1)
//            Log.d(TAG, String.format("Received heart rate: %d", heartRate))
            intent.putExtra(EXTRA_DATA, (heartRate).toString())
         }
         else -> {
            // For all other profiles, writes the data formatted in HEX.
            val data: ByteArray? = characteristic.value
            if (data?.isNotEmpty() == true) {
               val hexString: String = data.joinToString(separator = " ") {
                  String.format("%02X", it)
               }
               intent.putExtra(EXTRA_DATA, "$data\n$hexString")
            }
         }
      }
      sendBroadcast(intent)
   }

   @SuppressLint("MissingPermission")
   fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enabled: Boolean) {
      bluetoothGatt?.let { gatt ->
         gatt.setCharacteristicNotification(characteristic, enabled)

         if (characteristic.uuid == UUID.fromString(resources.getString(R.string.rxCharacteristicUUID))) {
            val descriptor = characteristic.getDescriptor(UUID.fromString(resources.getString(R.string.cccdUUID)))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
         }
      } ?: run {
//         Log.w(TAG, "BluetoothGatt not initialized")
      }
   }

   @SuppressLint("MissingPermission")
   fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
      bluetoothGatt?.let { gatt ->
         gatt.readCharacteristic(characteristic)
      } ?: run {
//         Log.w(TAG, "BluetoothGatt not initialized")
      }
   }

   override fun onUnbind(intent: Intent?): Boolean {
      close()
      return super.onUnbind(intent)
   }

   @SuppressLint("MissingPermission")
   private fun close() {
      if (bluetoothGatt != null) {
         bluetoothGatt!!.close()
         bluetoothGatt = null
      }
   }

   companion object {
      const val ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
      const val ACTION_GATT_DISCONNECTED =
         "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"
      const val ACTION_GATT_SERVICES_DISCOVERED =
         "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED"

      private const val STATE_DISCONNECTED = 0
      private const val STATE_CONNECTED = 2
      private const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
      private var connectionState = 0;
   }
}
