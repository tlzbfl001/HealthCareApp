package com.makebodywell.bodywell.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var type: String = "",
    var idToken: String? = "",
    var accessToken: String? = "",
    var email: String? = "",
    var name: String? = "",
    var nickname: String? = "",
    var gender: String? = "",
    var birthYear: String? = "",
    var birthDay: String? = "",
    var profileImage: String? = "",
    var regDate: String? = ""
) : Parcelable