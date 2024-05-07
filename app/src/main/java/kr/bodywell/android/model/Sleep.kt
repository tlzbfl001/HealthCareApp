package kr.bodywell.android.model

data class Sleep(
    var id: Int = 0,
    var userId: Int = 0,
    var sleepUid: String? = "",
    var bedTime: String = "",
    var wakeTime: String = "",
    var sleepTime: Int = 0,
    var regDate: String = ""
)