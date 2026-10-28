package com.nmichail.meridian.shared.chat.domain.usecase

import com.nmichail.meridian.core.testutils.TestCoroutineExtension
import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatLine
import com.nmichail.meridian.shared.chat.domain.entity.ChatThread
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(TestCoroutineExtension::class)
class LoadDiscussionsUseCaseTest {

	private val loadMessagesUseCase: LoadMessagesUseCase = mock()
	private val useCase = LoadDiscussionsUseCase(loadMessagesUseCase)
	private val chat = Chat(
		id = "1",
		type = ChatType.DIRECT,
		title = "Анна",
		preview = "последнее в чате",
		sentAt = null,
		unread = 0,
		personId = "member",
	)

	@Test
	fun `invoke EXPECT thread row`() = runTest {
		whenever(loadMessagesUseCase.invoke(chat.id)) doReturn ChatThread(
			lines = listOf(
				ChatLine(
					id = "root",
					mine = false,
					text = "тема обсуждения",
					sentAt = "2026-10-21T10:00:00Z",
				),
				ChatLine(
					id = "comment",
					mine = true,
					text = "комментарий",
					sentAt = "2026-10-22T12:00:00Z",
					author = "Анна",
					threadRoot = "root",
				),
			),
			members = 2,
		)

		val actual = useCase(listOf(chat))

		assertEquals(
			listOf(
				ChatDiscussion(
					chatId = "1",
					messageId = "root",
					title = "Анна",
					text = "тема обсуждения",
					comment = "Анна: комментарий",
					sentAt = "2026-10-22T12:00:00Z",
					corporate = true,
					type = ChatType.DIRECT,
					personId = "member",
				),
			),
			actual,
		)
	}

	@Test
	fun `replace chat EXPECT updated discussion and the rest`() = runTest {
		whenever(loadMessagesUseCase.invoke(chat.id)) doReturn ChatThread(
			lines = listOf(
				ChatLine(
					id = "root",
					mine = false,
					text = "тема обсуждения",
					sentAt = "2026-10-21T10:00:00Z",
				),
				ChatLine(
					id = "comment",
					mine = true,
					text = "комментарий",
					sentAt = "2026-10-22T12:00:00Z",
					author = "Анна",
					threadRoot = "root",
				),
			),
			members = 2,
		)
		val other = ChatDiscussion(
			chatId = "2",
			messageId = "root",
			title = "Сергей",
			text = "старая тема",
			comment = "",
			sentAt = "2026-10-20T10:00:00Z",
			corporate = true,
			type = ChatType.GROUP,
			personId = null,
		)

		val actual = useCase.replaceChat(
			chatId = chat.id,
			chats = listOf(chat),
			discussions = listOf(
				other,
				ChatDiscussion(
					chatId = "1",
					messageId = "old",
					title = "Анна",
					text = "старое",
					comment = "",
					sentAt = "2026-10-01T10:00:00Z",
					corporate = true,
					type = ChatType.DIRECT,
					personId = "member",
				),
			),
		)

		assertEquals(
			listOf(
				ChatDiscussion(
					chatId = "1",
					messageId = "root",
					title = "Анна",
					text = "тема обсуждения",
					comment = "Анна: комментарий",
					sentAt = "2026-10-22T12:00:00Z",
					corporate = true,
					type = ChatType.DIRECT,
					personId = "member",
				),
				other,
			),
			actual,
		)
	}
}
