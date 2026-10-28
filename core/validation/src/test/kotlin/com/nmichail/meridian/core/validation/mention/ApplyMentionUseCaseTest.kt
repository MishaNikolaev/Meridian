package com.nmichail.meridian.core.validation.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplyMentionUseCaseTest {

	private val useCase = ApplyMentionUseCase()

	@Test
	fun `pick EXPECT username replaced`() {
		assertEquals(
			"глянь @chief ",
			useCase(
				draft = "глянь @Ки",
				token = "chief",
			),
		)
	}
}