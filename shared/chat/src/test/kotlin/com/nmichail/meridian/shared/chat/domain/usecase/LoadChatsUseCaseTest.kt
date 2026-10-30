package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadChatsUseCaseTest {

	private val repository: ChatRepository = mock()
	private val useCase = LoadChatsUseCase(repository)
	private val chats = listOf(
		Chat(
			id = "1",
			type = ChatType.DIRECT,
			title = "Анна",
			preview = "",
			sentAt = null,
			unread = 0,
			corporate = true,
			mentioned = false,
			personId = null,
			discussionRoot = null,
			discussionText = null,
		),
	)

	@Test
	fun `invoke EXPECT repository chats`() = runTest {
		whenever(repository.loadChats()) doReturn chats

		val actual = useCase()

		assertEquals(chats, actual)
	}
}
