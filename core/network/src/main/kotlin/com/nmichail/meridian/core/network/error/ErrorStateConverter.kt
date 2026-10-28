package com.nmichail.meridian.core.network.error

import java.io.IOException
import javax.inject.Inject

class ErrorStateConverter @Inject constructor() {

	fun convert(exception: Exception): ErrorState =
		when (exception) {
			is IOException      -> exception.toMessage(StatusCodes.NO_CONNECTION)
			is GatewayException -> exception.toMessage(exception.status())
			else                -> ErrorState.Unknown
		}

	private fun GatewayException.status(): StatusCodes {
		val http = httpStatus ?: return StatusCodes.UNKNOWN
		return StatusCodes.entries.firstOrNull { status -> status.statusCode == http } ?: StatusCodes.UNKNOWN
	}

	private fun Exception.toMessage(statusCode: StatusCodes): ErrorState.ErrorMessage =
		ErrorState.ErrorMessage(
			statusCode = statusCode,
			messageId = statusCode.statusCode,
			message = message.orEmpty(),
		)
}