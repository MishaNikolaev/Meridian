package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExtendWith(TestCoroutineExtension::class)
class SendMessageUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = SendMessageUseCase(repository)

	@Test
	fun `invoke EXPECT repository send`() = runTest {
		useCase("1", "привет", "", "", "")

		verify(repository).send("1", "привет", "", "", "")
	}
}