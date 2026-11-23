package com.nmichail.meridian.core.validation.capitalize

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CapitalizeUseCaseTest {

	private val useCase = CapitalizeUseCase()

	@Test
	fun `lower first letter EXPECT title case`() {
		assertEquals("Стажёр", useCase("стажёр"))
	}

	@Test
	fun `upper first letter EXPECT same text`() {
		assertEquals("Стажёр", useCase("Стажёр"))
	}

	@Test
	fun `empty EXPECT empty`() {
		assertEquals("", useCase(""))
	}
}
