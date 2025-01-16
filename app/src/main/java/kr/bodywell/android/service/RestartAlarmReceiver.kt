package kr.bodywell.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.util.CustomUtil.resetAlarm

// 전원 온오프 후 알람 재설정
class RestartAlarmReceiver : BroadcastReceiver() {
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            alarmReceiver = AlarmReceiver()
            coroutineScope.launch {
                resetAlarm(context)
            }
        }
    }
}