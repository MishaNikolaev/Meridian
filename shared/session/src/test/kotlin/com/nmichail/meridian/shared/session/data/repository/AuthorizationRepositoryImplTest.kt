package com.nmichail.meridian.shared.session.data.repository

import com.nmichail.meridian.core.network.device.DeviceIdentity
import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.device.DevicePreKeys
import com.nmichail.meridian.core.network.device.DeviceKeys
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.core.testutils.doThrowSafe
import com.nmichail.meridian.shared.session.data.api.AuthorizationApi
import com.nmichail.meridian.shared.session.data.mapper.toRequest
import com.nmichail.meridian.shared.session.data.model.LogoutRequest
import com.nmichail.meridian.shared.session.data.model.PasswordAuthorizationRequest
import com.nmichail.meridian.shared.session.data.model.WorkspaceAuthorizationRequest
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthorizationRepositoryImplTest {

	private val api: AuthorizationApi = mock()
	private val deviceIdentityRepository: DeviceIdentityRepository = mock()
	private val sessionRepository: SessionRepository = mock()
	private val sessionRefresher: SessionRefresher = mock()
	private val repository = AuthorizationRepositoryImpl(
		api,
		deviceIdentityRepository,
		sessionRepository,
		sessionRefresher,
	)
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
	fun `sign in EXPECT trimmed password request`() = runTest {
		stubKeys()
		val request = PasswordAuthorizationRequest(
			username = "chief",
			password = "meridian1",
			device = keys.toRequest(),
		)
		whenever(api.authorizeByPassword(request)) doReturn session

		repository.signIn(" chief ", "meridian1")

		verify(api).authorizeByPassword(request)
	}

	@Test
	fun `sign in EXPECT saved session`() = runTest {
		stubKeys()
		whenever(api.authorizeByPassword(any())) doReturn session

		repository.signIn("chief", "meridian1")

		verify(sessionRepository).save(session)
	}

	@Test
	fun `sign in workspace EXPECT trimmed request`() = runTest {
		stubKeys()
		val request = WorkspaceAuthorizationRequest(
			host = "meridian.ftc.ru",
			email = "chief@ftc.ru",
			password = "meridian1",
			device = keys.toRequest(),
		)
		whenever(api.authorizeWorkspace(request)) doReturn session

		repository.signInWorkspace(" meridian.ftc.ru ", " chief@ftc.ru ", "meridian1")

		verify(api).authorizeWorkspace(request)
	}

	@Test
	fun `refresh without session EXPECT unauthorized`() = runTest {
		whenever(sessionRefresher.refresh()) doReturn null

		assertThrows<GatewayException> {
			repository.refresh()
		}
	}

	@Test
	fun `refresh EXPECT session`() = runTest {
		whenever(sessionRefresher.refresh()) doReturn session

		val actual = repository.refresh()

		assertEquals(session, actual)
	}

	@Test
	fun `logout EXPECT logout request`() = runTest {
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"

		repository.logout()

		verify(api).logout(LogoutRequest(refreshToken = "refresh"))
	}

	@Test
	fun `logout EXPECT cleared session`() = runTest {
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"

		repository.logout()

		verify(sessionRepository).clear()
	}

	@Test
	fun `logout EXPECT reset device`() = runTest {
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"

		repository.logout()

		verify(deviceIdentityRepository).reset()
	}

	@Test
	fun `logout without token EXPECT no api call`() = runTest {
		whenever(sessionRepository.getRefreshToken()) doReturn null

		repository.logout()

		verify(api, never()).logout(any())
	}

	@Test
	fun `logout failure EXPECT cleared session`() = runTest {
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(api.logout(any())) doThrowSafe IOException("down")

		try {
			repository.logout()
		} catch (_: IOException) {
		}

		verify(sessionRepository).clear()
	}

	private suspend fun stubKeys() {
		whenever(deviceIdentityRepository.getIdentity()) doReturn identity
		whenever(deviceIdentityRepository.getDeviceName()) doReturn "phone"
		whenever(deviceIdentityRepository.getPreKeys()) doReturn preKeys
		whenever(identity.deviceKeys("phone", preKeys)) doReturn keys
	}
}
