package kr.bodywell.android.model

data class Sleep(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String? = "",
    var startTime: String = "",
    var endTime: String = "",
    var total: Int = 0,
    var regDate: String = "",
    var isUpdated: Int = 0
)