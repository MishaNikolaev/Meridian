package com.nmichail.meridian.features.profile.presentation.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.dialog.MUiDialog
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType
import com.nmichail.meridian.component.ui.theme.ThemeChoice
import com.nmichail.meridian.component.ui.theme.selectTheme
import com.nmichail.meridian.component.ui.theme.themeChoice
import com.nmichail.meridian.features.profile.R

private val choices = listOf(ThemeChoice.System, ThemeChoice.Light, ThemeChoice.Dark)

@Composable
internal fun AppearanceBlock(version: String) {
	var open by remember { mutableStateOf(false) }

	val context = LocalContext.current

	val choice = themeChoice()

	MoreBlock(
		version = version,
		theme = stringResource(choice.label()),
		onAppearance = { open = true },
	)

	if (open) {
		ThemeDialog(
			choice = choice,
			onSelect = { next -> selectTheme(context, next) },
			onDismiss = { open = false },
		)
	}
}

@Composable
private fun ThemeDialog(
	choice: ThemeChoice,
	onSelect: (ThemeChoice) -> Unit,
	onDismiss: () -> Unit,
) {
	MUiDialog(
		model = MUiDialogModel(
			type = MUiDialogType.List,
			title = stringResource(R.string.profile_appearance),
			message = "",
			cancel = stringResource(R.string.profile_theme_cancel),
			confirm = stringResource(R.string.profile_theme_done),
			options = choices.map { stringResource(it.label()) },
			selectedOption = choices.indexOf(choice),
			fieldLabel = "",
			fieldValue = "",
		),
		onDismiss = onDismiss,
		onCancel = onDismiss,
		onConfirm = onDismiss,
		onOptionSelected = { index ->
			onSelect(choices[index])
		},
	)
}

private fun ThemeChoice.label(): Int = when (this) {
	ThemeChoice.System -> R.string.profile_theme_system
	ThemeChoice.Light -> R.string.profile_theme_light
	ThemeChoice.Dark -> R.string.profile_theme_dark
}