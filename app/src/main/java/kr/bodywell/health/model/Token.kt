package kr.bodywell.health.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Token (
    var id: Int = 0,
    var userId: Int = 0,
    var access: String = "",
    var refresh: String = "",
    var accessCreated: String = "",
    var refreshCreated: String = ""
) : Parcelable