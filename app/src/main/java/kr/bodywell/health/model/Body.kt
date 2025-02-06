package kr.bodywell.health.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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