package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.core.network.session.AuthSession
import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class SignInWorkspaceUseCaseTest {

	private val repository: AuthorizationRepository = mock()
	private val useCase = SignInWorkspaceUseCase(repository)
	private val session = AuthSession(
		userId = "user",
		deviceId = "device",
		accessToken = "access",
		refreshToken = "refresh",
		expiresIn = 3600,
	)

	@Test
	fun `invoke EXPECT repository session`() = runTest {
		whenever(repository.signInWorkspace("meridian.name.ru", "chief@name.ru", "meridian1")) doReturn session

		val actual = useCase("meridian.name.ru", "chief@name.ru", "meridian1")

		assertEquals(session, actual)
	}
}
