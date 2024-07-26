package kr.bodywell.android.view

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.util.BluetoothUtil
import kr.bodywell.android.util.BluetoothUtil.BLUETOOTH_CONNECTED
import kr.bodywell.android.util.BluetoothUtil.BLUETOOTH_CONNECTING
import kr.bodywell.android.util.BluetoothUtil.BLUETOOTH_NO_CONNECTED
import kr.bodywell.android.util.CalendarUtil.Companion.selectedDate
import kr.bodywell.android.util.CustomUtil.Companion.TAG
import kr.bodywell.android.util.CustomUtil.Companion.networkStatusCheck
import kr.bodywell.android.util.ViewModelUtil.createApiRequest
import kr.bodywell.android.util.ViewModelUtil.createSync
import kr.bodywell.android.util.ViewModelUtil.getToken
import kr.bodywell.android.util.ViewModelUtil.getUser
import kr.bodywell.android.util.ViewModelUtil.refreshToken
import kr.bodywell.android.util.ViewModelUtil.requestStatus
import kr.bodywell.android.util.ViewModelUtil.syncedStatus
import java.io.IOException
import java.time.LocalDate
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
   @SuppressLint("StaticFieldLeak")
   private val context = application.applicationContext
   private var dataManager: DataManager = DataManager(context)
   private val uuid="00001101-0000-1000-8000-00805F9B34FB"
   private var mSocket: BluetoothSocket? = null

   var dateVM = MutableLiveData<LocalDate>()
   var intVM = MutableLiveData<Int>()
   var msgVM = MutableLiveData<String>()

   init {
      dataManager.open()
      getUser = dataManager.getUser()
      getToken = dataManager.getToken()
      updateData()

      Log.d(TAG, "access: ${getToken.access}")
   }

   private fun updateData() = viewModelScope.launch {
      while(isActive) {
         if(networkStatusCheck(context)) {
            if(!syncedStatus) {
               refreshToken(dataManager)
               syncedStatus = createSync(dataManager)
            }else {
               refreshToken(dataManager)
               createApiRequest(dataManager)
            }
         }

         delay(10000)
      }
   }

   fun connect(adapter: BluetoothAdapter) {
      val pref = context.getSharedPreferences(BluetoothUtil.PREFERENCES, Context.MODE_PRIVATE)
      val mac = pref?.getString(BluetoothUtil.MAC, "")

      if(adapter.isEnabled && mac!!.isNotEmpty()) {
         val device = adapter.getRemoteDevice(mac)
         createBTConnect(device)
      }
   }

   private fun createBTConnect(device: BluetoothDevice) {
      try{
         mSocket=device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
         msgVM.value = BLUETOOTH_CONNECTING
         mSocket?.connect()
         msgVM.value = BLUETOOTH_CONNECTED
//         readMessage()
      }catch (e: IOException) {
         msgVM.value = BLUETOOTH_NO_CONNECTED
         e.printStackTrace()
      }catch(se: SecurityException) {
         se.printStackTrace()
      }
   }

   private fun readMessage() = viewModelScope.launch {
      while(mSocket!!.isConnected) {
         val buffer = ByteArray(256)
         try {
            val length=mSocket?.inputStream?.read(buffer)
            val message=String(buffer,0,length ?: 0)
            msgVM.value = message
         }catch(e: IOException) {
            msgVM.value = BLUETOOTH_NO_CONNECTED
            e.printStackTrace()
         }
      }
   }

   fun sendMessage(message: String) {
      try {
         mSocket?.outputStream?.write(message.toByteArray())
      }catch(e: IOException) {
         e.printStackTrace()
      }
   }

   fun closeConnection() {
      try {
         mSocket?.close()
      }catch (e: IOException) {
         e.printStackTrace()
      }
   }

   fun setDate() {
      dateVM.value = selectedDate
   }

   fun setInt(data: Int) {
      intVM.value = data
   }
}
