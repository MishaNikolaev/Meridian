package com.nmichail.meridian.features.conversation.presentation.compose.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.nmichail.meridian.component.ui.emoji.emojiCluster
import com.nmichail.meridian.component.ui.emoji.withSystemEmoji
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
internal fun MessageGlyphs(
	text: String,
	color: Color,
) {
	val style = if (emojiCluster(text) == 0) {
		MeridianType.chatMessage
	} else {
		MeridianType.chatEmoji
	}

	Text(
		text = withMentions(text, palette.accentPrimary),
		style = style,
		color = color,
	)
}

private val mentionPattern = Regex("@[\\p{L}\\p{N}_]+")

private fun withMentions(text: String, mention: Color): AnnotatedString {
	val builder = AnnotatedString.Builder()
	var index = 0

	mentionPattern.findAll(text).forEach { match ->
		if (match.range.first > index) {
			builder.append(withSystemEmoji(text.substring(index, match.range.first)))
		}

		builder.pushStyle(SpanStyle(color = mention))
		builder.append(match.value)
		builder.pop()
		index = match.range.last + 1
	}

	if (index < text.length) {
		builder.append(withSystemEmoji(text.substring(index)))
	}

	return builder.toAnnotatedString()
}