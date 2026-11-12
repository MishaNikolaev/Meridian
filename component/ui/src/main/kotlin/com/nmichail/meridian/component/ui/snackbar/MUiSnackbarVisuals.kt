package com.nmichail.meridian.component.ui.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class MUiSnackbarVisuals(
	override val message: String,
	val tone: MUiSnackbarTone,
	override val duration: SnackbarDuration,
	override val actionLabel: String? = null,
	override val withDismissAction: Boolean = false,
) : SnackbarVisuals