package kr.bodywell.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission1
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermission2
import java.time.LocalDate

class RestartAlarmReceiver : BroadcastReceiver() {
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
            alarmReceiver = AlarmReceiver()

            coroutineScope.launch {
                val dataManager = DataManager(context)
                dataManager.open()

                val getDrugDate = dataManager.getDrugDate(LocalDate.now().toString())

                for(i in 0 until getDrugDate.size) {
                    val getDrugData = dataManager.getDrugData(getDrugDate[i])
                    
                    if(getDrugData.isSet == 1) {
                        val timeList = ArrayList<DrugTime>()
                        val getDrugTime = dataManager.getDrugTime(getDrugData.id)

                        for(j in 0 until getDrugTime.size) {
                            timeList.add(DrugTime(time = getDrugTime[j].time))
                        }

                        val message = getDrugData.name + " " + getDrugData.amount + getDrugData.unit
                        alarmReceiver.setAlarm(context, getDrugData.id, getDrugData.startDate, getDrugData.endDate, timeList, message)
                    }
                }
            }
        }
    }
}