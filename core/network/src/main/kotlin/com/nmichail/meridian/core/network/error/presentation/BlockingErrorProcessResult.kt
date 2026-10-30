package com.nmichail.meridian.core.network.error.presentation

sealed interface BlockingErrorProcessResult<out I : Any> {

	data object Handled : BlockingErrorProcessResult<Nothing>

	data class NotHandled<I : Any>(
		val recommended: ErrorReactionOptions<I>,
	) : BlockingErrorProcessResult<I>
}