package com.makebodywell.bodywell.util

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.DrugTime
import java.time.LocalDate
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    private var pendingIntent: PendingIntent? = null
    private val channelId = "channel1"
    private val channelName: CharSequence = "AlarmChannel"

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id")
        val startDate = intent.getStringExtra("startDate")
        val endDate = intent.getStringExtra("endDate")
        val timeList: ArrayList<DrugTime> = intent.getParcelableArrayListExtra("timeList")!!
        val message = intent.getStringExtra("message")

        showAlarmNotification(context, id!!.toInt(), message)
        setAlarm(context, id.toInt(), startDate!!, endDate!!, timeList, message!!)
    }

    private fun showAlarmNotification(context: Context, id: Int, message: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle("약복용")
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, R.color.transparent))

        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        mBuilder.setChannelId(channelId)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = mBuilder.build()
        notificationManager.notify(id, notification)
    }

    fun setAlarm(context: Context, id: Int, startDate: String, endDate: String, timeList: ArrayList<DrugTime>, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("id", id.toString())
        intent.putExtra("startDate", startDate)
        intent.putExtra("endDate", endDate)
        intent.putExtra("timeList", timeList)
        intent.putExtra("message", message)

        pendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val today = LocalDate.now()

        if(today >= LocalDate.parse(startDate) && today <= LocalDate.parse(endDate)) { //설정된 알람주기동안 실행
            val cal = Calendar.getInstance()
            val currentTime = System.currentTimeMillis() //현재시간(밀리세컨드)

            for(i in 0 until timeList.size) {
                cal.set(Calendar.HOUR_OF_DAY, timeList[i].hour)
                cal.set(Calendar.MINUTE, timeList[i].minute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                var selectTime = cal.timeInMillis

                if(selectTime > currentTime) { //선택된 시간이 현재시간보다 크면 알람실행
                    try{
                        alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent)
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    return
                }else if(i == (timeList.size - 1)) {
                    cal.add(Calendar.DATE, 1)
                    cal.set(Calendar.HOUR_OF_DAY, timeList[0].hour)
                    cal.set(Calendar.MINUTE, timeList[0].minute)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    selectTime = cal.timeInMillis

                    try{
                        alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent)
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        if(today < LocalDate.parse(startDate)) {
            val cal = Calendar.getInstance()

            cal.add(Calendar.DATE, 1)
            cal.set(Calendar.HOUR_OF_DAY, timeList[0].hour)
            cal.set(Calendar.MINUTE, timeList[0].minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val selectTime = cal.timeInMillis

            try{
                alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent)
            }catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAlarm(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.cancel(pendingIntent)
    }
}