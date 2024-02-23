package com.makebodywell.bodywell.model

import android.os.Parcelable

data class Note (
   var id: Int = 0,
   var userId: Int = 0,
   var title: String = "",
   var content: String = "",
   var status: Int = 1,
   var regDate: String = ""
)