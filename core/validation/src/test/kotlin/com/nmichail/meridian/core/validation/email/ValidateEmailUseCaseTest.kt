package com.nmichail.meridian.core.validation.email

import com.nmichail.meridian.core.validation.DefaultValidationState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidateEmailUseCaseTest {

	private val useCase = ValidateEmailUseCase()

	@Test
	fun `empty email EXPECT empty`() {
		val expected = EmailValidationItem(
			data = "",
			validationState = DefaultValidationState.Invalid(EmailInvalidState.EMPTY),
		)

		val actual = useCase("")

		assertEquals(expected, actual)
	}

	@Test
	fun `email without at EXPECT invalid`() {
		val expected = EmailValidationItem(
			data = "chief",
			validationState = DefaultValidationState.Invalid(EmailInvalidState.INVALID),
		)

		val actual = useCase("chief")

		assertEquals(expected, actual)
	}

	@Test
	fun `email with domain EXPECT valid`() {
		val expected = EmailValidationItem(
			data = "chief@ftc.ru",
			validationState = DefaultValidationState.Valid,
		)

		val actual = useCase("chief@ftc.ru")

		assertEquals(expected, actual)
	}
}
