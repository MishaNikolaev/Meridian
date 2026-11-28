package com.nmichail.meridian.features.calls.catalog.presentation.compose.conference

internal data class FacePreview(
	val name: String,
	val photo: Int = 0,
	val photoId: String = "",
	val muted: Boolean = false,
	val speaking: Boolean = false,
	val live: Boolean = false,
)

private const val CODE_MIN = 100
private const val CODE_MAX = 999

internal fun inviteCode(): String {
	fun block(): String = (CODE_MIN..CODE_MAX).random().toString()
	return "${block()}-${block()}-${block()}"
}