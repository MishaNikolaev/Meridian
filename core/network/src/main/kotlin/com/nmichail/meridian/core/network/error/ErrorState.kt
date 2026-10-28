package com.nmichail.meridian.core.network.error

sealed class ErrorState {

	data object Unknown : ErrorState()

	data class ErrorMessage(
		val statusCode: StatusCodes,
		val messageId: Int,
		val message: String,
	) : ErrorState()
}

fun ErrorState?.messageIdOrNull(): Int? =
	when (this) {
		is ErrorState.ErrorMessage -> messageId

		ErrorState.Unknown,
		null                       -> null
	}