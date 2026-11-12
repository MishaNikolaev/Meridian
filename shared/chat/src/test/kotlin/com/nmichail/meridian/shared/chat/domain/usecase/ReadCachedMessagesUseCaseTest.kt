package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.data.memory.MessageMemory
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ReadCachedMessagesUseCaseTest {
	private val memory = MessageMemory()
	private val useCase = ReadCachedMessagesUseCase(memory)
	private val thread = ChatThread(
		lines = emptyList(),
		members = 2,
		readUpTo = null,
		reactions = emptyMap(),
		reactionViewers = emptyMap(),
		people = emptyList(),
		selfName = "",
	)

	@Test
	fun `missing chat EXPECT null`() {
		assertEquals(null, useCase("1"))
	}

	@Test
	fun `stored thread EXPECT thread`() {
		memory.threads["1"] = thread

		assertEquals(thread, useCase("1"))
	}
}
