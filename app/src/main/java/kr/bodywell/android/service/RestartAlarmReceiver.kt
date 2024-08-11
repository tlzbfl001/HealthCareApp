package kr.bodywell.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.model.DrugTime
import kr.bodywell.android.util.PermissionUtil.checkAlarmPermissions
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

                val getDrugDaily = dataManager.getDrug(LocalDate.now().toString())
                for(i in 0 until getDrugDaily.size) {
                    if(getDrugDaily[i].isSet == 1) {
                        val timeList = ArrayList<DrugTime>()
                        val getDrugTime = dataManager.getDrugTime(getDrugDaily[i].id)

                        for(j in 0 until getDrugTime.size) {
                            timeList.add(DrugTime(time = getDrugTime[j].time))
                        }

                        val message = getDrugDaily[i].name + " " + getDrugDaily[i].amount + getDrugDaily[i].unit
                        if(!checkAlarmPermissions(context)) {
                            alarmReceiver.setAlarm(context, getDrugDaily[i].id, getDrugDaily[i].startDate, getDrugDaily[i].endDate, timeList, message)
                        }
                    }
                }
            }
        }
    }
}