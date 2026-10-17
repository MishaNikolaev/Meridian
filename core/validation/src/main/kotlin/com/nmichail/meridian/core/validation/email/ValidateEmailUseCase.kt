package com.nmichail.meridian.core.validation.email

import com.nmichail.meridian.core.validation.DefaultValidationState
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {

	operator fun invoke(email: String): EmailValidationItem = when {
		EmailValidator.isEmpty(email) -> EmailValidationItem(
			data = email,
			validationState = DefaultValidationState.Invalid(EmailInvalidState.EMPTY),
		)
		EmailValidator.isInvalid(email) -> EmailValidationItem(
			data = email,
			validationState = DefaultValidationState.Invalid(EmailInvalidState.INVALID),
		)
		else -> EmailValidationItem(
			data = email,
			validationState = DefaultValidationState.Valid,
		)
	}
}
