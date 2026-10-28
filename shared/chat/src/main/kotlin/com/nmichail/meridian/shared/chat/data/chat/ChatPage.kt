package com.nmichail.meridian.shared.chat.data.chat

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.dto.ChatPageDto
import com.nmichail.meridian.shared.chat.data.dto.ChatTypeDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.data.mention.mentioned
import com.nmichail.meridian.shared.chat.data.mention.namedMention
import com.nmichail.meridian.shared.chat.domain.entity.Chat

internal suspend fun ChatPageDto.toChats(api: ChatApi, self: String?): List<Chat> {
	val username = username(api, self)
	return items.map { item ->
		val preview = item.lastMessage?.toEntity(
			self = null,
			author = null,
		)?.text.orEmpty()
		val chat = item.toEntity(
			mentioned = item.type != ChatTypeDto.DIRECT &&
				item.unreadCount > 0 &&
				mentioned(
					text = preview,
					username = username,
				),
		)
		chat.copy(title = item.title ?: item.personName ?: "Чат")
	}
}

private suspend fun ChatPageDto.username(api: ChatApi, self: String?): String {
	if (self == null) {
		return ""
	}
	val sample = items.firstOrNull { item ->
		item.type != ChatTypeDto.DIRECT &&
			item.unreadCount > 0 &&
			namedMention(
				item.lastMessage?.toEntity(
					self = null,
					author = null,
				)?.text.orEmpty(),
			)
	} ?: return ""
	return api.getChat(sample.chatId).members
		.firstOrNull { member -> member.userId == self }
		?.username
		.orEmpty()
}
