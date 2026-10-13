package com.nmichail.meridian.component.ui.snackbar

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal class MUiSnackbarPreviewParameterProvider : PreviewParameterProvider<MUiSnackbarModel> {
	override val values: Sequence<MUiSnackbarModel> = sequence {
		listOf(false, true).forEach { dark ->
			yield(
				MUiSnackbarModel(
					tone = MUiSnackbarTone.Error,
					message = "Неверный логин или пароль",
					action = "Повторить",
					dark = dark,
				),
			)
			yield(
				MUiSnackbarModel(
					tone = MUiSnackbarTone.Info,
					message = "Ссылка скопирована",
					dark = dark,
				),
			)
			yield(
				MUiSnackbarModel(
					tone = MUiSnackbarTone.Success,
					message = "Статус сохранён",
					action = "Отменить",
					dark = dark,
				),
			)
		}
	}
}

@Preview(widthDp = 360, heightDp = 200)
@Composable
private fun MUiSnackbarPreview(
	@PreviewParameter(MUiSnackbarPreviewParameterProvider::class)
	model: MUiSnackbarModel,
) {
	MeridianTheme(darkTheme = model.dark) {
		MUiSnackbar(
			model = model,
			modifier = Modifier.width(328.dp),
		)
	}
}
