package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String = "",
    var email: String = "",
    var idToken: String = "",
    var accessToken: String = "",
    var uid: String = "",
    var deviceUid: String = "",
    var name: String? = "",
    var gender: String? = "",
    var birthday: String? = "",
    var profileImage: String? = "",
    var height: Double? = 0.0,
    var weight: Double? = 0.0,
    var weightGoal: Double = 0.0,
    var kcalGoal: Int = 0,
    var waterGoal: Int = 0,
    var waterUnit: Int = 0,
    var measureHeight: Int = 0,
    var isUpdated: Int = 0
) : Parcelable