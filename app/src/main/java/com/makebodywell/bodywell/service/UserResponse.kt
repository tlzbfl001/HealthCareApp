package com.makebodywell.bodywell.service

import android.os.Parcelable
import androidx.health.connect.client.records.StepsRecord
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
	@SerializedName("uid")
	var uid: String = "",

	var type: String = "",

	@SerializedName("email")
	var email: String = "",

	@SerializedName("username")
	var username: String? = "",

	@SerializedName("emailVerified")
	var emailVerified: Boolean = false,
) : Parcelable


data class DataResponse(
	@SerializedName("code")
	var code: String = "",

	@SerializedName("message")
	var message: String = "",

	@SerializedName("statusCode")
	var statusCode: String = "",
)