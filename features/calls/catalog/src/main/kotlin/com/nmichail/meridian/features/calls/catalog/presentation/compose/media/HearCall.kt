package com.nmichail.meridian.features.calls.catalog.presentation.compose.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.nmichail.meridian.features.calls.catalog.data.media.WebRtcCall
import com.nmichail.meridian.features.calls.signal.presentation.CallSignalIntent

@Composable
fun HearCall(
	live: Boolean,
	incoming: Boolean,
	callId: String,
	chatId: String,
	line: WebRtcCall?,
	onIntent: (CallSignalIntent) -> Unit,
) {
	val hasCall = callId.isNotEmpty() && chatId.isNotEmpty()
	LaunchedEffect(live, incoming, callId, chatId) {
		if (!hasCall) {
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

	LaunchedEffect(live, line, incoming, callId, chatId) {
		if (live && !incoming && hasCall) {
			line?.offer()
		}
	}
}
