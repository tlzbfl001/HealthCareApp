package kr.bodywell.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.util.CustomUtil
import kr.bodywell.android.util.CustomUtil.resetAlarm
import kr.bodywell.android.util.MyApp.Companion.dataManager
import java.time.LocalDate

// 전원 온오프 후 알람 재설정
class RestartAlarmReceiver : BroadcastReceiver() {
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            coroutineScope.launch {
                Log.d(CustomUtil.TAG, "RestartAlarmReceiver")
                resetAlarm(context)
            }
        }
    }
}