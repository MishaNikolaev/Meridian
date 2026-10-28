package com.nmichail.meridian.shared.chat.data

import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.MessagePageDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.data.message.markDeleted

internal fun MessagePageDto.ownReactions(self: String?): Map<String, String> =
	items.mapNotNull { item ->
		val reaction = item.reactions.firstOrNull { reaction -> reaction.userId == self }?.reaction
		if (reaction == null) {
			null
		} else {
			item.messageId to reaction
		}
	}.toMap()

internal fun MessagePageDto.thread(
	details: ChatDetailsDto,
	self: String?,
	dropped: Set<String>,
	known: Map<String, String>,
): ChatThread {
	val names = details.members.associate { member -> member.userId to member.displayName }
	val lines = items.map { item ->
		val line = item.toEntity(
			self = self,
			author = names[item.senderUserId],
		)
		if (item.messageId in dropped) {
			line.markDeleted()
		} else {
			line
		}
	}
	return ChatThread(
		lines = lines,
		members = details.members.size,
		readUpTo = items.lastOrNull { item -> !item.deleted }?.messageId,
		reactions = savedReactions(self, known, lines),
		reactionViewers = reactionViewers(self, names),
		people = details.members
			.filter { member -> member.userId != self }
			.map { member -> member.toEntity() },
	)
}

private fun MessagePageDto.savedReactions(
	self: String?,
	known: Map<String, String>,
	lines: List<ChatLine>,
): Map<String, String> {
	val added = ownReactions(self).filterKeys { messageId -> messageId !in known }
	return (known + added).filterKeys { messageId -> lines.any { line -> line.id == messageId } }
}

private fun MessagePageDto.reactionViewers(
	self: String?,
	names: Map<String, String>,
): Map<String, List<ChatReaction>> =
	items.associate { item ->
		item.messageId to item.reactions.map { reaction ->
			reaction.toEntity(
				name = names[reaction.userId].orEmpty(),
				mine = reaction.userId == self,
			)
		}
	}.filterValues { reactions -> reactions.isNotEmpty() }
