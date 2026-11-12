package com.nmichail.meridian.core.network.error.presentation

import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.NoConnectionException
import java.io.IOException
import javax.inject.Inject

class DefaultErrorReactionConverter @Inject constructor() {

	fun <I : Any> convert(
		exception: Exception,
		retryIntent: I,
		backIntent: I?,
	): ErrorReactionOptions<I> =
		when {
			exception.canRetry() && backIntent != null -> ErrorReactionOptions.BackAndRetry(
				backIntent = backIntent,
				retryIntent = retryIntent,
			)

			exception.canRetry() -> ErrorReactionOptions.RetryOnly(retryIntent)

			backIntent != null -> ErrorReactionOptions.BackOnly(backIntent)
			else -> ErrorReactionOptions.RetryOnly(retryIntent)
		}
}

private fun Exception.canRetry(): Boolean =
	when (this) {
		is NoConnectionException -> true
		is IOException -> true
		is GatewayException -> code == GatewayCode.UNKNOWN
		else -> false
	}