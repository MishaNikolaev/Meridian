package com.nmichail.meridian.core.network.error.presentation

sealed interface ErrorReactionOptions<I : Any> {

	data class BackOnly<I : Any>(
		val backIntent: I,
	) : ErrorReactionOptions<I>

	data class RetryOnly<I : Any>(
		val retryIntent: I,
	) : ErrorReactionOptions<I>

	data class BackAndRetry<I : Any>(
		val backIntent: I,
		val retryIntent: I,
	) : ErrorReactionOptions<I>
}