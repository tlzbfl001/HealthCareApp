package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Body(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String? = "",
    var height: Double = 0.0,
    var weight: Double = 0.0,
    var intensity: Int = 0,
    var fat: Double = 0.0,
    var muscle: Double = 0.0,
    var bmi: Double = 0.0,
    var bmr: Double = 0.0,
    var createdAt: String = "",
    var isUpdated: Int = 0
) : Parcelable