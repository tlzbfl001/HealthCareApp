package kr.bodywell.android.model

import android.graphics.Bitmap

data class FileItem(
    var id: String = "",
    var name: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    var data: String = "",
    var bitmap: Bitmap? = null,
    var dietId: String = "",
    var profileId: String = ""
)