package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SaveReactionUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = SaveReactionUseCase(repository)

	@Test
	fun `invoke EXPECT repository save`() {
		useCase("message", "LIKE")

		verify(repository).saveReaction("message", "LIKE")
	}
}
