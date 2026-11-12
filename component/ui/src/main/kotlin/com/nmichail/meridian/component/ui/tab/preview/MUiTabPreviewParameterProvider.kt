package com.nmichail.meridian.component.ui.tab.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.tab.MUiTab
import com.nmichail.meridian.component.ui.tab.MUiTitleSwitch
import com.nmichail.meridian.component.ui.tab.model.MUiTabModel
import com.nmichail.meridian.component.ui.tab.model.MUiTitleSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiTabPreviewData(
	val dark: Boolean,
	val tab: MUiTabModel,
	val title: MUiTitleSwitchModel,
)

internal class MUiTabPreviewParameterProvider : PreviewParameterProvider<MUiTabPreviewData> {
	override val values: Sequence<MUiTabPreviewData> = sequence {
		val title = MUiTitleSwitchModel(
			leadingTitle = "Корпоративные",
			leadingCount = 12,
			trailingTitle = "Внешние",
			trailingCount = 3,
			leadingSelected = true,
		)
		listOf(false, true).forEach { dark ->
			yield(
				MUiTabPreviewData(
					dark = dark,
					tab = MUiTabModel(label = "Каталог", selected = true),
					title = title,
				),
			)

			yield(
				MUiTabPreviewData(
					dark = dark,
					tab = MUiTabModel(label = "Боты", selected = false),
					title = title,
				),
			)
		}
	}
}

@Preview(widthDp = 360, heightDp = 240)
@Composable
private fun MUiTabPreview(
	@PreviewParameter(MUiTabPreviewParameterProvider::class)
	data: MUiTabPreviewData,
) {
	MeridianTheme(themeState = data.dark) {
		Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
			Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
				MUiTab(model = data.tab, onClick = {})
			}
			MUiTitleSwitch(
				model = data.title,
				onLeadingClick = {},
				onTrailingClick = {},
			)
		}
	}
}