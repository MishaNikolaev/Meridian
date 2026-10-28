package com.nmichail.meridian.core.validation.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MentionQueryUseCaseTest {

	private val useCase = MentionQueryUseCase()

	@Test
	fun `trailing at EXPECT query`() {
		assertEquals("Ки", useCase("глянь @Ки"))
	}

	@Test
	fun `plain text EXPECT no query`() {
		assertEquals(null, useCase("без тега"))
	}
}