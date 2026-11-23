package com.nmichail.meridian.component.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.dialog.MUiDialog
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType

private val choices = listOf(ThemeChoice.System, ThemeChoice.Light, ThemeChoice.Dark)

@Composable
fun ThemeDialog(
	open: Boolean,
	onDismiss: () -> Unit,
) {
	if (!open) {
		return
	}

	val context = LocalContext.current
	val choice = themeChoice()

	MUiDialog(
		model = MUiDialogModel(
			type = MUiDialogType.List,
			title = stringResource(R.string.theme_title),
			message = "",
			cancel = stringResource(R.string.theme_cancel),
			confirm = stringResource(R.string.theme_done),
			options = choices.map { stringResource(it.label()) },
			selectedOption = choices.indexOf(choice),
			fieldLabel = "",
			fieldValue = "",
		),
		onDismiss = onDismiss,
		onCancel = onDismiss,
		onConfirm = onDismiss,
		onOptionSelected = { index ->
			selectTheme(context, choices[index])
		},
	)
}

fun ThemeChoice.label(): Int = when (this) {
	ThemeChoice.System -> R.string.theme_system
	ThemeChoice.Light -> R.string.theme_light
	ThemeChoice.Dark -> R.string.theme_dark
}
