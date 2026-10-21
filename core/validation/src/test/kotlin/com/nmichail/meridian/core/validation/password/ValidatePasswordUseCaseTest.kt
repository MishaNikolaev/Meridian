package com.nmichail.meridian.core.validation.password

import com.nmichail.meridian.core.validation.DefaultValidationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidatePasswordUseCaseTest {

	private val useCase = ValidatePasswordUseCase()

	@Test
	fun `short password EXPECT invalid length`() {
		val expected = PasswordValidationItem(
			data = "short",
			validationState = DefaultValidationState.Invalid(PasswordInvalidState.INVALID_LENGTH),
		)

		val actual = useCase("short")

		assertEquals(expected, actual)
	}

	@Test
	fun `password of eight characters EXPECT valid`() {
		val expected = PasswordValidationItem(
			data = "meridian1",
			validationState = DefaultValidationState.Valid,
		)

		val actual = useCase("meridian1")

		assertEquals(expected, actual)
	}
}
