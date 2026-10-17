package com.nmichail.meridian.core.validation.password

import com.nmichail.meridian.core.validation.DefaultValidationState
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {

	operator fun invoke(password: String): PasswordValidationItem = when {
		PasswordValidator.isLengthInvalid(password) -> PasswordValidationItem(
			data = password,
			validationState = DefaultValidationState.Invalid(PasswordInvalidState.INVALID_LENGTH),
		)
		else -> PasswordValidationItem(
			data = password,
			validationState = DefaultValidationState.Valid,
		)
	}
}
