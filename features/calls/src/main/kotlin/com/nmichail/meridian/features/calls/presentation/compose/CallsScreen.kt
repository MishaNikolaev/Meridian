package com.nmichail.meridian.features.calls.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText

@Composable
fun CallsScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		MUiTitleScreenText(text = "Звонки")
	}
}
