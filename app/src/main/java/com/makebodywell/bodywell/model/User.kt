package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String? = "",
    var idToken: String? = "",
    var userId: String? = "",
    var deviceId: String? = "",
    var healthId: String? = "",
    var activityId: String? = "",
    var bodyMeasurementId: String? = "",
    var email: String? = "",
    var name: String? = "",
    var nickname: String? = "",
    var gender: String? = "",
    var birthday: String? = "",
    var profileImage: String? = "",
    var height: String? = "",
    var weight: String? = "",
    var weightGoal: String? = "",
    var kcalGoal: String? = "",
    var waterGoal: String? = "",
    var waterUnit: String? = "",
    var regDate: String? = ""
) : Parcelable