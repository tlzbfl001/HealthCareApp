package kr.bodywell.android.model

data class Water(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String? = "",
    var count: Int = 0,
    var volume: Int = 200,
    var created: String = "",
    var isUpdated: Int = 0
)