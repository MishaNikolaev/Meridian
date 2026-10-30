package com.nmichail.meridian.shared.session.domain.usecase

import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class LogoutUseCaseTest {

	private val repository: AuthorizationRepository = mock()
	private val useCase = LogoutUseCase(repository)

	@Test
	fun `invoke EXPECT repository logout`() = runTest {
		useCase()

		verify(repository).logout()
	}
}
