package com.nmichail.meridian.features.chat.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nmichail.meridian.component.ui.compose.Screen
import com.nmichail.meridian.component.ui.effect.LiveDataEffect
import com.nmichail.meridian.component.ui.snackbar.MUiSnackbarHost
import com.nmichail.meridian.component.ui.snackbar.showErrorSnackBar
import com.nmichail.meridian.core.network.R as NetworkR
import com.nmichail.meridian.features.chat.presentation.ChatEvent
import com.nmichail.meridian.features.chat.presentation.ChatIntent
import com.nmichail.meridian.features.chat.presentation.ChatState
import com.nmichail.meridian.features.chat.presentation.compose.content.ChatContent
import com.nmichail.meridian.features.chat.presentation.compose.error.ServerUnavailable
import com.nmichail.meridian.features.chat.presentation.compose.preview.ChatScreenParameterProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ChatScreen(
	state: StateFlow<ChatState>,
	onIntent: (ChatIntent) -> Unit,
	event: LiveData<ChatEvent>,
) {
	val uiState by state.collectAsStateWithLifecycle()
	val snackBarHostState = remember { SnackbarHostState() }

	LaunchedEffect(Unit) {
		onIntent(ChatIntent.LoadInitialData)
	}

	EventObserver(
		event = event,
		snackBarHostState = snackBarHostState,
	)

	Box(modifier = Modifier.fillMaxSize()) {
		when (val current = uiState) {
			ChatState.Initial, ChatState.Loading -> Unit

			ChatState.Error                      -> ServerUnavailable(onIntent = onIntent)

			is ChatState.Content                 -> ChatContent(
				state = current,
				onIntent = onIntent,
			)
		}

		MUiSnackbarHost(
			snackBarHostState = snackBarHostState,
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(horizontal = 16.dp, vertical = 24.dp),
		)
	}
}

@Composable
private fun EventObserver(
	event: LiveData<ChatEvent>,
	snackBarHostState: SnackbarHostState,
) {
	val context = LocalContext.current

	LiveDataEffect(event) { value ->
		showErrorSnackBar(
			snackBarHostState = snackBarHostState,
			message = context.getString(value.messageId()),
		)
	}
}

private fun ChatEvent.messageId(): Int = when (this) {
	ChatEvent.NoConnection -> NetworkR.string.error_no_connection
	ChatEvent.Error        -> NetworkR.string.error_unknown
}

@Preview
@Composable
private fun PreviewChatScreen(
	@PreviewParameter(ChatScreenParameterProvider::class)
	state: ChatState,
) {
	Screen {
		ChatScreen(
			state = MutableStateFlow(state),
			onIntent = {},
			event = MutableLiveData(),
		)
	}
}
