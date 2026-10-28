package com.nmichail.meridian.core.validation.initials

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InitialsUseCaseTest {

	private val useCase = InitialsUseCase()

	@Test
	fun `two words EXPECT first letters`() {
		assertEquals("ВС", useCase("Волков Сергей"))
	}

	@Test
	fun `one word EXPECT two letters`() {
		assertEquals("АН", useCase("Анна"))
	}
}
