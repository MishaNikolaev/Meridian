package com.nmichail.meridian.shared.chat.data.cipher

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

private val openedJson = Json { ignoreUnknownKeys = true }

internal fun encodeOpenedMessage(
	text: String,
	replyAuthor: String,
	replyText: String,
	fileId: String,
	fileName: String,
	fileType: String,
): String {
	if (replyAuthor.isEmpty() && replyText.isEmpty() && fileId.isEmpty()) {
		return text
	}

	return openedJson.encodeToString(
		OpenedMessage(
			text = text,
			replyAuthor = replyAuthor,
			replyText = replyText,
			fileId = fileId,
			fileName = fileName,
			fileType = fileType,
		),
	)
}

internal fun decodeOpenedMessage(decrypted: String): OpenedMessage {
	val stored = try {
		openedJson.decodeFromString<StoredOpenedMessage>(decrypted)
	} catch (_: SerializationException) {
		return openedText(decrypted)
	}

	if (stored.text == null) {
		return openedText(decrypted)
	}

	return OpenedMessage(
		text = stored.text,
		replyAuthor = stored.replyAuthor,
		replyText = stored.replyText,
		fileId = stored.fileId,
		fileName = stored.fileName,
		fileType = stored.fileType,
	)
}

private fun openedText(value: String) = OpenedMessage(
	text = value,
	replyAuthor = "",
	replyText = "",
	fileId = "",
	fileName = "",
	fileType = "",
)

@Serializable
private data class StoredOpenedMessage(
	val text: String? = null,
	val replyAuthor: String = "",
	val replyText: String = "",
	val fileId: String = "",
	val fileName: String = "",
	val fileType: String = "",
)
