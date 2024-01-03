package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Drug(
   var id: Int = 0,
   var type: String = "",
   var name: String = "",
   var amount: String = "",
   var unit: String = "",
   var period: String = "",
   var startDate: String = "",
   var endDate: String = "",
   var count: Int = 0
)

data class DrugTime(
   var id: Int = 0,
   var time: String = "",
   var drugId: Int = 0
)

@Parcelize
data class DrugDate(
   var id: Int = 0,
   var date: String = "",
   var drugId: Int = 0
) : Parcelable

data class DrugCheck(
   var id: Int = 0,
   var checked: Int = 0,
   var drugTimeId: Int = 0,
   var regDate: String = ""
)

data class DrugList(
   var id: Int = 0,
   var date: String = "",
   var name: String = "",
   var amount: String = "",
   var unit: String = "",
   var time: String = "",
   var initCheck: Int = 0,
   var checked: Int = 0
)