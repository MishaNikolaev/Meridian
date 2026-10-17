package com.nmichail.meridian.component.ui.button.preview

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.button.MUiButton
import com.nmichail.meridian.component.ui.button.model.MUiButtonModel
import com.nmichail.meridian.component.ui.button.model.MUiButtonStyle
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiButtonPreviewData(
	val dark: Boolean,
	val model: MUiButtonModel,
)

internal class MUiButtonPreviewParameterProvider : PreviewParameterProvider<MUiButtonPreviewData> {
	override val values: Sequence<MUiButtonPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Primary, "Продолжить")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Secondary, "Скопировать ссылку")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Outline, "Вход в рабочее пространство")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Danger, "Закрыть сессию")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Text, "Сменить сервер")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Icon, "")))
			yield(MUiButtonPreviewData(dark, MUiButtonModel(MUiButtonStyle.Fab, "")))
		}
	}
}

@Preview(widthDp = 360, heightDp = 200)
@Composable
private fun MUiButtonPreview(
	@PreviewParameter(MUiButtonPreviewParameterProvider::class)
	data: MUiButtonPreviewData,
) {
	MeridianTheme(darkTheme = data.dark) {
		val widthModifier = when (data.model.style) {
			MUiButtonStyle.Icon, MUiButtonStyle.Fab, MUiButtonStyle.Text -> Modifier
			else -> Modifier.width(328.dp)
		}
		MUiButton(
			model = data.model,
			onClick = {},
			modifier = widthModifier,
		)
	}
}
