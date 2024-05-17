package kr.bodywell.android.model

data class Image(
    var id: Int = 0,
    var userId: Int = 0,
    var type: String = "",
    var dataId: Int = 0,
    var imageUri: String = "",
    var regDate: String = ""
)