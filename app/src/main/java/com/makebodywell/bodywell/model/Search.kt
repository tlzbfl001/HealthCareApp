package com.makebodywell.bodywell.model

data class Search (
	var id: Int = 0,
	var userId: Int = 0,
	var type: String = "",
	var name: String = "",
	var count: Int = 1,
	var useDate: String = ""
)