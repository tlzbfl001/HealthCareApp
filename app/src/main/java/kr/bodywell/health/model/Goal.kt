package kr.bodywell.health.model

data class Goal(
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
    var created: String = "",
    var isUpdated: Int = 0
)