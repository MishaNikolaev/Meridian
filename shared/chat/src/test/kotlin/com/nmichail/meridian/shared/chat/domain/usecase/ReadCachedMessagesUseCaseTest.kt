package com.nmichail.meridian.shared.chat.domain.usecase

import android.content.Context
import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSourceImpl
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ReadCachedMessagesUseCaseTest {
	private val context: Context = mock {
		on { getSharedPreferences(any(), any()) } doReturn mock()
	}
	private val messageDataSource = MessageDataSourceImpl(context)
	private val useCase = ReadCachedMessagesUseCase(messageDataSource)
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
		messageDataSource.saveThread(chatId = "1", thread = thread)

		assertEquals(thread, useCase("1"))
	}
}
