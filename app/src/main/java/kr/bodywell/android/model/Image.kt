package kr.bodywell.android.model

import android.graphics.Bitmap

data class Image(
    var id: Int = 0,
    var userId: Int = 0,
    var uid: String = "",
    var type: String = "",
    var dataName: String = "",
    var imageName: String = "",
    var bitmap: Bitmap? = null,
    var createdAt: String = ""
)