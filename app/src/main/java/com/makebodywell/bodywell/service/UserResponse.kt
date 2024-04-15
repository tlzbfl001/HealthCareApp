package com.makebodywell.bodywell.service

import android.os.Parcelable
import androidx.health.connect.client.records.StepsRecord
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
	var uid: String = "",
	var type: String = "",
	var email: String = "",
	var username: String? = "",
	var emailVerified: Boolean = false,
) : Parcelable