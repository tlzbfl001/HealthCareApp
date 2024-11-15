package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER

@Parcelize
data class InitExercise (
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