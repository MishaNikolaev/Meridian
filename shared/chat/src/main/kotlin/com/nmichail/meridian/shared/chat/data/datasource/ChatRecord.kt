package com.nmichail.meridian.shared.chat.data.datasource

import kotlinx.serialization.Serializable

@Serializable
internal data class ChatRecord(
	val id: String,
	val type: String,
	val title: String,
	val preview: String,
	val fileId: String = "",
	val fileName: String = "",
	val fileType: String = "",
	val sentAt: String?,
	val unread: Int,
	val corporate: Boolean,
	val mentioned: Boolean,
	val personId: String?,
	val discussionRoot: String?,
	val discussionText: String?,
	val discussionComment: String? = null,
	val discussionSentAt: String? = null,
)

@Serializable
internal data class ChatRecords(
	val items: List<ChatRecord>,
)
