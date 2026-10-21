package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.DefaultValidationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidateUsernameUseCaseTest {

	private val useCase = ValidateUsernameUseCase()

	@Test
	fun `short username EXPECT invalid length`() {
		val expected = UsernameValidationItem(
			data = "ab",
			validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH),
		)

		val actual = useCase("ab")

		assertEquals(expected, actual)
	}

	@Test
	fun `username with space EXPECT invalid characters`() {
		val expected = UsernameValidationItem(
			data = "a b",
			validationState = DefaultValidationState.Invalid(UsernameInvalidState.INVALID_CHARACTERS),
		)

		val actual = useCase("a b")

		assertEquals(expected, actual)
	}

	@Test
	fun `allowed username EXPECT valid`() {
		val expected = UsernameValidationItem(
			data = "chief",
			validationState = DefaultValidationState.Valid,
		)

		val actual = useCase("chief")

		assertEquals(expected, actual)
	}
}
