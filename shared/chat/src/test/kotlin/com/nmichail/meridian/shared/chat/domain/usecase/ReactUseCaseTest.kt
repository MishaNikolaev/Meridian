package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class ReactUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = ReactUseCase(repository)

	@Test
	fun `invoke EXPECT repository react`() = runTest {
		useCase("message", "LIKE")

		verify(repository).react("message", "LIKE")
	}
}