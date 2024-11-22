package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String = "",
    var idToken: String = "",
    var accessToken: String = "",
    var username: String = "",
    var email: String = "",
    var role: String = "",
    var uid: String = "",
    var profileUid: String = "",
    var deviceUid: String = ""
) : Parcelable

data class Profile(
    var id: String = "",
    var name: String? = "",
    var pictureUrl: String? = "",
    var birth: String? = "",
    var gender: String? = "",
    var height: Double? = 0.0,
    var weight: Double? = 0.0,
    var createdAt: String = "",
    var updatedAt: String = ""
)