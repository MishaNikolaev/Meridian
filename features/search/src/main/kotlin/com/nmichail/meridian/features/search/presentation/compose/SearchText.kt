package com.nmichail.meridian.features.search.presentation.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

internal fun highlight(text: String, query: String, color: Color): AnnotatedString {
	val needle = query.trim()
	if (needle.isEmpty()) {
		return AnnotatedString(text)
	}
	val builder = AnnotatedString.Builder()
	var index = 0
	while (index < text.length) {
		val found = text.indexOf(needle, startIndex = index, ignoreCase = true)
		if (found < 0) {
			builder.append(text.substring(index))
			return builder.toAnnotatedString()
		}
		builder.append(text.substring(index, found))
		builder.withStyle(SpanStyle(color = color, fontWeight = FontWeight.SemiBold)) {
			append(text.substring(found, found + needle.length))
		}
		index = found + needle.length
	}
	return builder.toAnnotatedString()
}

internal fun searchInitials(name: String): String = name
	.split(" ")
	.filter { part -> part.isNotEmpty() }
	.take(2)
	.joinToString(separator = "") { part -> part.take(1) }
	.uppercase()
