package kr.bodywell.android.model

data class Note (
   var id: Int = 0,
   var userId: Int = 0,
   var title: String = "",
   var content: String = "",
   var status: Int = 1,
   var created: String = ""
)