package com.nmichail.meridian.features.registration.data.repository

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.network.device.DeviceKeys
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.features.registration.data.api.RegistrationApi
import com.nmichail.meridian.features.registration.data.mapper.toRequest
import com.nmichail.meridian.features.registration.data.model.RegistrationRequest
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RegistrationRepositoryImplTest {

	private val api: RegistrationApi = mock()
	private val deviceIdentityRepository: DeviceIdentityRepository = mock()
	private val sessionRepository: SessionRepository = mock()
	private val repository = RegistrationRepositoryImpl(api, deviceIdentityRepository, sessionRepository)
	private val identity: DeviceIdentity = mock()
	private val preKeys: DevicePreKeys = mock()
	private val keys = DeviceKeys(
		deviceId = "device",
		name = "phone",
		identityPublicKey = "public",
		signedPreKeyId = 1,
		signedPreKeyPublic = "signed",
		signedPreKeySignature = "signature",
		oneTimePreKeyId = 1,
		oneTimePreKeyPublic = "once",
	)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `register EXPECT trimmed request`() = runTest {
		stubKeys()
		val request = RegistrationRequest(
			username = "chief",
			displayName = "Сергей",
			password = "meridian1",
			device = keys.toRequest(),
		)
		whenever(api.register(request)) doReturn session

		repository.register(
			RegistrationData(
				username = " chief ",
				displayName = " Сергей ",
				password = "meridian1",
			),
		)

		verify(api).register(request)
	}

	@Test
	fun `register EXPECT saved session`() = runTest {
		stubKeys()
		whenever(api.register(any())) doReturn session

		repository.register(
			RegistrationData(
				username = "chief",
				displayName = "Сергей",
				password = "meridian1",
			),
		)

		verify(sessionRepository).save(session)
	}

	private suspend fun stubKeys() {
		whenever(deviceIdentityRepository.getIdentity()) doReturn identity
		whenever(deviceIdentityRepository.getDeviceName()) doReturn "phone"
		whenever(deviceIdentityRepository.getPreKeys()) doReturn preKeys
		whenever(identity.deviceKeys("phone", preKeys)) doReturn keys
	}
}
