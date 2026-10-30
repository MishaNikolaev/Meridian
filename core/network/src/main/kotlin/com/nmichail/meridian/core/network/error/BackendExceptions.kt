package com.nmichail.meridian.core.network.error

class PasswordMismatchException(httpStatus: Int) : GatewayException(
	code = GatewayCode.PASSWORD_MISMATCH,
	httpStatus = httpStatus,
)

class NotOnServerException(httpStatus: Int) : GatewayException(
	code = GatewayCode.NOT_ON_SERVER,
	httpStatus = httpStatus,
)

class ServerNotFoundException(
	code: GatewayCode,
	httpStatus: Int,
) : GatewayException(
	code = code,
	httpStatus = httpStatus,
)

class DeviceTakenException(httpStatus: Int) : GatewayException(
	code = GatewayCode.DEVICE_TAKEN,
	httpStatus = httpStatus,
)

class ForbiddenException(httpStatus: Int) : GatewayException(
	code = GatewayCode.FORBIDDEN,
	httpStatus = httpStatus,
)

class TooManyRequestsException(httpStatus: Int) : GatewayException(
	code = GatewayCode.TOO_MANY_REQUESTS,
	httpStatus = httpStatus,
)

class PasswordNotSetException(httpStatus: Int) : GatewayException(
	code = GatewayCode.PASSWORD_NOT_SET,
	httpStatus = httpStatus,
)

class EntityNotFoundException(httpStatus: Int) : GatewayException(
	code = GatewayCode.USER_NOT_FOUND,
	httpStatus = httpStatus,
)

class BadGatewayResponseException(httpStatus: Int) : GatewayException(
	code = GatewayCode.UNKNOWN,
	httpStatus = httpStatus,
)

class SessionRefreshException : Exception()