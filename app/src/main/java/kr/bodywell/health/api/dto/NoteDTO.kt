package kr.bodywell.health.api.dto

import com.google.gson.annotations.SerializedName

data class NoteDTO (
	@SerializedName("title")
	var title: String = "",

	@SerializedName("content")
	var content: String = "",

	@SerializedName("emotion")
	var emotion: String,

	@SerializedName("date")
	var date: String,

	@SerializedName("createdAt")
	var createdAt: String = "",

	@SerializedName("updatedAt")
	var updatedAt: String = ""
)

data class NoteUpdateDTO (
	@SerializedName("title")
	var title: String = "",

	@SerializedName("content")
	var content: String = "",

	@SerializedName("emotion")
	var emotion: String
)