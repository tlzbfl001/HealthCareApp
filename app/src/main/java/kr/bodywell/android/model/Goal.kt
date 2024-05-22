package kr.bodywell.android.model

data class Goal(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var foodGoal: Int = 0,
    var waterGoal: Int = 0,
    var exerciseGoal: Int = 0,
    var bodyGoal: Double = 0.0,
    var sleepGoal: Int = 0,
    var drugGoal: Int = 0,
    var regDate: String = "",
    var isUpdated: Int = 0
)