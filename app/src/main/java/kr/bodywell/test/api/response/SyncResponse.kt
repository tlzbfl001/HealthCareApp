package kr.bodywell.test.api.response

import com.google.gson.annotations.SerializedName
import kr.bodywell.test.api.dto.SyncUpdateData

data class SyncResponse(
	@SerializedName("result")
	var result: SyncResult
)

data class SyncResult(
	@SerializedName("created")
	var created: List<SyncUpdateData>?,

	@SerializedName("updated")
	var updated: List<SyncUpdateData>?,

	@SerializedName("deleted")
	var deleted: List<SyncUpdateData>?,

	@SerializedName("rejected")
	var rejected: List<SyncRejected>?
)

data class SyncRejected(
	@SerializedName("error")
	var error: List<SyncError>
)

data class SyncError(
	@SerializedName("name")
	var name: String = "",

	@SerializedName("message")
	var message: String = ""
)