package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SaveReactionUseCaseTest {
	private val repository: ReactionRepository = mock()
	private val useCase = SaveReactionUseCase(repository)

	@Test
	fun `invoke EXPECT repository save`() {
		useCase("message", setOf("LIKE"))

		verify(repository).saveReaction(
			messageId = "message",
			reactions = setOf("LIKE"),
			saveUsed = true,
		)
	}
}
