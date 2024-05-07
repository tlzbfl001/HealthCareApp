package kr.bodywell.android.model

data class Water(
    var id: Int = 0,
    var userId: Int = 0,
    var waterUid: String = "",
    var count: Int = 0,
    var mL: Int = 200,
    var regDate: String = ""
)