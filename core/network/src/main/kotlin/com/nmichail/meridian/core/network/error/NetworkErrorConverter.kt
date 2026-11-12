package com.nmichail.meridian.core.network.error

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class NetworkErrorConverter @Inject constructor() {

	fun convertTransport(cause: Throwable): Exception? =
		when (cause) {
			is CancellationException -> null
			is UnknownHostException,
			is ConnectException,
			is SocketTimeoutException -> NoConnectionException()
			else -> null
		}

	fun fromResponse(status: Int, body: ApiErrorBody?): GatewayException =
		if (body == null) {
			BadGatewayResponseException(status)
		} else {
			fromBody(GatewayCode.from(body.code), status)
		}

	fun fromBody(code: GatewayCode, httpStatus: Int): GatewayException =
		when (code) {
			GatewayCode.PASSWORD_MISMATCH -> PasswordMismatchException(httpStatus)
			GatewayCode.NOT_ON_SERVER -> NotOnServerException(httpStatus)
			GatewayCode.SERVER_NOT_FOUND,
			GatewayCode.INVALID_HOST -> ServerNotFoundException(code, httpStatus)
			GatewayCode.DEVICE_TAKEN -> DeviceTakenException(httpStatus)
			GatewayCode.FORBIDDEN -> ForbiddenException(httpStatus)
			GatewayCode.TOO_MANY_REQUESTS -> TooManyRequestsException(httpStatus)
			GatewayCode.PASSWORD_NOT_SET -> PasswordNotSetException(httpStatus)
			GatewayCode.USER_NOT_FOUND -> EntityNotFoundException(httpStatus)
			else -> GatewayException(code = code, httpStatus = httpStatus)
		}
}