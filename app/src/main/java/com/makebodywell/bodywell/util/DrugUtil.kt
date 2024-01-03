package com.makebodywell.bodywell.util

import com.makebodywell.bodywell.model.Time
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
        var drugTimeList = ArrayList<Time>()
        var drugDateList = ArrayList<LocalDate>()

        fun setDrugTimeList(h: String, m: String) {
            drugTimeList.add(Time(hour = h, minute = m))
        }

        fun clearDrugData() {
            drugStartDate = ""
            drugEndDate = ""
            drugTimeList.clear()
        }
    }
}