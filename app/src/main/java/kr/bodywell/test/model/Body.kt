package kr.bodywell.test.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Body(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String? = "",
    var height: Double? = null,
    var weight: Double? = null,
    var intensity: Int = 1,
    var fat: Double? = null,
    var muscle: Double? = null,
    var bmi: Double? = null,
    var bmr: Double? = null,
    var createdAt: String = "",
    var isUpdated: Int = 0
) : Parcelable