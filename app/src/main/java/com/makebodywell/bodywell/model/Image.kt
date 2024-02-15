package com.makebodywell.bodywell.model

data class Image(
    var id: Int = 0,
    var userId: Int = 0,
    var imageUri: String = "",
    var type: Int = 0,
    var dataId: Int = 0,
    var regDate: String = ""
)