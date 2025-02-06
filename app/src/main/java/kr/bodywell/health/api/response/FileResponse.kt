package kr.bodywell.health.api.response

import com.google.gson.annotations.SerializedName

data class FileResponse(
   @SerializedName("id")
   var id: String = "",

   @SerializedName("name")
   var name: String = "",

   @SerializedName("mimetype")
   var mimetype: String = "",

   @SerializedName("filetype")
   var filetype: String = "",

   @SerializedName("size")
   var size: Int = 0,

   @SerializedName("data")
   var data: Int = 0,

   @SerializedName("createdAt")
   var createdAt: String = "",

   @SerializedName("updatedAt")
   var updatedAt: String = ""
)