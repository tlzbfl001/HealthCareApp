package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Medicine(
   var id: String = "",
   var category: String = "",
   var name: String = "",
   var amount: Int = 0,
   var unit: String = "",
   var starts: String = "",
   var ends: String = "",
   var createdAt: String = "",
   var updatedAt: String = ""
) : Parcelable

@Parcelize
data class MedicineTime(
   var id: String = "",
   var userId: Int = 0,
   var time: String = "",
   var createdAt: String = "",
   var updatedAt: String = "",
   var deletedAt: String = "",
   var medicineId: String = ""
) : Parcelable

data class MedicineIntake(
   var id: String = "",
   var userId: Int = 0,
   var category: String = "",
   var name: String = "",
   var amount: Int = 0,
   var unit: String = "",
   var intakeAt: String = "",
   var createdAt: String = "",
   var updatedAt: String = "",
   var deletedAt: String = "",
   var medicineId: String = "",
   var medicineTimeId: String = "",
   var sourceId: String = ""
)

data class MedicineList(
   var id: Int = 0,
   var name: String = "",
   var amount: Int = 0,
   var unit: String = "",
   var time: String = "",
   var date: String = "",
   var medicineId: String = "",
   var medicineTimeId: String = "",
   var initCheck: Int = 0,
   var isChecked: String = ""
)