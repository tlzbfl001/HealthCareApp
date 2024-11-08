package kr.bodywell.android.model

data class GoalInit(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var food: Int = 0,
    var waterVolume: Int = 0,
    var water: Int = 0,
    var exercise: Int = 0,
    var body: Double = 0.0,
    var sleep: Int = 0,
    var drug: Int = 0,
    var createdAt: String = "",
    var isUpdated: Int = 0
)

data class Goal(
    var id: String = "",
    var weight: Double = 0.0,
    var kcalOfDiet: Int = 0,
    var kcalOfWorkout: Int = 0,
    var waterAmountOfCup: Int = 0,
    var waterIntake: Int = 0,
    var sleep: Int = 0,
    var medicineIntake: Int = 0,
    var date: String = "",
    var createdAt: String = "",
    var updatedAt: String = "",
    var deletedAt: String = "",
    var userId: String = ""
)