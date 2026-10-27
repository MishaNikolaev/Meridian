package com.nmichail.meridian.features.chat.domain.usecase

import com.nmichail.meridian.features.chat.domain.repository.ChatRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetChatTextUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = GetChatTextUseCase(repository)

	@Test
	fun `invoke EXPECT stored text`() {
		whenever(repository.getText()) doReturn "тест"

		val actual = useCase()

		assertEquals("тест", actual)
	}
}