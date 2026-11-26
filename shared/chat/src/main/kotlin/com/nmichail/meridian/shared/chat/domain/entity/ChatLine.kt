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
	val fileId: String,
	val fileName: String,
	val fileType: String,
	val poll: Poll?,
	val vote: Vote?,
)

fun ChatLine.markDeleted(): ChatLine = copy(
	text = "Сообщение удалено",
	deleted = true,
	edited = false,
	replyAuthor = null,
	replyText = null,
)
