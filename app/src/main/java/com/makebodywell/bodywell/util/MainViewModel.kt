package com.makebodywell.bodywell.util

import androidx.lifecycle.ViewModel
import com.makebodywell.bodywell.model.Time

class MainViewModel : ViewModel() {
    private var drugType = ""
    private var drugName = ""
    private var drugCount = ""
    private var drugUnitNum = 0
    private var drugPeriodNum = 0
    private var drugTimeList = ArrayList<Time>()
    private var drugDateList = ArrayList<String>()
    private var drugStartDate = ""
    private var drugEndDate = ""

    fun setDrugType(data: String) {
        drugType = data
    }

    fun setDrugName(data: String) {
        drugName = data
    }

    fun setDrugCount(data: String) {
        drugCount = data
    }

    fun setDrugUnitNum(data : Int) {
        drugUnitNum = data
    }

    fun setDrugPeriodNum(data : Int) {
        drugPeriodNum = data
    }

    fun setDrugTimeList(h: String, m: String) {
        drugTimeList.add(Time(hour = h, minute = m))
    }

    fun setDrugDateList(data : String) {
        drugDateList.add(data)
    }

    fun setDrugStartDate(data: String) {
        drugStartDate = data
    }

    fun setDrugEndDate(data: String) {
        drugEndDate = data
    }

    fun getDrugType() : String {
        return drugType
    }

    fun getDrugName() : String {
        return drugName
    }

    fun getDrugCount() : String {
        return drugCount
    }

    fun getDrugUnitNum() : Int {
        return drugUnitNum
    }

    fun getDrugPeriodNum() : Int {
        return drugPeriodNum
    }

    fun getDrugTimeList() : ArrayList<Time> {
        return drugTimeList
    }

    fun getDrugDateList() : ArrayList<String> {
        return drugDateList
    }

    fun getDrugStartDate() : String {
        return drugStartDate
    }

    fun getDrugEndDate() : String {
        return drugEndDate
    }

    fun clearDrugTimeList() {
        drugTimeList.clear()
    }
}