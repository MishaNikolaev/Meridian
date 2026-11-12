package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion

internal fun List<Chat>.discussions(): List<ChatDiscussion> {
	val discussions = mutableListOf<ChatDiscussion>()

	for (chat in this) {
		val rootId = chat.discussionRoot ?: continue

		val topic = chat.discussionText
		val text: String
		val comment: String

		if (topic.isNullOrEmpty()) {
			text = chat.preview
			comment = ""
		} else if (topic == chat.preview) {
			text = topic
			comment = ""
		} else {
			text = topic
			comment = chat.preview
		}

		discussions += ChatDiscussion(
			chatId = chat.id,
			messageId = rootId,
			title = chat.title,
			text = text,
			comment = comment,
			sentAt = chat.sentAt,
			corporate = chat.corporate,
			type = chat.type,
			personId = chat.personId,
		)
	}

	return discussions.sortedByDescending { discussion -> discussion.sentAt.orEmpty() }
}