package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion

internal fun List<Chat>.discussions(): List<ChatDiscussion> {
	val discussions = mutableListOf<ChatDiscussion>()
	for (chat in this) {
		val rootId = chat.discussionRoot ?: continue
		val rootText = chat.discussionText?.takeIf { text -> text.isNotEmpty() } ?: chat.preview
		discussions += ChatDiscussion(
			chatId = chat.id,
			messageId = rootId,
			title = chat.title,
			text = rootText,
			comment = chat.preview,
			sentAt = chat.sentAt,
			corporate = chat.corporate,
			type = chat.type,
			personId = chat.personId,
		)
	}
	return discussions.sortedByDescending { discussion -> discussion.sentAt.orEmpty() }
}