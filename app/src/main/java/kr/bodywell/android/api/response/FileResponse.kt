package kr.bodywell.android.api.response

import com.google.gson.annotations.SerializedName

data class FileResponse(
	@SerializedName("id")
	var id: String = "",

	@SerializedName("name")
	var name: String = "",

	@SerializedName("originalName")
	var originalName: String = "",

	@SerializedName("contentType")
	var contentType: String = "",

	@SerializedName("size")
	var size: Int = 0,

	@SerializedName("url")
	var url: String = "",

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = "",

	@SerializedName("uploaderId")
	var uploaderId: String = ""
)