package com.nmichail.meridian.features.calls.signal.presentation

sealed interface CallSignalState {

	data object Idle : CallSignalState

	data class Live(
		val callId: String,
		val chatId: String,
		val incoming: Boolean,
	) : CallSignalState
}
