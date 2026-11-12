package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.ReactionMemory
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ReadReactionsUseCaseTest {
	private val repository: ChatRepository = mock()
	private val useCase = ReadReactionsUseCase(repository)

	@Test
	fun `invoke EXPECT stored reactions`() {
		val memory = ReactionMemory(
			chosen = mapOf("message" to setOf("LIKE")),
			used = emptyList(),
		)
		whenever(repository.readReactions()) doReturn memory

		assertEquals(memory, useCase())
	}
}