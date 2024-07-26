package kr.bodywell.android.model

import android.bluetooth.BluetoothDevice

data class Bluetooth (
   val device: BluetoothDevice,
   val isChecked: Boolean
//   val name: String,
//   val status: String
)