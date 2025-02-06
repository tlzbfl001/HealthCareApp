package kr.bodywell.health.model

data class Note (
   var id: String = "",
   var title: String = "",
   var content: String = "",
   var emotion: String = "",
   var date: String = "",
   var createdAt: String = "",
   val updatedAt: String = ""
)