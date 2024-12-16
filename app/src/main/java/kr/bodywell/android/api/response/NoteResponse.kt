package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class NoteResponse (
   @SerializedName("id")
   var id: String = "",

   @SerializedName("title")
   var title: String = "",

   @SerializedName("content")
   var content: String = "",

   @SerializedName("emotion")
   var emotion: String,

   @SerializedName("date")
   var date: String
)