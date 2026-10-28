package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.memory.MessageMemory
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadMessagesUseCaseTest {

	private val repository: ChatRepository = mock()
	private val memory = MessageMemory()
	private val useCase = LoadMessagesUseCase(repository, memory)
	private val cached = ReadCachedMessagesUseCase(memory)
	private val thread = ChatThread(
		lines = emptyList(),
		members = 2,
		readUpTo = null,
		reactions = emptyMap(),
		reactionViewers = emptyMap(),
		people = emptyList(),
	)

	@Test
	fun `invoke EXPECT chat thread`() = runTest {
		whenever(repository.loadMessages("1")) doReturn thread

		val actual = useCase("1")

		assertEquals(thread, actual)
	}

	@Test
	fun `loaded thread EXPECT remembered`() = runTest {
		whenever(repository.loadMessages("1")) doReturn thread

		useCase("1")

		assertEquals(thread, cached("1"))
	}
}
