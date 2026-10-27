package com.nmichail.meridian.shared.chat.domain.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ChatLineTest {

	@Test
	fun `keeping edits EXPECT local text stays`() {
		val remote = listOf(
			ChatLine(
				id = "1",
				mine = true,
				text = "старое",
				sentAt = null,
			),
		)
		val current = listOf(
			ChatLine(
				id = "1",
				mine = true,
				text = "новое",
				sentAt = null,
				edited = true,
			),
		)

		val lines = remote.keepingEdits(current)

		assertEquals("новое", lines.single().text)
		assertEquals(true, lines.single().edited)
	}

	@Test
	fun `merge viewers EXPECT local mine kept until server has it`() {
		val mine = ChatReaction(
			name = "",
			reaction = "👍",
			sentAt = "now",
			mine = true,
		)
		val remote = mapOf(
			"1" to listOf(
				ChatReaction(
					name = "Анна",
					reaction = "🔥",
					sentAt = null,
					mine = false,
				),
			),
		)

		val viewers = mergeReactionViewers(remote, mapOf("1" to listOf(mine)))

		assertEquals(listOf(remote.getValue("1").single(), mine), viewers["1"])
	}
}
