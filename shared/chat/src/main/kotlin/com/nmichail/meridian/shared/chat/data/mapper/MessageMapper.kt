package com.nmichail.meridian.shared.chat.data.mapper

import com.nmichail.meridian.shared.chat.data.cipher.opened
import com.nmichail.meridian.shared.chat.data.dto.MessageDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionDto
import com.nmichail.meridian.shared.chat.data.message.markDeleted
import com.nmichail.meridian.shared.chat.data.message.noticeText
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

internal fun MessageDto.toEntity(self: String?, author: String?): ChatLine {
	val serviceText = notice?.let(::noticeText)

	val opened = if (serviceText == null) opened() else null

	val line = ChatLine(
		id = messageId,
		mine = senderUserId == self && serviceText == null,
		text = serviceText ?: opened?.text.orEmpty(),
		sentAt = createdAt,
		author = author?.takeIf { senderUserId != self && serviceText == null },
		authorId = senderUserId,
		replyAuthor = opened?.replyAuthor?.takeIf { reply -> reply.isNotEmpty() },
		replyText = opened?.replyText?.takeIf { reply -> reply.isNotEmpty() },
		threadRoot = threadRoot,
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
		name = name,
		reaction = reaction,
		sentAt = createdAt,
		mine = mine,
	)
