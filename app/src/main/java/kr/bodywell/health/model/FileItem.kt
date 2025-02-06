package kr.bodywell.health.model

import android.graphics.Bitmap

data class FileItem(
	var id: String = "",
	var name: String = "",
	val createdAt: String = "",
	val updatedAt: String = "",
	var data: String = "",
	var bitmap: Bitmap? = null,
	var profileId: String = "",
	var dietId: String = "",
	var noteId: String = ""
)