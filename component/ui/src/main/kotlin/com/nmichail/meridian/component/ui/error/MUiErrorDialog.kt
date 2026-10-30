package com.nmichail.meridian.component.ui.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.R
import com.nmichail.meridian.component.ui.dialog.MUiDialog
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogModel
import com.nmichail.meridian.component.ui.dialog.model.MUiDialogType

@Composable
fun MUiErrorDialog(
	message: String,
	onDismiss: () -> Unit,
	retry: String? = null,
	back: String? = null,
	onRetry: (() -> Unit)? = null,
	onBack: (() -> Unit)? = null,
) {
	val confirm = if (retry != null && onRetry != null) {
		onRetry
	} else {
		onBack ?: onDismiss
	}

	MUiDialog(
		model = MUiDialogModel(
			type = MUiDialogType.Basic,
			title = stringResource(R.string.error_title),
			message = message,
			cancel = back.orEmpty(),
			confirm = retry ?: back.orEmpty(),
			options = emptyList(),
			selectedOption = 0,
			fieldLabel = "",
			fieldValue = "",
		),
		onDismiss = onDismiss,
		onCancel = onBack ?: onDismiss,
		onConfirm = confirm,
		onOptionSelected = {},
	)
}