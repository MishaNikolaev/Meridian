package com.nmichail.meridian.shared.chat.data.cipher

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpenedMessageTest {

	private val reply = encodeOpenedMessage(
		text = "Да, залил",
		replyAuthor = "Соколова Анна Сергеевна",
		replyText = "Кирилл, макеты экрана оплаты уже обновил?",
	)

	@Test
	fun `opened text EXPECT unchanged`() {
		val actual = decodeOpenedMessage("привет")

		assertEquals("привет", actual.text)
	}

	@Test
	fun `reply EXPECT message text`() {
		assertEquals("Да, залил", decodeOpenedMessage(reply).text)
	}

	@Test
	fun `reply EXPECT quoted author`() {
		assertEquals("Соколова Анна Сергеевна", decodeOpenedMessage(reply).replyAuthor)
	}
}
