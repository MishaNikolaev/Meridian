package com.nmichail.meridian.shared.chat.data.repository

import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionStore
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.model.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.model.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.model.ChatPageDto
import com.nmichail.meridian.shared.chat.data.model.ChatSummaryDto
import com.nmichail.meridian.shared.chat.data.model.ChatTypeDto
import com.nmichail.meridian.shared.chat.data.model.CreateChatDto
import com.nmichail.meridian.shared.chat.data.model.MessageDto
import com.nmichail.meridian.shared.chat.data.model.MessagePageDto
import com.nmichail.meridian.shared.chat.data.model.ReceiptDto
import com.nmichail.meridian.shared.chat.data.model.ReceiptKindDto
import com.nmichail.meridian.shared.chat.data.model.UserCardDto
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatSummary
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChatRepositoryImplTest {

	private val api: ChatApi = mock()
	private val sessionStore: SessionStore = mock()
	private val repository = ChatRepositoryImpl(api, sessionStore)

	@Test
	fun `load chats EXPECT summaries`() = runTest {
		whenever(sessionStore.getUserId()) doReturn "self"
		whenever(api.getChats()) doReturn ChatPageDto(
			items = listOf(
				ChatSummaryDto(
					chatId = "1",
					type = ChatTypeDto.GROUP,
					title = "Обсуждение",
					unreadCount = 2,
				),
			),
		)
		val expected = listOf(
			ChatSummary(
				id = "1",
				type = ChatType.GROUP,
				title = "Обсуждение",
				preview = "",
				sentAt = null,
				unread = 2,
			),
		)

		val actual = repository.loadChats()

		assertEquals(expected, actual)
	}

	@Test
	fun `create direct chat EXPECT created chat`() = runTest {
		whenever(api.getUsers("member")) doReturn listOf(
			UserCardDto(userId = "member-id", username = "member", displayName = "Анна"),
		)
		whenever(
			api.create(
				CreateChatDto(
					type = ChatTypeDto.DIRECT,
					title = null,
					memberIds = listOf("member-id"),
				),
			),
		) doReturn details(title = "Анна")

		val actual = repository.create(ChatType.DIRECT, "", "member")

		assertEquals(CreatedChat(id = "1", title = "Анна"), actual)
	}

	@Test
	fun `create without login EXPECT no user lookup`() = runTest {
		whenever(
			api.create(
				CreateChatDto(
					type = ChatTypeDto.GROUP,
					title = "Обсуждение",
					memberIds = emptyList(),
				),
			),
		) doReturn details(title = "Обсуждение")

		repository.create(ChatType.GROUP, "Обсуждение", " ")

		verify(api, never()).getUsers(any())
	}

	@Test
	fun `unknown login EXPECT user not found`() = runTest {
		whenever(api.getUsers("ghost")) doReturn emptyList()

		assertThrows<GatewayException> {
			repository.create(ChatType.DIRECT, "", "ghost")
		}
	}

	@Test
	fun `load deleted message EXPECT empty text`() = runTest {
		whenever(sessionStore.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(
			items = listOf(message(deleted = true)),
		)

		val actual = repository.loadMessages("1")

		assertEquals("", actual.lines.first().text)
	}

	@Test
	fun `load deleted message EXPECT no read target`() = runTest {
		whenever(sessionStore.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(
			items = listOf(message(deleted = true)),
		)
		val expected = ChatThread(
			lines = listOf(
				ChatLine(
					id = "message",
					mine = true,
					text = "",
					sentAt = "2026-01-01T00:00:00Z",
					author = null,
				),
			),
			members = 1,
			readUpTo = null,
		)

		val actual = repository.loadMessages("1")

		assertEquals(expected, actual)
	}

	@Test
	fun `mark read EXPECT read receipt`() = runTest {
		val statement: HttpStatement = mock()
		whenever(api.sendReceipt("message", ReceiptDto(kind = ReceiptKindDto.READ))) doReturn statement

		repository.markRead("message")

		verify(api).sendReceipt("message", ReceiptDto(kind = ReceiptKindDto.READ))
	}

	private fun details(title: String): ChatDetailsDto = ChatDetailsDto(
		chat = ChatSummaryDto(
			chatId = "1",
			type = ChatTypeDto.DIRECT,
			title = title,
		),
		members = listOf(
			ChatMemberDto(userId = "self", username = "chief", displayName = "Сергей"),
		),
	)

	private fun message(deleted: Boolean): MessageDto = MessageDto(
		messageId = "message",
		chatId = "1",
		senderUserId = "self",
		createdAt = "2026-01-01T00:00:00Z",
		deleted = deleted,
	)
}
