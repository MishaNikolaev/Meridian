package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.device.DeviceIdentityStore
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionStore
import java.io.IOException
import javax.inject.Inject

class RestoreSessionUseCase @Inject constructor(
	private val sessionStore: SessionStore,
	private val sessionRefresher: SessionRefresher,
	private val deviceIdentityStore: DeviceIdentityStore,
) {

	suspend operator fun invoke(): Boolean {
		if (sessionStore.isSignedIn()) {
			return true
		}
		if (sessionStore.refreshToken() == null) {
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
		sessionStore.clear()
		deviceIdentityStore.reset()
	}
}
