package kr.bodywell.android.model

data class InitWater(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String? = "",
    var count: Int = 0,
    var volume: Int = 200,
    var createdAt: String = "",
    var isUpdated: Int = 0
)

data class Water(
    var id: String = "",
    var mL: Int = 0,
    var count: Int = 0,
    var date: String = ""
)