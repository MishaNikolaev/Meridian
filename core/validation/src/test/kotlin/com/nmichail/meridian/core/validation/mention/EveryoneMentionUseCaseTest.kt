package com.nmichail.meridian.core.validation.mention

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EveryoneMentionUseCaseTest {

	private val useCase = EveryoneMentionUseCase()

	@Test
	fun `all prefix EXPECT everyone`() {
		assertEquals(true, useCase("вс"))
	}
}
