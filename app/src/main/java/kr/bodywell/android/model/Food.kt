package kr.bodywell.android.model

import kr.bodywell.android.database.DBHelper.Companion.TYPE_USER

data class Food(
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