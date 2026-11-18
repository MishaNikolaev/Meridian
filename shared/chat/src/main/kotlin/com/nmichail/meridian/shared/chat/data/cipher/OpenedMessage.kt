package com.nmichail.meridian.shared.chat.data.cipher

import kotlinx.serialization.Serializable

@Serializable
internal data class OpenedMessage(
	val text: String,
	val replyAuthor: String,
	val replyText: String,
	val fileId: String,
	val fileName: String,
	val fileType: String,
)