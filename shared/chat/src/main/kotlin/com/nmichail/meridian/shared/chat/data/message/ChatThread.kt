package com.nmichail.meridian.shared.chat.data.message

import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.MessagePageDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread

internal fun MessagePageDto.ownReactions(self: String?): Map<String, Set<String>> =
	items.mapNotNull { item ->
		val reactions = item.reactions
			.filter { reaction -> reaction.userId == self }
			.map { reaction -> reaction.reaction }
			.toSet()

		if (reactions.isEmpty()) {
			null
		} else {
			item.messageId to reactions
		}
	}.toMap()

internal fun MessagePageDto.thread(
	details: ChatDetailsDto,
	self: String?,
	dropped: Set<String>,
	saved: Map<String, Set<String>>,
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
		reactions = savedReactions(self, saved, lines),
		reactionViewers = reactionViewers(self, names),
		people = details.members
			.filter { member -> member.userId != self }
			.map { member -> member.toEntity() },
		selfName = names[self].orEmpty(),
	)
}

private fun MessagePageDto.savedReactions(
	self: String?,
	saved: Map<String, Set<String>>,
	lines: List<ChatLine>,
): Map<String, Set<String>> {
	val added = ownReactions(self).filterKeys { messageId -> messageId !in saved }

	return (saved + added).filterKeys { messageId -> lines.any { line -> line.id == messageId } }
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