package com.nmichail.meridian.shared.chat.domain.entity

data class ChatDiscussion(
	val chatId: String,
	val messageId: String,
	val title: String,
	val text: String,
	val comment: String,
	val sentAt: String?,
	val corporate: Boolean,
	val type: ChatType,
	val personId: String?,
)
