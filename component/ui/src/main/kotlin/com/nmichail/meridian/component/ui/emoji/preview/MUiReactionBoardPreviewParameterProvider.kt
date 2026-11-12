package com.nmichail.meridian.component.ui.emoji.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nmichail.meridian.component.ui.emoji.MUiReactionBoard
import com.nmichail.meridian.component.ui.emoji.MessageReaction
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiReactionBoardPreviewData(
	val dark: Boolean,
	val selected: String,
)

internal class MUiReactionBoardPreviewParameterProvider : PreviewParameterProvider<MUiReactionBoardPreviewData> {
	override val values: Sequence<MUiReactionBoardPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(MUiReactionBoardPreviewData(dark = dark, selected = "😀"))
		}
	}
}

@Preview(widthDp = 280, heightDp = 240)
@Composable
private fun MUiReactionBoardPreview(
	@PreviewParameter(MUiReactionBoardPreviewParameterProvider::class)
	data: MUiReactionBoardPreviewData,
) {
	MeridianTheme(themeState = data.dark) {
		MUiReactionBoard(
			reactions = MessageReaction.entries,
			selected = setOf(data.selected),
			onPick = {},
		)
	}
}