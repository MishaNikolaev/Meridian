package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import java.io.IOException
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
	private val sessionRepository: SessionRepository,
	private val sessionRefresher: SessionRefresher,
	private val deviceIdentityRepository: DeviceIdentityRepository,
) {

	suspend operator fun invoke(): Boolean {
		if (sessionRepository.isSignedIn()) {
			return true
		}
		if (sessionRepository.getRefreshToken() == null) {
			dropSession()
			return false
		}
		val session = try {
			sessionRefresher.refresh()
		} catch (_: IOException) {
			return false
		}
		if (session != null) {
			return true
		}
		dropSession()
		return false
	}

	private fun dropSession() {
		sessionRepository.clear()
		deviceIdentityRepository.reset()
	}
}
