package com.nmichail.meridian.component.ui.control.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.control.MUiRadio
import com.nmichail.meridian.component.ui.control.MUiSwitch
import com.nmichail.meridian.component.ui.control.model.MUiRadioModel
import com.nmichail.meridian.component.ui.control.model.MUiSwitchModel
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiControlPreviewData(
	val dark: Boolean,
	val switch: MUiSwitchModel,
	val radio: MUiRadioModel,
)

internal class MUiControlPreviewParameterProvider : PreviewParameterProvider<MUiControlPreviewData> {
	override val values: Sequence<MUiControlPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(
				MUiControlPreviewData(
					dark = dark,
					switch = MUiSwitchModel(checked = true),
					radio = MUiRadioModel(selected = true),
				),
			)
			yield(
				MUiControlPreviewData(
					dark = dark,
					switch = MUiSwitchModel(checked = false),
					radio = MUiRadioModel(selected = false),
				),
			)
		}
	}
}

@Preview(widthDp = 360, heightDp = 160)
@Composable
private fun MUiControlPreview(
	@PreviewParameter(MUiControlPreviewParameterProvider::class)
	data: MUiControlPreviewData,
) {
	MeridianTheme(darkTheme = data.dark) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(16.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			MUiSwitch(model = data.switch, onClick = {})

			MUiRadio(model = data.radio, onClick = {})
		}
	}
}