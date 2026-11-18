package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.features.conversation.presentation.ConversationIntent.DraftFile

@Suppress("MagicNumber")
internal fun DraftFile.accepted(): Boolean =
	type.isNotEmpty() && bytes.isNotEmpty() && bytes.size <= 20 * 1024 * 1024

internal fun List<DraftFile>.attachedName(): String = when {
	isEmpty() -> ""
	any { file -> file.document } -> first().name
	all { file -> file.type.startsWith("image/") } -> ""
	else -> first().name
}

internal fun List<DraftFile>.spokenName(): String = firstOrNull()?.name.orEmpty()

internal fun List<DraftFile>.previews(): List<ByteArray> =
	filter { file -> file.type.startsWith("image/") }.map { file -> file.bytes }