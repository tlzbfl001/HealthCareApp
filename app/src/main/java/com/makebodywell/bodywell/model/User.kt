package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String? = "",
    var uid: String? = "",
    var email: String? = "",
    var name: String? = "",
    var gender: String? = "",
    var birthday: String? = "",
    var image: String? = "",
    var height: String? = "0",
    var weight: String? = "0",
    var weightGoal: String? = "0",
    var kcalGoal: String? = "0",
    var waterGoal: String? = "0",
    var waterUnit: String? = "0",
    var measureHeight: Int? = 0,
    var regDate: String? = ""
) : Parcelable