package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class ReactionUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = ReactionUseCase(repository)

	@Test
	fun `read EXPECT stored reactions`() {
		whenever(repository.readReactions()) doReturn mapOf("message" to "LIKE")

		assertEquals(mapOf("message" to "LIKE"), useCase.read())
	}

	@Test
	fun `remember EXPECT repository remember`() {
		useCase.remember("message", "LIKE")

		verify(repository).saveReaction("message", "LIKE")
	}

	@Test
	fun `send EXPECT repository react`() = runTest {
		useCase.send("message", "LIKE")

		verify(repository).react("message", "LIKE")
	}
}
