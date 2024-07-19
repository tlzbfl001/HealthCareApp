package kr.bodywell.test.util

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService : Service() {
   private val btUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
   private var bluetoothAdapter: BluetoothAdapter? = null
   private var socket: BluetoothSocket? = null
   private var btArray: Array<BluetoothDevice?>? = null
   private var sendReceive: SendReceive? = null
   private var inputStream: InputStream? = null
   private var outputStream: OutputStream? = null
   private val stateConnected = 1
   private val stateConnectionFailed = 2
   private val stateMessageReceived = 3

   private var isActive = true

   override fun onBind(intent: Intent): IBinder {
      TODO("Return the communication channel to the service.")
   }

   @SuppressLint("MissingPermission")
   override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
      if(intent == null) {
         return START_REDELIVER_INTENT // 강제종료시 서비스 재시작되고, 마지막 intent 값도 전달됨.
      }else {
         deviceNum = intent.getStringExtra("deviceNum").toString()

         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
         val pairedDevices = bluetoothAdapter!!.bondedDevices
         btArray = arrayOfNulls(pairedDevices.size)

         var index = 0
         if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
               btArray!![index] = device
               index++
            }
         }

         val deviceId = deviceNum.toInt()
         val clientClass = ClientClass(btArray!![deviceId]!!) // 전달된 데이터에 해당하는 기기 연동
         clientClass.start()
      }

      return super.onStartCommand(intent, flags, startId)
   }

   @SuppressLint("MissingPermission")
   private inner class ClientClass constructor(private val device: BluetoothDevice) : Thread(){
      init {
         try {
            socket = device.createRfcommSocketToServiceRecord(btUUID)
         } catch (e: IOException) {
            e.printStackTrace()
         }
      }

      @SuppressLint("MissingPermission")
      override fun run() {
         try{
            socket!!.connect()
            val message = Message.obtain()
            message.what = stateConnected
            handler.sendMessage(message)
            sendReceive = SendReceive(socket)
            sendReceive!!.start()
         }catch (e: IOException) {
            e.printStackTrace()
            val message = Message.obtain()
            message.what = stateConnectionFailed
            handler.sendMessage(message)
         }
      }
   }

   var handler = Handler { msg ->
      when (msg.what) {
         stateConnected -> btStatus = "연결 완료"
         stateConnectionFailed -> btStatus = "연결 실패"
         stateMessageReceived -> {
            val readBuff = msg.obj as ByteArray
            val tempMsg = String(readBuff, 0, msg.arg1)
            btData = tempMsg
         }
      }
      true
   }

   private inner class SendReceive(private val bluetoothSocket: BluetoothSocket?) : Thread() {
      init {
         var tempIn: InputStream? = null
         var tempOut: OutputStream? = null
         try {
            tempIn = bluetoothSocket!!.inputStream
            tempOut = bluetoothSocket.outputStream
         } catch (e: IOException) {
            e.printStackTrace()
         }
         inputStream = tempIn
         outputStream = tempOut
      }

      override fun run() {
         val buffer = ByteArray(1024)
         var bytes: Int
         while (isActive) {
            try{
               bytes = inputStream!!.read(buffer)
               handler.obtainMessage(stateMessageReceived, bytes, -1, buffer).sendToTarget()
            }catch (e: IOException) {
               e.printStackTrace()
               isActive = false
               try {
                  if (btData != "0") btData = "0"
                  if (inputStream != null) inputStream!!.close() // 입력 스트림 닫아주기
                  if (outputStream != null) outputStream!!.close() // 출력 스트림 닫아주기
                  if (socket != null) socket?.close() // 소켓 닫아주기
               } catch (e: IOException) {
                  e.printStackTrace()
               }
            }
            sleep(1000)
         }
      }
   }

   companion object {
      var deviceNum = ""
      var btStatus = "연결중"
      var btData = "0"
   }

   override fun onDestroy() {
      super.onDestroy()
      isActive = false
      try {
         if (inputStream != null) inputStream!!.close()
         if (outputStream != null) outputStream!!.close()
         if (socket != null) socket?.close()
      } catch (e: IOException) {
         e.printStackTrace()
      }
   }
}