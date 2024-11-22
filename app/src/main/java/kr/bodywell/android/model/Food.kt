package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Food(
    var id: String = "",
    var mealTime: String = "",
    var name: String = "",
    var calorie: Int = 0,
    var carbohydrate: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var quantity: Int = 1,
    var quantityUnit: String = "개",
    var volume: Int = 0,
    var volumeUnit: String = "",
    var registerType: String = "USER",
    var date: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
    var deletedAt: String = "",
    var userId: String = "",
    var foodId: String = ""
) : Parcelable