package com.nmichail.meridian.component.ui.chip.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nmichail.meridian.component.ui.chip.MUiChip
import com.nmichail.meridian.component.ui.chip.MUiChipRow
import com.nmichail.meridian.component.ui.chip.model.MUiChipModel
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiChipPreviewData(
	val dark: Boolean,
	val model: MUiChipModel,
)

internal class MUiChipPreviewParameterProvider : PreviewParameterProvider<MUiChipPreviewData> {
	override val values: Sequence<MUiChipPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(MUiChipPreviewData(dark, MUiChipModel(label = "1 час", selected = true)))
			yield(MUiChipPreviewData(dark, MUiChipModel(label = "4 часа", selected = false)))
		}
	}
}

@Preview(widthDp = 360, heightDp = 160)
@Composable
private fun MUiChipPreview(
	@PreviewParameter(MUiChipPreviewParameterProvider::class)
	data: MUiChipPreviewData,
) {
	MeridianTheme(darkTheme = data.dark) {
		MUiChip(model = data.model, onClick = {})
	}
}

@Preview(widthDp = 360, heightDp = 80)
@Composable
private fun MUiChipRowPreview() {
	MeridianTheme {
		MUiChipRow(
			chips = listOf(
				MUiChipModel(label = "1 час", selected = true),
				MUiChipModel(label = "4 часа", selected = false),
				MUiChipModel(label = "8 часов", selected = false),
			),
			onClick = {},
		)
	}
}