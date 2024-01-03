package com.makebodywell.bodywell.util

import com.makebodywell.bodywell.model.DrugTime
import java.time.LocalDate

class DrugUtil {
    companion object {
        var drugType = ""
        var drugName = ""
        var drugCount = ""
        var drugUnitNum = 0
        var drugPeriodNum = 0
        var drugStartDate = ""
        var drugEndDate = ""
        var drugTimeList = ArrayList<DrugTime>()
        var drugDateList = ArrayList<LocalDate>()

        fun setDrugTimeList(h: Int, m: Int) {
            drugTimeList.add(DrugTime(hour = h, minute = m))
        }

        fun clearDrugData() {
            drugStartDate = ""
            drugEndDate = ""
            drugTimeList.clear()
        }
    }
}