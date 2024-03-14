package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercise (
   var id: Int = 0,
   var userId: Int = 0,
   var name: String = "",
   var intensity: String = "",
   var workoutTime: Int = 0,
   var kcal: Int = 0,
   var useCount: Int = 0,
   var regDate: String = "",
   var useDate: String = ""
) : Parcelable