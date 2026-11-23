package com.nmichail.meridian.features.calls.presentation.compose.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalIntent
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun HearCall(
	live: Boolean,
	incoming: Boolean,
	callId: String,
	chatId: String,
	line: CallLine?,
	inbound: SharedFlow<String>,
	onIntent: (CallSignalIntent) -> Unit,
) {
	LaunchedEffect(live, incoming, callId, chatId) {
		if (callId.isEmpty() || chatId.isEmpty()) {
			onIntent(CallSignalIntent.Stop)
		} else if (live || !incoming) {
			onIntent(CallSignalIntent.Start(callId = callId, chatId = chatId, incoming = incoming))
		} else {
			onIntent(CallSignalIntent.Stop)
		}
	}

	DisposableEffect(line) {
		val current = line
		if (current != null) {
			current.onSignal = { body -> onIntent(CallSignalIntent.Outgoing(body)) }
		}
		onDispose {
			current?.onSignal = null
		}
	}

	DisposableEffect(Unit) {
		onDispose { onIntent(CallSignalIntent.Stop) }
	}

	LaunchedEffect(line, inbound) {
		val current = line ?: return@LaunchedEffect
		inbound.collect(current::take)
	}

	LaunchedEffect(live, line, incoming) {
		if (live && line != null && !incoming) {
			line.offer()
		}
	}
}
