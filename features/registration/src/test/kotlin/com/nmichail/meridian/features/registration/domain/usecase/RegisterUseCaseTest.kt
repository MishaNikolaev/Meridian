package com.nmichail.meridian.features.registration.domain.usecase

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.features.registration.domain.entity.RegistrationData
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.features.registration.domain.repository.RegistrationRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class RegisterUseCaseTest {

	private val repository: RegistrationRepository = mock()
	private val useCase = RegisterUseCase(repository)
	private val data = RegistrationData(
		username = "chief",
		displayName = "Сергей",
		password = "meridian1",
	)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `invoke EXPECT repository session`() = runTest {
		whenever(repository.register(data)) doReturn session

		val actual = useCase(data)

		assertEquals(session, actual)
	}
}
