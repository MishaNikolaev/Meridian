package com.nmichail.meridian.shared.chat.data.cipher

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
internal data class OpenedMessage(
	val text: String,
	val replyAuthor: String,
	val replyText: String,
)

internal fun encodeOpenedMessage(text: String, replyAuthor: String, replyText: String): String {
	if (replyAuthor.isEmpty() && replyText.isEmpty()) {
		return text
	}
	return Json.encodeToString(
		OpenedMessage(
			text = text,
			replyAuthor = replyAuthor,
			replyText = replyText,
		),
	)
}

internal fun decodeOpenedMessage(decrypted: String): OpenedMessage =
	try {
		Json.decodeFromString(decrypted)
	} catch (_: SerializationException) {
		OpenedMessage(
			text = decrypted,
			replyAuthor = "",
			replyText = "",
		)
	}