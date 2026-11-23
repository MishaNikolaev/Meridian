package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.datasource.ChatRecord
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
	val discussion = lastDiscussion?.toEntity(
		self = null,
		author = null,
	)
	return Chat(
		id = chatId,
		type = type.toEntity(),
		title = title.orEmpty(),
		preview = latest?.text.orEmpty(),
		fileId = latest?.fileId.orEmpty(),
		fileName = latest?.fileName.orEmpty(),
		fileType = latest?.fileType.orEmpty(),
		sentAt = latest?.sentAt,
		unread = unreadCount,
		corporate = corporate,
		mentioned = mentioned,
		personId = personId,
		discussionRoot = discussion?.threadRoot,
		discussionText = discussion?.replyText,
		discussionComment = discussion?.text,
		discussionSentAt = discussion?.sentAt,
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
	photoId = photoId.orEmpty(),
)

internal fun Chat.toRecord(): ChatRecord = ChatRecord(
	id = id,
	type = type.name,
	title = title,
	preview = preview,
	fileId = fileId,
	fileName = fileName,
	fileType = fileType,
	sentAt = sentAt,
	unread = unread,
	corporate = corporate,
	mentioned = mentioned,
	personId = personId,
	discussionRoot = discussionRoot,
	discussionText = discussionText,
	discussionComment = discussionComment,
	discussionSentAt = discussionSentAt,
)

internal fun ChatRecord.toChat(): Chat? {
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
		fileId = fileId,
		fileName = fileName,
		fileType = fileType,
		sentAt = sentAt,
		unread = unread,
		corporate = corporate,
		mentioned = mentioned,
		personId = personId,
		discussionRoot = discussionRoot,
		discussionText = discussionText,
		discussionComment = discussionComment,
		discussionSentAt = discussionSentAt,
	)
}