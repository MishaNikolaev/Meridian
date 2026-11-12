package com.nmichail.meridian.component.ui.field.preview

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.field.MUiTextField
import com.nmichail.meridian.component.ui.field.model.MUiFieldIcon
import com.nmichail.meridian.component.ui.field.model.MUiTextFieldModel
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiTextFieldPreviewData(
	val dark: Boolean,
	val model: MUiTextFieldModel,
)

internal class MUiTextFieldPreviewParameterProvider : PreviewParameterProvider<MUiTextFieldPreviewData> {
	override val values: Sequence<MUiTextFieldPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(
				MUiTextFieldPreviewData(
					dark = dark,
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.Mail,
						value = "",
						placeholder = "name@company.ru",
					),
				),
			)
			yield(
				MUiTextFieldPreviewData(
					dark = dark,
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.Building,
						label = "Адрес сервера",
						value = "meridian.ftc.ru",
						highlighted = true,
					),
				),
			)
			yield(
				MUiTextFieldPreviewData(
					dark = dark,
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.Mail,
						value = "name@company.ru",
						error = "Проверьте данные и повторите попытку",
					),
				),
			)
			yield(
				MUiTextFieldPreviewData(
					dark = dark,
					model = MUiTextFieldModel(
						icon = MUiFieldIcon.Search,
						value = "",
						placeholder = "Поиск по контактам",
					),
				),
			)
		}
	}
}

@Preview(widthDp = 360, heightDp = 200)
@Composable
private fun MUiTextFieldPreview(
	@PreviewParameter(MUiTextFieldPreviewParameterProvider::class)
	data: MUiTextFieldPreviewData,
) {
	MeridianTheme(themeState = data.dark) {
		MUiTextField(
			model = data.model,
			onValueChange = {},
			modifier = Modifier.width(328.dp),
		)
	}
}