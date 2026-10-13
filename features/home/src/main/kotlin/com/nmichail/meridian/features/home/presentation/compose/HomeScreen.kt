package com.nmichail.meridian.features.home.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText

@Composable
fun HomeScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		MUiTitleScreenText(text = "Главный экран")
	}
}
