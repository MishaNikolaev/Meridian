package com.nmichail.meridian.component.ui.snackbar

data class MUiSnackbarModel(
	val tone: MUiSnackbarTone,
	val message: String,
	val duration: MUiSnackbarDuration,
	val action: String? = null,
	val dark: Boolean = false,
)