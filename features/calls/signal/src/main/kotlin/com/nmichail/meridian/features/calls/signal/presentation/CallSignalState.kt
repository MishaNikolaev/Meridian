package com.nmichail.meridian.features.calls.signal.presentation

sealed interface CallSignalState {

	data object Initial : CallSignalState

	data object Loading : CallSignalState

	data class Content(
		val callId: String,
		val chatId: String,
		val incoming: Boolean,
	) : CallSignalState

	data class Error(
		val message: Int,
	) : CallSignalState
}