package kr.bodywell.android.service

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kr.bodywell.android.R
import kr.bodywell.android.model.MedicineTime
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2
import java.time.LocalDate
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    private val channelId = "channel1"
    private val channelName: CharSequence = "AlarmChannel"
    var alarmManager: AlarmManager? = null
    var pendingIntent: PendingIntent? = null

    override fun onReceive(context: Context, intent: Intent) {
        val alarmCode = intent.getStringExtra("alarmCode")
        val startDate = intent.getStringExtra("startDate")
        val endDate = intent.getStringExtra("endDate")
        val timeList = intent.getParcelableArrayListExtra<MedicineTime>("timeList")
        val message = intent.getStringExtra("message")


        val alarmUp = PendingIntent.getBroadcast(
            context, alarmCode!!.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_MUTABLE
        )
        Log.d(TAG, "alarmUp: ${alarmUp}")

        Log.d(TAG, "alarmCode: $alarmCode")
        Log.d(TAG, "startDate: $startDate")
        Log.d(TAG, "endDate: $endDate")
        Log.d(TAG, "timeList: $timeList")
        Log.d(TAG, "message: $message")

        if(alarmCode != null) {
            if(LocalDate.now() >= LocalDate.parse(startDate) && LocalDate.now() <= LocalDate.parse(endDate)) {
                showAlarmNotification(context, alarmCode.toInt(), message)
            }

            setAlarm(context, alarmCode.toInt(), startDate!!, endDate!!, timeList!!, message!!)
        }
    }

    private fun showAlarmNotification(context: Context, notificationId: Int, message: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle(message)
            .setContentText("약복용 시간이에요. 잊지말고 복용해주세요.")
            .setColor(ContextCompat.getColor(context, R.color.transparent))

        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        mBuilder.setChannelId(channelId)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = mBuilder.build()
        notificationManager.notify(notificationId, notification)
    }

    fun setAlarm(context: Context, alarmCode: Int, startDate: String, endDate: String, timeList: ArrayList<MedicineTime>, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("alarmCode", alarmCode.toString())
        intent.putExtra("startDate", startDate)
        intent.putExtra("endDate", endDate)
        intent.putExtra("timeList", timeList)
        intent.putExtra("message", message)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val cal = Calendar.getInstance()

        if(LocalDate.now() >= LocalDate.parse(startDate) && LocalDate.now() <= LocalDate.parse(endDate)) { // 설정된 알람주기동안 실행
            val currentTime = System.currentTimeMillis() // 현재시간(밀리세컨드)

            for(i in 0 until timeList.size) {
                val split1 = timeList[i].time.split(":")
                cal.set(Calendar.HOUR_OF_DAY, split1[0].toInt())
                cal.set(Calendar.MINUTE, split1[1].toInt())
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                var selectTime = cal.timeInMillis

                if(selectTime > currentTime) { // 선택된 시간이 현재시간보다 크면 알람실행
                    try{
                        if(checkAlarmPermission2(context)) {
                            alarmManager!!.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                        }
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    return
                }else if(i == (timeList.size - 1)) {
                    val split2 = timeList[0].time.split(":")
                    cal.add(Calendar.DATE, 1)
                    cal.set(Calendar.HOUR_OF_DAY, split2[0].toInt())
                    cal.set(Calendar.MINUTE, split2[1].toInt())
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    selectTime = cal.timeInMillis

                    try{
                        if(checkAlarmPermission2(context)) {
                            alarmManager!!.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                        }
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        if(LocalDate.now() < LocalDate.parse(startDate)) {
            val split = timeList[0].time.split(":")
            cal.add(Calendar.DATE, 1)
            cal.set(Calendar.HOUR_OF_DAY, split[0].toInt())
            cal.set(Calendar.MINUTE, split[1].toInt())
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val selectTime = cal.timeInMillis

            try{
                if(checkAlarmPermission1(context) && checkAlarmPermission2(context)) {
                    alarmManager!!.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                }
            }catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAlarm(context: Context, alarmCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            PendingIntent.getBroadcast(context,alarmCode,intent,PendingIntent.FLAG_MUTABLE)
        }else{
            PendingIntent.getBroadcast(context,alarmCode,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager!!.cancel(pendingIntent!!)
        pendingIntent!!.cancel()
    }
}