package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

internal fun List<ChatLine>.keepingEdits(edited: List<ChatLine>): List<ChatLine> {
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

internal fun ChatLine.markDeleted(): ChatLine = copy(
	text = "Сообщение удалено",
	deleted = true,
	edited = false,
	replyAuthor = null,
	replyText = null,
)