package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER

@Parcelize
data class Activities (
   var id: String = "",
   var name: String = "",
   var registerType: String = TYPE_USER,
   var createdAt: String = "",
   var updatedAt: String = "",
   var deletedAt: String = ""
) : Parcelable

@Parcelize
data class Workout (
   var id: String = "",
   var name: String = "",
   var calorie: Int = 0,
   var intensity: String = "HIGH",
   var time: Int = 0,
   var date: String = "",
   var createdAt: String = "",
   var updatedAt: String = "",
   var deletedAt: String = "",
   var activityId: String = ""
) : Parcelable