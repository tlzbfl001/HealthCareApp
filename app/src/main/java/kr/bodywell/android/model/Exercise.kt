package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercise (
   var id: Int = 0,
   var userId: Int = 0,
   var uid: String = "",
   var name: String = "",
   var intensity: String = "HIGH",
   var workoutTime: Int = 0,
   var kcal: Int = 0,
   var useCount: Int = 0,
   var regDate: String = "",
   var useDate: String = "",
   var basic: Int = 0,
   var isUpdated: Int = 0
) : Parcelable