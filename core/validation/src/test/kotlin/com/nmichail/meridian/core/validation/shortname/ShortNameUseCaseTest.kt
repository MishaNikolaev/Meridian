package com.nmichail.meridian.core.validation.shortname

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShortNameUseCaseTest {

	private val useCase = ShortNameUseCase()

	@Test
	fun `full name EXPECT first two words`() {
		assertEquals("Волков Сергей", useCase("Волков Сергей Андреевич"))
	}

	@Test
	fun `two words EXPECT same name`() {
		assertEquals("Анна Павловна", useCase("Анна Павловна"))
	}

	@Test
	fun `one word EXPECT same name`() {
		assertEquals("Анна", useCase("Анна"))
	}
}
