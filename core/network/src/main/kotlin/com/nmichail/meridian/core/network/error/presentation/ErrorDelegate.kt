package com.nmichail.meridian.core.network.error.presentation

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.error.EntityNotFoundException
import com.nmichail.meridian.core.network.error.ForbiddenException
import com.nmichail.meridian.core.network.error.GatewayCode
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.error.SessionRefreshException
import com.nmichail.meridian.core.network.session.SessionRepository
import javax.inject.Inject

class ErrorDelegate @Inject constructor(
	private val errorLogger: ErrorLogger,
	private val defaultErrorReactionConverter: DefaultErrorReactionConverter,
	private val defaultErrorEventConverter: DefaultErrorEventConverter,
	private val errorRouter: ErrorRouter,
	private val sessionRepository: SessionRepository,
	private val deviceIdentityRepository: DeviceIdentityRepository,
) {

	fun processNonBlockingError(exception: Exception): NonBlockingErrorProcessResult =
		process(
			exception = exception,
			handled = { NonBlockingErrorProcessResult.Handled },
			notHandled = {
				NonBlockingErrorProcessResult.NotHandled(
					recommended = defaultErrorEventConverter.convert(exception),
				)
			},
		)

	fun <I : Any> processBlockingError(
		exception: Exception,
		retryIntent: I,
		backIntent: I?,
	): BlockingErrorProcessResult<I> =
		process(
			exception = exception,
			handled = { BlockingErrorProcessResult.Handled },
			notHandled = {
				BlockingErrorProcessResult.NotHandled(
					recommended = defaultErrorReactionConverter.convert(
						exception = exception,
						retryIntent = retryIntent,
						backIntent = backIntent,
					),
				)
			},
		)

	private fun <R> process(
		exception: Exception,
		handled: () -> R,
		notHandled: () -> R,
	): R {
		if (errorLogger.needLog(exception)) {
			errorLogger.log(exception)
		}
		return when (exception) {
			is ForbiddenException -> handleForbidden(
				handled = handled,
				notHandled = notHandled,
			)

			is EntityNotFoundException -> handleMissingEntity(
				handled = handled,
				notHandled = notHandled,
			)

			is SessionRefreshException -> {
				logout(userBlocked = false)
				handled()
			}

			is GatewayException -> if (exception.code == GatewayCode.UNAUTHORIZED) {
				handled()
			} else {
				notHandled()
			}

			else -> notHandled()
		}
	}

	private fun <R> handleForbidden(
		handled: () -> R,
		notHandled: () -> R,
	): R {
		if (!sessionRepository.isSignedIn()) {
			return notHandled()
		}
		if (sessionRepository.getHost() != null) {
			sessionRepository.saveHost("")
			errorRouter.navigateToWorkspace()
			return handled()
		}
		logout(userBlocked = true)
		return handled()
	}

	private fun <R> handleMissingEntity(
		handled: () -> R,
		notHandled: () -> R,
	): R {
		if (!sessionRepository.isSignedIn()) {
			return notHandled()
		}
		logout(userBlocked = false)
		return handled()
	}

	private fun logout(userBlocked: Boolean) {
		sessionRepository.clear()
		deviceIdentityRepository.reset()
		errorRouter.navigateToLogout(userBlocked = userBlocked)
	}
}
