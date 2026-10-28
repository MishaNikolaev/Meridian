package com.nmichail.meridian.shared.chat.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.nmichail.meridian.core.network.error.GatewayException
import com.nmichail.meridian.core.network.session.SessionRepository
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.dto.ChatDetailsDto
import com.nmichail.meridian.shared.chat.data.dto.ChatMemberDto
import com.nmichail.meridian.shared.chat.data.dto.ChatPageDto
import com.nmichail.meridian.shared.chat.data.dto.ChatDto
import com.nmichail.meridian.shared.chat.data.dto.ChatTypeDto
import com.nmichail.meridian.shared.chat.data.dto.CreateChatDto
import com.nmichail.meridian.shared.chat.data.dto.MessageDto
import com.nmichail.meridian.shared.chat.data.dto.MessagePageDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionDto
import com.nmichail.meridian.shared.chat.data.dto.ReactionRequestDto
import com.nmichail.meridian.shared.chat.data.dto.ReadKindDto
import com.nmichail.meridian.shared.chat.data.dto.ReadRequestDto
import com.nmichail.meridian.shared.chat.data.dto.UserCardDto
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import com.nmichail.meridian.shared.chat.domain.entity.CreatedChat
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChatRepositoryImplTest {

	private val api: ChatApi = mock()
	private val sessionRepository: SessionRepository = mock()
	private val context: Context = mock()
	private val preferences: SharedPreferences = mock()
	private val editor: SharedPreferences.Editor = mock()
	private lateinit var repository: ChatRepositoryImpl

	@BeforeEach
	fun setUp() {
		whenever(context.getSharedPreferences(any(), any())) doReturn preferences
		whenever(preferences.edit()) doReturn editor
		whenever(editor.putString(any(), any())) doReturn editor
		repository = ChatRepositoryImpl(
			api,
			sessionRepository,
			MemberDataSourceImpl(api),
			ReactionDataSourceImpl(context),
			DeletionDataSourceImpl(context),
		)
	}

	@Test
	fun `load chats EXPECT summaries`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChats()) doReturn ChatPageDto(
			items = listOf(
				ChatDto(
					chatId = "1",
					type = ChatTypeDto.GROUP,
					title = "Обсуждение",
					personId = null,
					unreadCount = 2,
					lastMessage = null,
					corporate = true,
				),
			),
		)
		val expected = listOf(
			Chat(
				id = "1",
				type = ChatType.GROUP,
				title = "Обсуждение",
				preview = "",
				sentAt = null,
				unread = 2,
				corporate = true,
				mentioned = false,
				personId = null,
				discussionRoot = null,
				discussionText = null,
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
			api.createChat(
				CreateChatDto(
					type = ChatTypeDto.DIRECT,
					title = null,
					memberIds = listOf("member-id"),
				),
			),
		) doReturn details(title = "Анна")

		val actual = repository.create(ChatType.DIRECT, "", listOf("member"))

		assertEquals(
			CreatedChat(
				id = "1",
				title = "Анна",
				corporate = true,
			),
			actual,
		)
	}

	@Test
	fun `create without login EXPECT no user lookup`() = runTest {
		whenever(
			api.createChat(
				CreateChatDto(
					type = ChatTypeDto.GROUP,
					title = "Обсуждение",
					memberIds = emptyList(),
				),
			),
		) doReturn details(title = "Обсуждение")

		repository.create(ChatType.GROUP, "Обсуждение", listOf(" "))

		verify(api, never()).getUsers(any())
	}

	@Test
	fun `unknown login EXPECT user not found`() = runTest {
		whenever(api.getUsers("ghost")) doReturn emptyList()

		assertThrows<GatewayException> {
			repository.create(ChatType.DIRECT, "", listOf("ghost"))
		}
	}

	@Test
	fun `load deleted message EXPECT empty text`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(
			items = listOf(message(deleted = true)),
		)

		val actual = repository.loadMessages("1")

		assertEquals("Сообщение удалено", actual.lines.first().text)
		assertEquals(true, actual.lines.first().deleted)
	}

	@Test
	fun `load deleted message EXPECT no read target`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(
			items = listOf(message(deleted = true)),
		)
		val expected = ChatThread(
			lines = listOf(
				ChatLine(
					id = "message",
					mine = true,
					text = "Сообщение удалено",
					sentAt = "2026-01-01T00:00:00Z",
					author = null,
					replyAuthor = null,
					replyText = null,
					threadRoot = null,
					deleted = true,
					edited = false,
				),
			),
			members = 1,
			readUpTo = null,
			reactions = emptyMap(),
			reactionViewers = emptyMap(),
			people = emptyList(),
		)

		val actual = repository.loadMessages("1")

		assertEquals(expected, actual)
	}

	@Test
	fun `load messages EXPECT cached reaction`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(items = listOf(message(deleted = false)))
		whenever(preferences.getString("message_reactions", null)) doReturn """{"message":"LIKE"}"""

		val actual = repository.loadMessages("1")

		assertEquals(mapOf("message" to "LIKE"), actual.reactions)
	}

	@Test
	fun `load messages EXPECT server reaction`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(
			items = listOf(
				message(deleted = false).copy(
					reactions = listOf(
						ReactionDto(
							userId = "other",
							reaction = "FIRE",
							createdAt = null,
						),
						ReactionDto(
							userId = "self",
							reaction = "LIKE",
							createdAt = null,
						),
					),
				),
			),
		)

		val actual = repository.loadMessages("1")

		assertEquals(mapOf("message" to "LIKE"), actual.reactions)
	}

	@Test
	fun `react EXPECT saved reaction`() = runTest {
		whenever(preferences.getString("message_reactions", null)) doReturn null
		whenever(api.setReaction("message", ReactionRequestDto(reaction = "LIKE"))) doReturn mock()

		repository.react("message", "LIKE")

		verify(editor).putString("message_reactions", """{"message":"LIKE"}""")
	}

	@Test
	fun `react EXPECT reaction request`() = runTest {
		whenever(preferences.getString("message_reactions", null)) doReturn null
		whenever(api.setReaction("message", ReactionRequestDto(reaction = "LIKE"))) doReturn mock()

		repository.react("message", "LIKE")

		verify(api).setReaction("message", ReactionRequestDto(reaction = "LIKE"))
	}

	@Test
	fun `load cached deletion EXPECT tombstone`() = runTest {
		whenever(sessionRepository.getUserId()) doReturn "self"
		whenever(api.getChat("1")) doReturn details(title = "Анна")
		whenever(api.getMessages("1")) doReturn MessagePageDto(items = listOf(message(deleted = false)))
		whenever(preferences.getStringSet("deleted_messages", null)) doThrow ClassCastException()
		whenever(preferences.getString("deleted_messages", null)) doReturn "message"

		val actual = repository.loadMessages("1")

		assertEquals("Сообщение удалено", actual.lines.first().text)
		assertEquals(true, actual.lines.first().deleted)
	}

	@Test
	fun `delete EXPECT remembered after request`() = runTest {
		val statement: HttpStatement = mock()
		whenever(api.deleteMessages("message")) doReturn statement
		whenever(preferences.getStringSet("deleted_messages", null)) doReturn null
		whenever(preferences.getString("deleted_messages", null)) doReturn null

		repository.delete("message")

		verify(api).deleteMessages("message")
		verify(editor).putStringSet("deleted_messages", setOf("message"))
	}

	@Test
	fun `mark read EXPECT read request`() = runTest {
		val statement: HttpStatement = mock()
		whenever(api.markRead("message", ReadRequestDto(kind = ReadKindDto.READ))) doReturn statement

		repository.markRead("message")

		verify(api).markRead("message", ReadRequestDto(kind = ReadKindDto.READ))
	}

	private fun details(title: String): ChatDetailsDto = ChatDetailsDto(
		chat = ChatDto(
			chatId = "1",
			type = ChatTypeDto.DIRECT,
			title = title,
			personId = null,
			unreadCount = 0,
			lastMessage = null,
			corporate = true,
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
		deviceMessages = emptyList(),
		reactions = emptyList(),
	)
}
