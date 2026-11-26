package com.nmichail.meridian.shared.chat.data.cipher

import com.nmichail.meridian.shared.chat.data.model.SavedOpenedMessage
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
	val saved = try {
		openedJson.decodeFromString<SavedOpenedMessage>(decrypted)
	} catch (_: SerializationException) {
		return openedText(decrypted)
	}

	if (saved.text == null) {
		return openedText(decrypted)
	}

	return OpenedMessage(
		text = saved.text,
		replyAuthor = saved.replyAuthor.orEmpty(),
		replyText = saved.replyText.orEmpty(),
		fileId = saved.fileId.orEmpty(),
		fileName = saved.fileName.orEmpty(),
		fileType = saved.fileType.orEmpty(),
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
