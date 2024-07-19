package kr.bodywell.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String = "",
    var email: String = "",
    var idToken: String = "",
    var userUid: String = "",
    var profileUid: String? = "",
    var deviceUid: String = "",
    var name: String? = "",
    var gender: String? = "",
    var birthday: String? = "",
    var image: String? = "",
    var height: Double? = 0.0,
    var weight: Double? = 0.0,
    var weightGoal: Double = 0.0,
    var kcalGoal: Int = 0,
    var waterGoal: Int = 0,
    var waterUnit: Int = 0,
    var measureHeight: Int = 0,
    var created: String = "",
    var updated: String = "",
    var isUpdated: Int = 0
) : Parcelable