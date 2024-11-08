package kr.bodywell.android.service

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kr.bodywell.android.R
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.model.Item
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2
import java.time.LocalDate
import java.util.Calendar


class AlarmReceiver : BroadcastReceiver() {
    private var pendingIntent: PendingIntent? = null
    private val channelId = "channel1"
    private val channelName: CharSequence = "AlarmChannel"

    override fun onReceive(context: Context, intent: Intent) {
        val today = LocalDate.now()
        val notificationId = intent.getStringExtra("notificationId")
        val startDate = intent.getStringExtra("startDate")
        val endDate = intent.getStringExtra("endDate")
        val timeList = intent.getParcelableArrayListExtra<Item>("timeList")
        val message = intent.getStringExtra("message")

        if(today >= LocalDate.parse(startDate) && today <= LocalDate.parse(endDate)) {
            showAlarmNotification(context, notificationId!!.toInt(), message)
        }

        setAlarm(context, notificationId!!.toInt(), startDate!!, endDate!!, timeList!!, message!!)
    }

    private fun showAlarmNotification(context: Context, notificationId: Int, message: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo2)
            .setContentTitle(message)
            .setContentText("약복용 시간이에요. 잊지말고 복용해주세요~")
            .setColor(ContextCompat.getColor(context, R.color.transparent))

        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        mBuilder.setChannelId(channelId)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = mBuilder.build()
        notificationManager.notify(notificationId, notification)
    }

    fun setAlarm(context: Context, notificationId: Int, startDate: String, endDate: String, timeList: ArrayList<Item>, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra("notificationId", notificationId.toString())
        intent.putExtra("startDate", startDate)
        intent.putExtra("endDate", endDate)
        intent.putExtra("timeList", timeList)
        intent.putExtra("message", message)

        pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val today = LocalDate.now()
        val cal = Calendar.getInstance()

        if(today >= LocalDate.parse(startDate) && today <= LocalDate.parse(endDate)) { //설정된 알람주기동안 실행
            val currentTime = System.currentTimeMillis() //현재시간(밀리세컨드)

            for(i in 0 until timeList.size) {
                val split1 = timeList[i].string1.split(":")
                cal.set(Calendar.HOUR_OF_DAY, split1[0].toInt())
                cal.set(Calendar.MINUTE, split1[1].toInt())
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                var selectTime = cal.timeInMillis

                if(selectTime > currentTime) { //선택된 시간이 현재시간보다 크면 알람실행
                    try{
                        if(checkAlarmPermission2(context)) {
                            alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                        }
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    return
                }else if(i == (timeList.size - 1)) {
                    val split2 = timeList[0].string1.split(":")
                    cal.add(Calendar.DATE, 1)
                    cal.set(Calendar.HOUR_OF_DAY, split2[0].toInt())
                    cal.set(Calendar.MINUTE, split2[1].toInt())
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    selectTime = cal.timeInMillis

                    try{
                        if(checkAlarmPermission2(context)) {
                            alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                        }
                    }catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        if(today < LocalDate.parse(startDate)) {
            val split = timeList[0].string1.split(":")
            cal.add(Calendar.DATE, 1)
            cal.set(Calendar.HOUR_OF_DAY, split[0].toInt())
            cal.set(Calendar.MINUTE, split[1].toInt())
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val selectTime = cal.timeInMillis

            try{
                if(checkAlarmPermission1(context) && checkAlarmPermission2(context)) {
                    alarmManager.setAlarmClock(AlarmClockInfo(selectTime, pendingIntent), pendingIntent!!)
                }
            }catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun cancelAlarm(context: Context, notificationId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent!!)
    }
}