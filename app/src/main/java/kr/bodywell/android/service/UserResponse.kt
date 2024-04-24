package kr.bodywell.android.service

import android.os.Parcelable
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