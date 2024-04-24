package kr.bodywell.android.model

data class Food(
    var id: Int = 0,
    var userId: Int = 0,
    var type: Int = 0,
    var name: String = "",
    var unit: String = "",
    var amount: Int = 0,
    var kcal: Int = 0,
    var carbohydrate: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var salt: Double = 0.0,
    var sugar: Double = 0.0,
    var count: Int = 1,
    var useCount: Int = 0,
    var regDate: String = "",
    var useDate: String = ""
)