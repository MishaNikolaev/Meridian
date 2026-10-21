package com.nmichail.meridian.features.registration.data.repository

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.registration.data.api.RegistrationApi
import com.nmichail.meridian.features.registration.data.mapper.toRequest
import com.nmichail.meridian.features.registration.data.model.RegistrationRequest
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import com.nmichail.meridian.features.registration.domain.repository.RegistrationRepository
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
	private val api: RegistrationApi,
	private val deviceIdentityRepository: DeviceIdentityRepository,
	private val sessionRepository: SessionRepository,
) : RegistrationRepository {

	override suspend fun register(data: RegistrationData): AuthSession {
		val keys = deviceIdentityRepository.getIdentity().deviceKeys(
			deviceName = deviceIdentityRepository.getDeviceName(),
			preKeys = deviceIdentityRepository.getPreKeys(),
		)
		val session = api.register(
			RegistrationRequest(
				username = data.username.trim(),
				displayName = data.displayName.trim(),
				password = data.password,
				device = keys.toRequest(),
			),
		)
		sessionRepository.save(session)
		return session
	}
}
