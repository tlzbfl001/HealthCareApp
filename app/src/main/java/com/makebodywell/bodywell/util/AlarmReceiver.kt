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
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.R
import com.makebodywell.bodywell.model.DrugDate
import com.makebodywell.bodywell.model.DrugTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    private var pendingIntent: PendingIntent? = null
    private val channelId = "channel1"
    private val channelName: CharSequence = "AlarmChannel"

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id")
        val time: ArrayList<DrugTime> = intent.getParcelableArrayListExtra("time")!!
        val message = intent.getStringExtra("message")

        if(intent.getParcelableArrayListExtra<DrugDate>("drugDate") == null) {
            val startDate = intent.getStringExtra("startDate")
            val endDate = intent.getStringExtra("endDate")
            showAlarmNotification(context, id!!.toInt(), message)
            setAlarm1(context, id.toInt(), startDate!!, endDate!!, time, message!!)
        }else {
            val drugDate: ArrayList<DrugDate> = intent.getParcelableArrayListExtra("drugDate")!!
            showAlarmNotification(context, id!!.toInt(), message)
            setAlarm2(context, id.toInt(), time, drugDate, message!!)
        }
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

    fun setAlarm1(context: Context, id: Int, startDate: String, endDate: String, time: ArrayList<DrugTime>, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id.toString())
        intent.putExtra("startDate", startDate)
        intent.putExtra("endDate", endDate)
        intent.putExtra("time", time)
        intent.putExtra("message", message)

        pendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        }else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        }

        val today = LocalDate.now()
        if(today >= LocalDate.parse(startDate) && today <= LocalDate.parse(endDate)) { //설정된 알람주기동안 실행
            val cal = Calendar.getInstance()
            val currentTime = System.currentTimeMillis() //현재시간(밀리세컨드)

            for(i in 0 until time.size) {
                cal.set(Calendar.HOUR_OF_DAY, time[i].hour.toInt())
                cal.set(Calendar.MINUTE, time[i].minute.toInt())
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
                }else if(i == (time.size - 1)) { //마지막번째일경우 다음날 알람실행
                    cal.add(Calendar.DATE, 1)
                    cal.set(Calendar.HOUR_OF_DAY, time[0].hour.toInt())
                    cal.set(Calendar.MINUTE, time[0].minute.toInt())
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

        // 설정된 날짜가 오늘보다 크면 설정된 날짜로 알람 설정
        if(today < LocalDate.parse(startDate)) {
            val formatter1 = SimpleDateFormat("yyyy-MM-dd")
            val formatter2 = SimpleDateFormat("yyyyMMdd")
            val formatDate = formatter2.format(formatter1.parse(startDate)!!)

            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, Integer.parseInt(formatDate.substring(0, 4)))
            cal.set(Calendar.MONTH, Integer.parseInt(formatDate.substring(4,6)) - 1)
            cal.set(Calendar.DATE, Integer.parseInt(formatDate.substring(6,8)))
            cal.set(Calendar.HOUR_OF_DAY, time[0].hour.toInt())
            cal.set(Calendar.MINUTE, time[0].minute.toInt())
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            try{
                alarmManager.setAlarmClock(AlarmClockInfo(cal.timeInMillis, pendingIntent), pendingIntent)
            }catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun setAlarm2(context: Context, id: Int, time: ArrayList<DrugTime>, drugDate: ArrayList<DrugDate>, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("id", id.toString())
        intent.putExtra("time", time)
        intent.putExtra("drugDate", drugDate)
        intent.putExtra("message", message)

        pendingIntent = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        }else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        }

        val today = LocalDate.now()
        for(i in 0 until drugDate.size) {
            if(today >= LocalDate.parse(drugDate[i].date)) {
                val cal = Calendar.getInstance()
                val currentTime = System.currentTimeMillis()

                for(j in 0 until time.size) {
                    cal.set(Calendar.HOUR_OF_DAY, time[j].hour.toInt())
                    cal.set(Calendar.MINUTE, time[j].minute.toInt())
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    val selectTime = cal.timeInMillis

                    if(selectTime > currentTime) {
                        try{
                            alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent)
                        }catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                        return
                    }
                }
            }
        }
    }

    fun isAlarmSet(context: Context?, id: Int): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        return pendingIntent != null
    }

    fun cancelAlarm(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.cancel(pendingIntent)
    }
}