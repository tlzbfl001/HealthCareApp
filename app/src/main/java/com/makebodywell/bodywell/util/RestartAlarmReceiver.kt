package com.makebodywell.bodywell.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.model.DrugDate
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

                val getDrugDaily = dataManager.getDrugDaily(LocalDate.now().toString())
                for(i in 0 until getDrugDaily.size) {
                    if(getDrugDaily[i].period == "매일" && getDrugDaily[i].isSet == 1) {
                        val timeList = ArrayList<DrugTime>()
                        val getDrugTime = dataManager.getDrugTime(getDrugDaily[i].id)
                        for(j in 0 until getDrugTime.size) {
                            timeList.add(DrugTime(hour = getDrugTime[j].hour, minute = getDrugTime[j].minute))
                        }

                        val message = getDrugDaily[i].name + " " + getDrugDaily[i].amount + getDrugDaily[i].unit
                        alarmReceiver.setAlarm1(context, getDrugDaily[i].id, getDrugDaily[i].startDate, getDrugDaily[i].endDate, timeList, message)
                    }else if(getDrugDaily[i].period == "특정일 지정" && getDrugDaily[i].isSet == 1) {
                        val timeList = ArrayList<DrugTime>()
                        val getDrugTime = dataManager.getDrugTime(getDrugDaily[i].id)
                        for(j in 0 until getDrugTime.size) {
                            timeList.add(DrugTime(hour = getDrugTime[j].hour, minute = getDrugTime[j].minute))
                        }

                        val dateList = ArrayList<DrugDate>()
                        val getDrugDate = dataManager.getDrugDate(getDrugDaily[i].id)
                        for(j in 0 until getDrugDate.size) {
                            dateList.add(DrugDate(date = getDrugDate[j].date))
                        }

                        val message = getDrugDaily[i].name + " " + getDrugDaily[i].amount + getDrugDaily[i].unit
                        alarmReceiver.setAlarm2(context, getDrugDaily[i].id, timeList, dateList, message)
                    }
                }
            }
        }
    }
}