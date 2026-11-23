package com.nmichail.meridian.features.conversation.presentation

private const val FILE_LIMIT = 20 * 1024 * 1024

data class DraftFile(
	val name: String,
	val type: String,
	val bytes: ByteArray,
	val document: Boolean,
)

fun DraftFile.accepted(): Boolean =
	type.isNotEmpty() && bytes.isNotEmpty() && bytes.size <= FILE_LIMIT

fun List<DraftFile>.attachedName(): String = when {
	isEmpty() -> ""
	any { file -> file.document } -> first().name
	all { file -> file.type.startsWith("image/") } -> ""
	else -> first().name
}

fun List<DraftFile>.spokenName(): String = firstOrNull()?.name.orEmpty()

fun List<DraftFile>.previews(): List<ByteArray> =
	filter { file -> file.type.startsWith("image/") }.map { file -> file.bytes }
