package com.nmichail.meridian.shared.chat.data.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MentionsTest {

	@Test
	fun `all EXPECT no named mention`() {
		assertEquals(false, namedMention("@all посмотрите"))
	}

	@Test
	fun `login EXPECT named mention`() {
		assertEquals(true, namedMention("посмотри @chief"))
	}

	@Test
	fun `all EXPECT mentioned without username`() {
		assertEquals(true, mentioned("@all посмотрите", ""))
	}

	@Test
	fun `username EXPECT mentioned`() {
		assertEquals(true, mentioned("посмотри @chief", "chief"))
	}

	@Test
	fun `other login EXPECT not mentioned`() {
		assertEquals(false, mentioned("посмотри @member", "chief"))
	}
}
