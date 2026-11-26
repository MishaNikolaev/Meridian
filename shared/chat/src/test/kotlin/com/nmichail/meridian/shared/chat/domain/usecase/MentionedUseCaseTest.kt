package com.nmichail.meridian.shared.chat.domain.usecase

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MentionedUseCaseTest {

	private val namedMentionUseCase = NamedMentionUseCase()
	private val mentionedUseCase = MentionedUseCase()

	@Test
	fun `all EXPECT no named mention`() {
		assertEquals(false, namedMentionUseCase("@all посмотрите"))
	}

	@Test
	fun `login EXPECT named mention`() {
		assertEquals(true, namedMentionUseCase("посмотри @chief"))
	}

	@Test
	fun `all EXPECT mentioned without username`() {
		assertEquals(true, mentionedUseCase("@all посмотрите", ""))
	}

	@Test
	fun `username EXPECT mentioned`() {
		assertEquals(true, mentionedUseCase("посмотри @chief", "chief"))
	}

	@Test
	fun `other login EXPECT not mentioned`() {
		assertEquals(false, mentionedUseCase("посмотри @member", "chief"))
	}
}
