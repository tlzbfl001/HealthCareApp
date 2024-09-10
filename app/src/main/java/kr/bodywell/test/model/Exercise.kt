package kr.bodywell.test.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.bodywell.test.database.DBHelper.Companion.TYPE_USER

@Parcelize
data class Exercise (
   var id: Int = 0,
   var userId: Int = 0,
   var uid: String = "",
   var registerType: String = TYPE_USER,
   var name: String = "",
   var intensity: String = "HIGH",
   var workoutTime: Int = 0,
   var kcal: Int = 0,
   var useCount: Int = 0,
   var useDate: String = "",
   var createdAt: String = "",
   var isUpdated: Int = 0
) : Parcelable