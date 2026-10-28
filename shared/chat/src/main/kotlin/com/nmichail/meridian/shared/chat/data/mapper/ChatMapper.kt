package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.dto.ChatDto
import com.nmichail.meridian.shared.chat.domain.entity.Chat

internal fun ChatDto.toEntity(mentioned: Boolean): Chat {
	val latest = lastMessage?.toEntity(
		self = null,
		author = null,
	)
	return Chat(
		id = chatId,
		type = type.toEntity(),
		title = title.orEmpty(),
		preview = latest?.text.orEmpty(),
		sentAt = latest?.sentAt,
		unread = unreadCount,
		corporate = corporate,
		mentioned = mentioned,
		personId = personId,
		discussionRoot = latest?.threadRoot,
		discussionText = latest?.replyText,
	)
}