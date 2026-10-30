package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.device.DeviceIdentityRepository
import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.testutils.doThrowSafe
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class RestoreSessionUseCaseTest {

	private val sessionRepository: SessionRepository = mock()
	private val sessionRefresher: SessionRefresher = mock()
	private val deviceIdentityRepository: DeviceIdentityRepository = mock()
	private val useCase = RestoreSessionUseCase(sessionRepository, sessionRefresher, deviceIdentityRepository)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `signed in session EXPECT true`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn true

		val actual = useCase()

		assertEquals(true, actual)
	}

	@Test
	fun `stored access token EXPECT true`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getAccessToken()) doReturn "access"

		val actual = useCase()

		assertEquals(true, actual)
	}

	@Test
	fun `stored access token EXPECT no refresh`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getAccessToken()) doReturn "access"

		useCase()

		verify(sessionRefresher, never()).refresh()
	}

	@Test
	fun `missing refresh token EXPECT false`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn null

		val actual = useCase()

		assertEquals(false, actual)
	}

	@Test
	fun `missing refresh token EXPECT cleared session`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn null

		useCase()

		verify(sessionRepository).clear()
	}

	@Test
	fun `missing refresh token EXPECT reset device`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn null

		useCase()

		verify(deviceIdentityRepository).reset()
	}

	@Test
	fun `refresh returns session EXPECT true`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(sessionRefresher.refresh()) doReturn session

		val actual = useCase()

		assertEquals(true, actual)
	}

	@Test
	fun `refresh returns nothing EXPECT false`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(sessionRefresher.refresh()) doReturn null

		val actual = useCase()

		assertEquals(false, actual)
	}

	@Test
	fun `refresh returns nothing EXPECT cleared session`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(sessionRefresher.refresh()) doReturn null

		useCase()

		verify(sessionRepository).clear()
	}

	@Test
	fun `refresh without connection EXPECT false`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(sessionRefresher.refresh()) doThrowSafe IOException("down")

		val actual = useCase()

		assertEquals(false, actual)
	}

	@Test
	fun `refresh without connection EXPECT kept session`() = runTest {
		whenever(sessionRepository.isSignedIn()) doReturn false
		whenever(sessionRepository.getRefreshToken()) doReturn "refresh"
		whenever(sessionRefresher.refresh()) doThrowSafe IOException("down")

		useCase()

		verify(sessionRepository, never()).clear()
	}
}
