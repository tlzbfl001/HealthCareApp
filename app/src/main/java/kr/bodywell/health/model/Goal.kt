package kr.bodywell.health.model

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