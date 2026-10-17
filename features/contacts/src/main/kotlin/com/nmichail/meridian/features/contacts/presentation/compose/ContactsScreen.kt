package com.nmichail.meridian.features.contacts.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText

@Composable
fun ContactsScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		MUiTitleScreenText(text = "Контакты")
	}
}
