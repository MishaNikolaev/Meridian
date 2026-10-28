package com.nmichail.meridian.shared.chat.data.message

import com.nmichail.meridian.shared.chat.domain.entity.ChatLine

internal fun ChatLine.markDeleted(): ChatLine = copy(
	text = "Сообщение удалено",
	deleted = true,
	edited = false,
	replyAuthor = null,
	replyText = null,
)