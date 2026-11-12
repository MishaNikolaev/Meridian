package com.nmichail.meridian.core.network.error.presentation

import com.nmichail.meridian.core.network.error.NoConnectionException
import java.io.IOException
import javax.inject.Inject

class DefaultErrorEventConverter @Inject constructor() {

	fun convert(exception: Exception): DefaultErrorEvent =
		when (exception) {
			is NoConnectionException,
			is IOException -> DefaultErrorEvent.NoConnection
			else -> DefaultErrorEvent.Default
		}
}