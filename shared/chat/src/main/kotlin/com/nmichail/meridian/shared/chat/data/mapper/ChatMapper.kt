package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.datasource.CachedChat
import com.nmichail.meridian.shared.chat.data.dto.ChatDto
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.entity.ChatType

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

internal fun ChatDto.toProfile(
	owner: String,
	members: List<ChatPerson>,
	selfId: String,
) = ChatProfile(
	title = title.orEmpty(),
	description = description.orEmpty(),
	owner = owner,
	members = members,
	selfId = selfId,
)

internal fun Chat.toCached(): CachedChat = CachedChat(
	id = id,
	type = type.name,
	title = title,
	preview = preview,
	sentAt = sentAt,
	unread = unread,
	corporate = corporate,
	mentioned = mentioned,
	personId = personId,
	discussionRoot = discussionRoot,
	discussionText = discussionText,
)

internal fun CachedChat.toChat(): Chat? {
	val kind = try {
		ChatType.valueOf(type)
	} catch (_: IllegalArgumentException) {
		return null
	}

	return Chat(
		id = id,
		type = kind,
		title = title,
		preview = preview,
		sentAt = sentAt,
		unread = unread,
		corporate = corporate,
		mentioned = mentioned,
		personId = personId,
		discussionRoot = discussionRoot,
		discussionText = discussionText,
	)
}