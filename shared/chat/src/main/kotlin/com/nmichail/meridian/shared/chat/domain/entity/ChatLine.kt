package com.nmichail.meridian.shared.chat.domain.entity

data class ChatLine(
	val id: String,
	val mine: Boolean,
	val text: String,
	val sentAt: String?,
	val author: String?,
	val authorId: String,
	val replyAuthor: String?,
	val replyText: String?,
	val threadRoot: String?,
	val deleted: Boolean,
	val edited: Boolean,
	val service: Boolean,
)
