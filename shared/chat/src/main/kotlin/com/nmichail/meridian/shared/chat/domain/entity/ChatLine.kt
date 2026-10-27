package com.nmichail.meridian.shared.chat.domain.entity

data class ChatLine(
	val id: String,
	val mine: Boolean,
	val text: String,
	val sentAt: String?,
	val author: String? = null,
	val replyAuthor: String? = null,
	val replyText: String? = null,
	val threadRoot: String? = null,
	val deleted: Boolean = false,
	val edited: Boolean = false,
)

fun List<ChatLine>.keepingEdits(edited: List<ChatLine>): List<ChatLine> {
	val saved = edited.filter { line -> line.edited }.associateBy { line -> line.id }
	return map { line ->
		val edit = saved[line.id]
		if (edit == null || line.deleted) {
			line
		} else {
			line.copy(text = edit.text, edited = true)
		}
	}
}

fun ChatLine.markDeleted(): ChatLine = copy(
	text = "Сообщение удалено",
	deleted = true,
	edited = false,
	replyAuthor = null,
	replyText = null,
)
