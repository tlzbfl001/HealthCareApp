package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token (
    var id: Int = 0,
    var userId: Int = 0,
    var accessToken: String = "",
    var refreshToken: String = "",
    var accessTokenRegDate: String = "",
    var refreshTokenRegDate: String = ""
) : Parcelable