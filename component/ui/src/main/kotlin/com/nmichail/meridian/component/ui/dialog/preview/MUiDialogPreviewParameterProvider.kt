package com.nmichail.meridian.component.ui.dialog.preview

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nmichail.meridian.component.ui.dialog.MUiDialogCard
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType
import com.nmichail.meridian.component.ui.theme.MeridianTheme

internal data class MUiDialogPreviewData(
	val dark: Boolean,
	val model: MUiDialogModel,
)

internal class MUiDialogPreviewParameterProvider : PreviewParameterProvider<MUiDialogPreviewData> {
	override val values: Sequence<MUiDialogPreviewData> = sequence {
		listOf(false, true).forEach { dark ->
			yield(
				MUiDialogPreviewData(
					dark = dark,
					model = MUiDialogModel(
						type = MUiDialogType.Basic,
						title = "Заголовок диалога",
						message = "Краткое пояснение, помогающее принять решение. Не более трёх строк.",
						cancel = "Отмена",
						confirm = "Готово",
						options = emptyList(),
						selectedOption = 0,
						fieldLabel = "",
						fieldValue = "",
					),
				),
			)
			yield(
				MUiDialogPreviewData(
					dark = dark,
					model = MUiDialogModel(
						type = MUiDialogType.Icon,
						title = "Заголовок диалога",
						message = "Краткое пояснение, помогающее принять решение. Не более трёх строк.",
						cancel = "Отмена",
						confirm = "Готово",
						options = emptyList(),
						selectedOption = 0,
						fieldLabel = "",
						fieldValue = "",
					),
				),
			)
			yield(
				MUiDialogPreviewData(
					dark = dark,
					model = MUiDialogModel(
						type = MUiDialogType.Destructive,
						title = "Удалить сообщение?",
						message = "Сообщение исчезнет у всех участников чата. Это действие нельзя отменить.",
						cancel = "Отмена",
						confirm = "Удалить",
						options = emptyList(),
						selectedOption = 0,
						fieldLabel = "",
						fieldValue = "",
					),
				),
			)
			yield(
				MUiDialogPreviewData(
					dark = dark,
					model = MUiDialogModel(
						type = MUiDialogType.List,
						title = "Заголовок диалога",
						message = "",
						cancel = "Отмена",
						confirm = "Готово",
						options = listOf(
							"Отключить на 1 час",
							"Отключить на 8 часов",
							"Отключить навсегда",
						),
						selectedOption = 1,
						fieldLabel = "",
						fieldValue = "",
					),
				),
			)
			yield(
				MUiDialogPreviewData(
					dark = dark,
					model = MUiDialogModel(
						type = MUiDialogType.Input,
						title = "Заголовок диалога",
						message = "Краткое пояснение, помогающее принять решение. Не более трёх строк.",
						cancel = "Отмена",
						confirm = "Готово",
						options = emptyList(),
						selectedOption = 0,
						fieldLabel = "Название чата",
						fieldValue = "Власов Кирилл",
					),
				),
			)
		}
	}
}

@Preview(widthDp = 360, heightDp = 480)
@Composable
private fun MUiDialogPreview(
	@PreviewParameter(MUiDialogPreviewParameterProvider::class)
	data: MUiDialogPreviewData,
) {
	MeridianTheme(themeState = data.dark) {
		MUiDialogCard(
			model = data.model,
			onCancel = {},
			onConfirm = {},
			onOptionSelected = {},
			modifier = Modifier.width(312.dp),
		)
	}
}