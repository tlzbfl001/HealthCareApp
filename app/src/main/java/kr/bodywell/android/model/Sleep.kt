package kr.bodywell.android.model

data class Sleep(
    var id: Int = 0,
    var userId: Int = 0,
    var bedTime: Int = 0,
    var wakeTime: Int = 0,
    var sleepTime: Int = 0,
    var regDate: String = "",
)