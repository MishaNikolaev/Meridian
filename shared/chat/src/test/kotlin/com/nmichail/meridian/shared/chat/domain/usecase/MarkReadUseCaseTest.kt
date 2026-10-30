package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class MarkReadUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = MarkReadUseCase(repository)

	@Test
	fun `invoke EXPECT repository mark read`() = runTest {
		useCase("message")

		verify(repository).markRead("message")
	}
}
