package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.text.MUiBodyRegularText
import com.nmichail.meridian.component.ui.theme.MeridianThemeTokens
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChatScreen(
	state: StateFlow<ChatState>,
	onIntent: (ChatIntent) -> Unit,
) {
	val uiState by state.collectAsStateWithLifecycle()

	LaunchedEffect(Unit) {
		onIntent(ChatIntent.LoadInitialData)
	}

	when (val current = uiState) {
		ChatState.Initial, ChatState.Loading -> Unit

		is ChatState.Error -> Error(current.message)

		is ChatState.Content -> ChatContent(
			state = current,
			onIntent = onIntent,
		)
	}
}

@Composable
private fun Error(message: Int) {
	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		MUiBodyRegularText(text = stringResource(message), color = MeridianThemeTokens.palette.statusError)
	}
}