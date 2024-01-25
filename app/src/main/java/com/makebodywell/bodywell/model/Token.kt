package com.makebodywell.bodywell.model

data class Token (
    var id: Int = 0,
    var userId: Int = 0,
    var accessToken: String = "",
    var refreshToken: String = "",
    var regDate: String = ""
)