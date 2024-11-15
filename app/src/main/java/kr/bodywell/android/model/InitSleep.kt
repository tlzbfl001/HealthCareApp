package kr.bodywell.android.model

data class InitSleep(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var isUpdated: Int = 0
)

data class Sleep(
    var id: String = "",
    var starts: String = "",
    var ends: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
    var deletedAt: String = ""
)