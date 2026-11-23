package com.nmichail.meridian.features.conversation.presentation.compose.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nmichail.meridian.component.ui.emoji.emojiCluster
import com.nmichail.meridian.component.ui.text.MUiLinkedText
import com.nmichail.meridian.component.ui.theme.MeridianType
import com.nmichail.meridian.component.ui.theme.palette

@Composable
internal fun SpokenText(
	text: String,
	color: Color,
	onLink: (String) -> Unit = {},
) {
	val style = if (emojiCluster(text) == 0) {
		MeridianType.chatMessage
	} else {
		MeridianType.chatEmoji
	}

	MUiLinkedText(
		text = text,
		style = style,
		textColor = color,
		mentionColor = palette.accentPrimary,
		onLinkClick = onLink,
	)
}