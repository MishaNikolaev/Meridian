package com.nmichail.meridian.core.validation.displayName

import com.nmichail.meridian.core.validation.DefaultValidationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidateDisplayNameUseCaseTest {

	private val useCase = ValidateDisplayNameUseCase()

	@Test
	fun `blank display name EXPECT empty`() {
		val expected = DisplayNameValidationItem(
			data = " ",
			validationState = DefaultValidationState.Invalid(DisplayNameInvalidState.EMPTY),
		)

		val actual = useCase(" ")

		assertEquals(expected, actual)
	}

	@Test
	fun `display name longer than limit EXPECT too long`() {
		val name = "а".repeat(129)
		val expected = DisplayNameValidationItem(
			data = name,
			validationState = DefaultValidationState.Invalid(DisplayNameInvalidState.TOO_LONG),
		)

		val actual = useCase(name)

		assertEquals(expected, actual)
	}

	@Test
	fun `display name EXPECT valid`() {
		val expected = DisplayNameValidationItem(
			data = "Анна",
			validationState = DefaultValidationState.Valid,
		)

		val actual = useCase("Анна")

		assertEquals(expected, actual)
	}
}
