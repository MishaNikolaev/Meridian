package com.nmichail.meridian.features.chat.domain.usecase

import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SaveChatTextUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = SaveChatTextUseCase(repository)

	@Test
	fun `invoke EXPECT repository save`() {
		useCase("тест")

		verify(repository).save("тест")
	}
}