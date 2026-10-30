package com.nmichail.meridian.core.network.error.presentation

sealed interface DefaultErrorEvent {

	data object Default : DefaultErrorEvent

	data object NoConnection : DefaultErrorEvent
}