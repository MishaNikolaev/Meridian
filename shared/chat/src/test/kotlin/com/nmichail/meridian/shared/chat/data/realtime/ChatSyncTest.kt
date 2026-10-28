package com.nmichail.meridian.shared.chat.data.realtime

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ChatSyncTest {

	@Test
	fun `sync message EXPECT chat id`() {
		val json = Json { ignoreUnknownKeys = true }
		val text = """
			{"type":"sync","event":{"id":1,"type":"message.created","payload":{"chatId":"chat"},"createdAt":"2026-01-01T00:00:00Z"}}
		""".trim()
		val sync = json.decodeFromString(ChatSync.serializer(), text)
		assertEquals("chat", sync.event.payload.chatId)
	}
}
