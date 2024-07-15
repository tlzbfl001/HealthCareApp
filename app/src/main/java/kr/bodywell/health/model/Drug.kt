package kr.bodywell.health.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Drug(
   var id: Int = 0,
   var userId: Int = 0,
   var uid: String = "",
   var type: String = "",
   var name: String = "",
   var amount: Int = 0,
   var unit: String = "",
   var count: Int = 0,
   var startDate: String = "",
   var endDate: String = "",
   var isSet: Int = 0,
   var isUpdated: Int = 0
)

@Parcelize
data class DrugTime(
   var id: Int = 0,
   var userId: Int = 0,
   var drugId: Int = 0,
   var uid: String = "",
   var time: String = "",
   var created: String = ""
) : Parcelable

data class DrugCheck(
   var id: Int = 0,
   var userId: Int = 0,
   var drugId: Int = 0,
   var drugTimeId: Int = 0,
   var uid: String = "",
   var created: String = ""
)

data class DrugList(
   var id: Int = 0,
   var uid: String = "",
   var drugId: Int = 0,
   var drugTimeId: Int = 0,
   var date: String = "",
   var name: String = "",
   var amount: Int = 0,
   var unit: String = "",
   var time: String = "",
   var initCheck: Int = 0,
   var checked: Int = 0
)