package com.nmichail.meridian.core.network.error.presentation

import android.util.Log
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.NoConnectionException
import java.io.IOException
import javax.inject.Inject

class ErrorLogger @Inject constructor() {

	private companion object {

		const val TAG = "MeridianError"
	}

	fun needLog(exception: Exception): Boolean =
		when (exception) {
			is NoConnectionException -> false

			is IOException -> false

			is GatewayException -> exception.code == GatewayCode.UNKNOWN

			else -> true
		}

	fun log(exception: Exception) {
		try {
			Log.e(TAG, exception.javaClass.simpleName, exception)
		} catch (ignored: RuntimeException) {
			return
		}
	}
}