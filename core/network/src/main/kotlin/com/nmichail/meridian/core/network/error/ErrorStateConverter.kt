package com.nmichail.meridian.core.network.error

import com.nmichail.meridian.core.network.R
import java.io.IOException
import javax.inject.Inject

class ErrorStateConverter @Inject constructor() {

	fun convert(exception: Exception): ErrorState =
		when (exception) {
			is NoConnectionException -> exception.toMessage(StatusCodes.NO_CONNECTION)
			is IOException           -> exception.toMessage(StatusCodes.NO_CONNECTION)
			is GatewayException      -> exception.toMessage(exception.status())
			else                     -> ErrorState.Unknown
		}

	private fun GatewayException.status(): StatusCodes {
		val http = httpStatus ?: return StatusCodes.UNKNOWN
		return StatusCodes.entries.firstOrNull { status -> status.statusCode == http } ?: StatusCodes.UNKNOWN
	}

	private fun Exception.toMessage(statusCode: StatusCodes): ErrorState.ErrorMessage =
		ErrorState.ErrorMessage(
			statusCode = statusCode,
			messageId = messageId(),
			message = message.orEmpty(),
		)

	private fun Exception.messageId(): Int =
		when (this) {
			is GatewayException -> displayMessage()
			is NoConnectionException -> R.string.error_no_connection
			is IOException -> R.string.error_no_connection
			else -> R.string.error_unknown
		}
}