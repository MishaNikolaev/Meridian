package com.nmichail.meridian.component.ui.emoji

enum class MessageReaction(
	val glyph: String,
	val quick: Boolean,
) {
	LIKE(glyph = "👍", quick = true),
	DISLIKE(glyph = "👎", quick = false),
	HEART(glyph = "❤️", quick = true),
	FIRE(glyph = "🔥", quick = true),
	LAUGH(glyph = "😂", quick = true),
	WOW(glyph = "😮", quick = true),
	SAD(glyph = "😢", quick = true),
	OK(glyph = "👌", quick = false),
}