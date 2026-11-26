package com.nmichail.meridian.shared.chat.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SavedOpenedMessage(
	val text: String?,
	val replyAuthor: String?,
	val replyText: String?,
	val fileId: String?,
	val fileName: String?,
	val fileType: String?,
)
