package kr.bodywell.test.model

data class Sleep(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var isUpdated: Int = 0
)