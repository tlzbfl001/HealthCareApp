package com.makebodywell.bodywell.model

data class Food(
    var id: Int = 0,
    var name: String = "",
    var unit: Int = 0,
    var amount: Int = 1,
    var kcal: Int = 0,
    var carbohydrate: Double = 0.0,
    var protein: Double = 0.0,
    var fat: Double = 0.0,
    var salt: Double = 0.0,
    var sugar: Double = 0.0,
    val star: Int = 0,
    var type: Int = 0,
    var regDate: String = "",
)
