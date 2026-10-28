package com.nmichail.meridian.features.conversation.presentation

import com.nmichail.meridian.component.ui.emoji.MessageReaction

fun reactionGlyph(token: String): String {
	val named = MessageReaction.entries.firstOrNull { reaction ->
		reaction.name.equals(token, ignoreCase = true)
	}
	return named?.glyph ?: token
}