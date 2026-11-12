package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.component.ui.emoji.MessageReaction
import com.nmichail.meridian.shared.chat.domain.entity.ChatReaction

private const val USED_LIMIT = 32

fun reactionGlyph(token: String): String {
	val named = MessageReaction.entries.firstOrNull { reaction ->
		reaction.name.equals(token, ignoreCase = true)
	}

	return named?.glyph ?: token
}

fun reactionGlyphs(saved: Map<String, Set<String>>): Map<String, Set<String>> = saved
	.mapValues { (_, tokens) ->
		tokens.map { token -> reactionGlyph(token) }.filter { glyph -> glyph.isNotBlank() }.toSet()
	}

internal fun List<ChatReaction>.mineGlyph(
	glyph: String,
	sentAt: String,
	selfName: String,
): List<ChatReaction> {
	val others = filter { viewer -> !viewer.mine }

	val mine = filter { viewer -> viewer.mine }

	val next = if (mine.any { viewer -> reactionGlyph(viewer.reaction) == glyph }) {
		mine.filter { viewer -> reactionGlyph(viewer.reaction) != glyph }
	} else {
		val known = mine.firstOrNull { viewer -> viewer.name.isNotBlank() }?.name.orEmpty()

		mine + ChatReaction(
			name = selfName.ifBlank { known },
			reaction = glyph,
			sentAt = sentAt,
			mine = true,
		)
	}

	return others + next
}

internal fun List<String>.usedGlyph(glyph: String, active: Boolean): List<String> {
	if (!active) {
		return this
	}

	return (this + glyph).takeLast(USED_LIMIT)
}