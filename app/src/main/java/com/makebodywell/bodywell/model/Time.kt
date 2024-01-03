package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Time(
    var hour: String = "",
    var minute: String = ""
) : Parcelable
