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

@Parcelize
data class DrugTime(
   var hour: String = "",
   var minute: String = ""
) : Parcelable

@Parcelize
data class DrugDate(
   var date: String = ""
) : Parcelable