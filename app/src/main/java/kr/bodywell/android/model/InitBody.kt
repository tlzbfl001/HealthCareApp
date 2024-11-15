package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InitBody(
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

@Parcelize
data class Body(
    var id: String? = "",
    var height: Double? = null,
    var weight: Double? = null,
    var bodyMassIndex: Double? = null,
    var bodyFatPercentage: Double? = null,
    var skeletalMuscleMass: Double? = null,
    var basalMetabolicRate: Double? = null,
    var workoutIntensity: Int? = 1,
    var time: String? = "",
    var createdAt: String? = "",
    var updatedAt: String? = "",
    var deletedAt: String? = ""
) : Parcelable