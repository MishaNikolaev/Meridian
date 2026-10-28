package com.nmichail.meridian.features.chat.presentation.compose.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nmichail.meridian.features.chat.presentation.ChatState

internal class ChatContentParameterProvider : PreviewParameterProvider<ChatState.Content> {

	override val values: Sequence<ChatState.Content> = sequenceOf(
		previewContent,
		previewEmptyContent,
		previewDiscussionsContent,
		previewExternalContent,
		previewCreateContent,
	)
}
