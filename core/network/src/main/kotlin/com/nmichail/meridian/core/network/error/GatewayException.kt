package com.nmichail.meridian.core.network.error

import com.nmichail.meridian.core.network.R
import java.io.IOException

class GatewayException(
	val code: GatewayCode,
	message: String = code.name,
	val httpStatus: Int? = null,
) : Exception(message)

fun Throwable.userMessage(): Int =
	when (this) {
		is java.util.concurrent.CancellationException -> throw this
		is GatewayException -> displayMessage()
		is IOException -> R.string.error_no_connection
		else -> R.string.error_unknown
	}

fun GatewayException.displayMessage(): Int = code.message
