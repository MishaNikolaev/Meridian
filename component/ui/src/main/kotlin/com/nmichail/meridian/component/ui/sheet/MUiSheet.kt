package com.nmichail.meridian.component.ui.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MUiSheet(
	open: Boolean,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
) {
	if (!open) {
		return
	}

	Box(modifier = modifier.fillMaxSize()) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.32f))
				.clickable(onClick = onDismiss),
		)

		Box(modifier = Modifier.align(Alignment.BottomCenter)) {
			content()
		}
	}
}