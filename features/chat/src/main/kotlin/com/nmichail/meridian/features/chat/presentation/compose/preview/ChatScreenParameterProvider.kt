package com.nmichail.meridian.features.chat.presentation.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nmichail.meridian.features.chat.presentation.ChatState

internal class ChatScreenParameterProvider : PreviewParameterProvider<ChatState> {

	override val values: Sequence<ChatState> = sequenceOf(
		ChatState.Initial,
		ChatState.Loading,
		previewContent,
		ChatState.Error,
	)
}