package com.makebodywell.bodywell.model

data class Food(
    var id: Int = 0,
    var name: String? = null,
    var unit: String? = null,
    var amount: Int = 1,
    var kcal: String? = "0",
    var carbohydrate: String? = "0.0",
    var protein: String? = "0.0",
    var fat: String? = "0.0",
    var salt: String? = "0.0",
    var sugar: String? = "0.0",
    val star: Int = 0,
    var type: Int = 0,
    var regDate: String? = null,
)
