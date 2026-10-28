package com.nmichail.meridian.core.validation.initials

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InitialsValidatorTest {

	@Test
	fun `two words EXPECT first letters`() {
		assertEquals("ВС", InitialsValidator.of("Волков Сергей"))
	}

	@Test
	fun `one word EXPECT two letters`() {
		assertEquals("АН", InitialsValidator.of("Анна"))
	}
}
