package com.makebodywell.bodywell.model

data class Food(
    var id: Int = 0,
    var name: String = "",
    var unit: String = "",
    var amount: String = "",
    var count: Int = 0,
    var kcal: String = "",
    var carbohydrate: String = "",
    var protein: String = "",
    var fat: String = "",
    var salt: String = "",
    var sugar: String = "",
    val star: Int = 0,
    var type: Int = 0,
    var regDate: String = "",
)
