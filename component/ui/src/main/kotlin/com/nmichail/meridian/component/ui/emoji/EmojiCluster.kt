package com.nmichail.meridian.component.ui.emoji

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily

fun withSystemEmoji(text: String): AnnotatedString {
	val builder = AnnotatedString.Builder()
	var index = 0

	while (index < text.length) {
		val start = index
		val emoji = text.codePointAt(index).isEmojiPiece()
		while (index < text.length && text.codePointAt(index).isEmojiPiece() == emoji) {
			index += Character.charCount(text.codePointAt(index))
		}
		val slice = text.substring(start, index)

		if (emoji) {
			builder.pushStyle(SpanStyle(fontFamily = FontFamily.Default))
			builder.append(slice)
			builder.pop()
		} else {
			builder.append(slice)
		}
	}
	return builder.toAnnotatedString()
}

fun emojiCluster(text: String): Int {
	val value = text.trim()
	if (value.isEmpty()) {
		return 0
	}

	var count = 0
	var index = 0

	while (index < value.length) {
		val code = value.codePointAt(index)

		index += Character.charCount(code)
		if (Character.isWhitespace(code)) {
			continue
		}

		if (!code.isEmojiPiece()) {
			return 0
		}

		if (Character.getType(code) == Character.OTHER_SYMBOL.toInt()) {
			count += 1
		}
	}
	return count
}

private fun Int.isEmojiPiece(): Boolean {
	val type = Character.getType(this)
	return type == Character.OTHER_SYMBOL.toInt() ||
		type == Character.MODIFIER_SYMBOL.toInt() ||
		type == Character.FORMAT.toInt() ||
		type == Character.NON_SPACING_MARK.toInt() ||
		type == Character.ENCLOSING_MARK.toInt()
}