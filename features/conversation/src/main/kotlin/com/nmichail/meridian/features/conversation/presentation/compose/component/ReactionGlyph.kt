package com.nmichail.meridian.features.conversation.presentation.compose.component

import com.nmichail.meridian.component.ui.emoji.MessageReaction

fun reactionGlyph(token: String): String {
	val named = MessageReaction.entries.firstOrNull { reaction ->
		reaction.name.equals(token, ignoreCase = true)
	}
	return named?.glyph ?: token
}

fun reactionGlyphs(saved: Map<String, String>): Map<String, String> = saved
	.mapValues { (_, token) -> reactionGlyph(token) }
	.filterValues { glyph -> glyph.isNotBlank() }