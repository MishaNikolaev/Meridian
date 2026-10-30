package com.nmichail.meridian.core.network.error

import com.nmichail.meridian.core.network.R

open class GatewayException(
	val code: GatewayCode,
	message: String = code.name,
	val httpStatus: Int? = null,
) : Exception(message)

fun Throwable.userMessage(): Int {
	if (this is java.util.concurrent.CancellationException) {
		throw this
	}
	val state = (this as? Exception)?.let(ErrorStateConverter()::convert)
	return state.messageIdOrNull() ?: R.string.error_unknown
}

fun GatewayException.displayMessage(): Int = code.message