package com.makebodywell.bodywell.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.DrugTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

                val getDrugDaily = dataManager.getDrugDaily(MyApp.prefs.getId(), LocalDate.now().toString())
                for(i in 0 until getDrugDaily.size) {
                    if(getDrugDaily[i].isSet == 1) {
                        val timeList = ArrayList<DrugTime>()
                        val getDrugTime = dataManager.getDrugTime(MyApp.prefs.getId(), getDrugDaily[i].id)

                        for(j in 0 until getDrugTime.size) {
                            timeList.add(DrugTime(hour = getDrugTime[j].hour, minute = getDrugTime[j].minute))
                        }

                        val message = getDrugDaily[i].name + " " + getDrugDaily[i].amount + getDrugDaily[i].unit
                        alarmReceiver.setAlarm(context, getDrugDaily[i].id, getDrugDaily[i].startDate, getDrugDaily[i].endDate, timeList, message)
                    }
                }
            }
        }
    }
}