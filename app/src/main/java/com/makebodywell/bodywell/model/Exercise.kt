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
   var calories: Int = 0,
   var regDate: String = ""
) : Parcelable