package com.nmichail.meridian.shared.group.data.repository

import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.ChatDto
import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.mapper.toEntity
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatProfile
import com.nmichail.meridian.shared.group.data.api.GroupApi
import com.nmichail.meridian.testutils.TestCoroutineExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class GroupRepositoryImplTest {
	private val chatApi: ChatApi = mock()
	private val groupApi: GroupApi = mock()
	private val sessionRepository: SessionRepository = mock()
	private val chatDataSource: ChatDataSource = mock()
	private val repository = GroupRepositoryImpl(
		chatApi,
		groupApi,
		sessionRepository,
		chatDataSource,
	)
	private val member: ChatMemberDto = mock {
		on { userId } doReturn "self"
		on { username } doReturn "anna"
		on { displayName } doReturn "Анна"
		on { role } doReturn "owner"
	}
	private val chat: ChatDto = mock {
		on { chatId } doReturn "chat-1"
		on { title } doReturn "Группа"
		on { description } doReturn ""
		on { photoId } doReturn null
	}
	private val details: ChatDetailsDto = mock {
		on { this.chat } doReturn chat
		on { members } doReturn listOf(member)
	}

	@Test
	fun `read chat EXPECT profile title`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(chatApi.getChat("chat-1")) doReturn details

		val actual = repository.readChat("chat-1")

		assertEquals(
			ChatProfile(
				title = "Группа",
				description = "",
				owner = "Анна",
				members = listOf(member.toEntity()),
				selfId = "self",
				photoId = "",
			),
			actual,
		)
	}

	@Test
	fun `leave chat EXPECT chat removed`() = runTest {
		val chat: Chat = mock { on { id } doReturn "chat-1" }
		whenever(chatDataSource.loadChats()) doReturn listOf(chat)

		repository.leaveChat("chat-1")

		verify(chatDataSource).saveChats(emptyList())
	}
}
