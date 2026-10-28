package com.nmichail.meridian.component.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class MUiSnackbarVisuals(
	override val message: String,
	val tone: MUiSnackbarTone,
	override val actionLabel: String? = null,
	override val withDismissAction: Boolean = false,
	override val duration: SnackbarDuration = if (actionLabel == null) {
		SnackbarDuration.Short
	} else {
		SnackbarDuration.Long
	},
) : SnackbarVisuals

@Composable
fun MUiSnackbarHost(
	snackBarHostState: SnackbarHostState,
	modifier: Modifier = Modifier,
) {
	SnackbarHost(
		hostState = snackBarHostState,
		modifier = modifier,
		snackbar = { data ->
			val visuals = data.visuals as? MUiSnackbarVisuals
			MUiSnackbar(
				model = MUiSnackbarModel(
					tone = visuals?.tone ?: MUiSnackbarTone.Error,
					message = data.visuals.message,
					action = data.visuals.actionLabel,
					duration = MUiSnackbarDuration.Indefinite,
				),
				onAction = { data.performAction() },
				onDismiss = { data.dismiss() },
			)
		},
	)
}

fun CoroutineScope.showErrorSnackBar(
	snackBarHostState: SnackbarHostState,
	message: String,
) {
	snackBarHostState.currentSnackbarData?.dismiss()
	launch {
		snackBarHostState.showSnackbar(
			MUiSnackbarVisuals(
				message = message,
				tone = MUiSnackbarTone.Error,
			),
		)
	}
}
