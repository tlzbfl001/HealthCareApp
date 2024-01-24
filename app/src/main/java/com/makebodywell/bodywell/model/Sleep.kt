package com.makebodywell.bodywell.model

data class Sleep(
    var id: Int = 0,
    var sleepHour: Int = 0,
    var sleepMinute: Int = 0,
    var bedHour: Int = 0,
    var bedMinute: Int = 0,
    var wakeHour: Int = 0,
    var wakeMinute: Int = 0,
    var regDate: String = "",
)