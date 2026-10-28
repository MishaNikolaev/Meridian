package com.nmichail.meridian.features.chat.presentation

import com.nmichail.meridian.shared.chat.domain.entity.Chat
import com.nmichail.meridian.shared.chat.domain.entity.ChatDiscussion
import com.nmichail.meridian.shared.chat.domain.entity.ChatType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DiscussionsTest {

	private val chat = Chat(
		id = "1",
		type = ChatType.DIRECT,
		title = "Анна",
		preview = "комментарий",
		sentAt = "2026-10-22T12:00:00Z",
		unread = 0,
		corporate = true,
		mentioned = false,
		personId = "member",
		discussionRoot = "root",
		discussionText = "тема обсуждения",
	)

	@Test
	fun `thread EXPECT discussion row`() {
		val actual = listOf(chat).discussions()

		assertEquals(
			listOf(
				ChatDiscussion(
					chatId = "1",
					messageId = "root",
					title = "Анна",
					text = "тема обсуждения",
					comment = "комментарий",
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
	fun `message without thread EXPECT no discussion`() {
		val plain = chat.copy(
			discussionRoot = null,
			discussionText = null,
		)

		val actual = listOf(plain).discussions()

		assertEquals(emptyList<ChatDiscussion>(), actual)
	}
}
