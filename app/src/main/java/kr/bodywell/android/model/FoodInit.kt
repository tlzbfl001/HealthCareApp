package kr.bodywell.android.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER

data class FoodInit(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var registerType: String = TYPE_USER,
    var type: String = "",
    var name: String = "",
    var unit: String = "",
    var amount: Int = 0,
    var calorie: Int = 0,
    var carbohydrate: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var salt: Double = 0.0,
    var sugar: Double = 0.0,
    var count: Int = 1,
    var useCount: Int = 0,
    var useDate: String = "",
    var createdAt: String = "",
    var isUpdated: Int = 0
)

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

data class FoodUsages(
    var id: String = "",
    var usageCount: Int = 0,
    var createdAt: String = "",
    var updatedAt: String = "",
    var foodId: String = "",
    var userId: String = "",
)