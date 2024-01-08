package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercise (
   var id: Int = 0,
   var category: String = "",
   var name: String = "",
   var workoutTime: Int = 0,
   var distance: Double = 0.0,
   var calories: Int = 0,
   var regDate: String = ""
) : Parcelable
