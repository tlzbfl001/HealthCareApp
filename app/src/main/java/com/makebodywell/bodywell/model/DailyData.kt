package com.makebodywell.bodywell.model

data class DailyData(
    var id: Int = 0,
    var foodGoal: Int = 0,
    var waterGoal: Int = 0,
    var exerciseGoal: Int = 0,
    var bodyGoal: Double = 0.0,
    var drugGoal: Int = 0,
    var sleepHourGoal: Int = 0,
    var sleepMinuteGoal: Int = 0,
    var regDate: String = ""
)