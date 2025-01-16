package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.bodywell.android.model.Constant.USER

@Parcelize
data class ActivityData (
   var id: String = "",
   var name: String = "",
   var registerType: String = USER,
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