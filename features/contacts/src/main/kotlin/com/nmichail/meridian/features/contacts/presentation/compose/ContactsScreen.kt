package com.nmichail.meridian.features.contacts.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nmichail.meridian.component.ui.text.MUiTitleScreenText
import com.nmichail.meridian.features.contacts.R

@Composable
fun ContactsScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		MUiTitleScreenText(text = stringResource(R.string.contacts_title))
	}
}
