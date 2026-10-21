package com.nmichail.meridian.core.validation.notEmpty

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidateNotEmptyUseCaseTest {

	private val useCase = ValidateNotEmptyUseCase()

	@Test
	fun `blank value EXPECT invalid`() {
		val expected = NotEmptyValidationItem(
			data = " ",
			validationState = NotEmptyValidationState.Invalid,
		)

		val actual = useCase(" ")

		assertEquals(expected, actual)
	}

	@Test
	fun `filled value EXPECT valid`() {
		val expected = NotEmptyValidationItem(
			data = "meridian.ftc.ru",
			validationState = NotEmptyValidationState.Valid,
		)

		val actual = useCase("meridian.ftc.ru")

		assertEquals(expected, actual)
	}
}
