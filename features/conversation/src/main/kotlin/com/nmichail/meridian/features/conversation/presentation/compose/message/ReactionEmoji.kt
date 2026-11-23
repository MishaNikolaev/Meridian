package com.nmichail.meridian.features.conversation.presentation.compose.message

import com.nmichail.meridian.component.ui.emoji.MessageReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread

private const val USED_LIMIT = 32

fun reactionEmoji(token: String): String {
	val named = MessageReaction.entries.firstOrNull { reaction ->
		reaction.name.equals(token, ignoreCase = true)
	}

	return named?.emoji ?: token
}

fun reactionEmojis(saved: Map<String, Set<String>>): Map<String, Set<String>> = saved
	.mapValues { (_, tokens) ->
		tokens.map { token -> reactionEmoji(token) }.filter { emoji -> emoji.isNotBlank() }.toSet()
	}

internal fun List<ChatReaction>.mineReaction(
	emoji: String,
	sentAt: String,
	selfName: String,
): List<ChatReaction> {
	val others = filter { viewer -> !viewer.mine }

	val mine = filter { viewer -> viewer.mine }

	val next = if (mine.any { viewer -> reactionEmoji(viewer.reaction) == emoji }) {
		mine.filter { viewer -> reactionEmoji(viewer.reaction) != emoji }
	} else {
		val known = mine.firstOrNull { viewer -> viewer.name.isNotBlank() }?.name.orEmpty()
		val knownId = mine.firstOrNull { viewer -> viewer.userId.isNotEmpty() }?.userId.orEmpty()

		mine + ChatReaction(
			userId = knownId,
			name = selfName.ifBlank { known },
			reaction = emoji,
			sentAt = sentAt,
			mine = true,
		)
	}

	return others + next
}

internal fun Map<String, List<ChatReaction>>.mergeViewers(
	current: Map<String, List<ChatReaction>>,
	selfName: String,
): Map<String, List<ChatReaction>> = (keys + current.keys).associate { id ->
	val viewers = if (id !in current) {
		get(id).orEmpty()
	} else {
		val mine = current.getValue(id).filter { viewer -> viewer.mine }
		val others = get(id).orEmpty().filter { viewer -> !viewer.mine }

		others + mine
	}

	id to viewers.map { viewer -> viewer.named(selfName) }
}

private fun ChatReaction.named(selfName: String): ChatReaction {
	if (selfName.isEmpty() || !mine || name.isNotBlank()) {
		return this
	}

	return copy(name = selfName)
}

internal fun ChatThread.faceIds(): List<String> {
	val authors = lines.map { line -> line.authorId }
	val reactors = reactionViewers.values.flatten().map { viewer -> viewer.userId }

	return authors + reactors
}

internal fun List<String>.usedReaction(emoji: String, active: Boolean): List<String> {
	if (!active) {
		return this
	}

	return (this + emoji).takeLast(USED_LIMIT)
}