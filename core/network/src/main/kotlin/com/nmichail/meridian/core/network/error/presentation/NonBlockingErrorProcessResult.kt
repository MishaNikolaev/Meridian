package com.nmichail.meridian.core.network.error.presentation

sealed interface NonBlockingErrorProcessResult {

	data object Handled : NonBlockingErrorProcessResult

	data class NotHandled(
		val recommended: DefaultErrorEvent,
	) : NonBlockingErrorProcessResult
}