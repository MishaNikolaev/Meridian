package com.nmichail.meridian.component.ui.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun DismissKeyboardOnDispose() {
	val keyboard = rememberUpdatedState(LocalSoftwareKeyboardController.current)
	DisposableEffect(Unit) {
		onDispose {
			keyboard.value?.hide()
		}
	}
}
