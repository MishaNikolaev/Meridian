package com.nmichail.meridian.shared.chat.domain.usecase

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
		whenever(repository.readReactions()) doReturn mapOf("message" to "LIKE")

		assertEquals(mapOf("message" to "LIKE"), useCase())
	}
}