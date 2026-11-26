package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.cipher.opened
import com.nmichail.meridian.shared.chat.data.datasource.ChatRecord
import com.nmichail.meridian.shared.chat.data.dto.ChatDto
import com.nmichail.meridian.shared.chat.data.dto.MessageDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionDto
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatPerson
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.markDeleted
import com.nmichail.meridian.shared.chat.domain.entity.noticeText
import com.nmichail.meridian.shared.chat.domain.entity.toPoll
import com.nmichail.meridian.shared.chat.domain.entity.toVote

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

fun ChatDto.toProfile(
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

internal fun MessageDto.toEntity(self: String?, author: String?): ChatLine {
	val serviceText = notice?.let(::noticeText)
	val opened = if (serviceText == null) opened() else null
	val text = serviceText ?: opened?.text.orEmpty()
	val line = ChatLine(
		id = messageId,
		mine = senderUserId == self && serviceText == null,
		text = text,
		sentAt = createdAt,
		author = author?.takeIf { senderUserId != self && serviceText == null },
		authorId = senderUserId,
		replyAuthor = opened?.replyAuthor?.takeIf { reply -> reply.isNotEmpty() },
		replyText = opened?.replyText?.takeIf { reply -> reply.isNotEmpty() },
		threadRoot = threadRoot,
		fileId = opened?.fileId.orEmpty(),
		fileName = opened?.fileName.orEmpty(),
		fileType = opened?.fileType.orEmpty(),
		poll = text.toPoll(),
		vote = text.toVote(),
		deleted = false,
		edited = false,
		service = serviceText != null,
	)

	return if (deleted) {
		line.markDeleted()
	} else {
		line
	}
}

internal fun ReactionDto.toEntity(name: String, mine: Boolean): ChatReaction =
	ChatReaction(
		userId = userId,
		name = name,
		reaction = reaction,
		sentAt = createdAt,
		mine = mine,
	)