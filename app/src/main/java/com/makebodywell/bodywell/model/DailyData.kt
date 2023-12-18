package com.makebodywell.bodywell.model

data class DailyData(
    var id: Int = 0,
    var foodGoal: Int = 0,
    var waterGoal: Int = 0,
    var exerciseGoal: Int = 0,
<<<<<<< HEAD
    var bodyGoal: Double = 0.0,
=======
    var bodyGoal: Int = 0,
>>>>>>> e5f18d1dfc2f1449657445a53cc6b46714d681ac
    var sleepGoal: Int = 0,
    var drugGoal: Int = 0,
    var regDate: String = ""
)