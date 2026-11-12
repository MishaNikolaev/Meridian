package com.nmichail.meridian.component.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

fun CoroutineScope.showSuccessSnackBar(
	snackBarHostState: SnackbarHostState,
	message: String,
) {
	snackBarHostState.currentSnackbarData?.dismiss()

	launch {
		snackBarHostState.showSnackbar(
			MUiSnackbarVisuals(
				message = message,
				tone = MUiSnackbarTone.Success,
				duration = SnackbarDuration.Short,
			),
		)
	}
}

@Composable
fun SuccessSnackBarEffect(
	show: Boolean,
	message: String,
	snackBarHostState: SnackbarHostState,
	onShown: () -> Unit,
) {
	val scope = rememberCoroutineScope()

	LaunchedEffect(show) {
		if (show) {
			scope.showSuccessSnackBar(snackBarHostState = snackBarHostState, message = message)

			onShown()
		}
	}
}

fun CoroutineScope.showErrorSnackBar(
	snackBarHostState: SnackbarHostState,
	message: String,
	title: String? = null,
) {
	val text = if (title == null) {
		message
	} else {
		"$title\n$message"
	}

	snackBarHostState.currentSnackbarData?.dismiss()

	launch {
		snackBarHostState.showSnackbar(
			MUiSnackbarVisuals(
				message = text,
				tone = MUiSnackbarTone.Error,
				duration = SnackbarDuration.Short,
			),
		)
	}
}