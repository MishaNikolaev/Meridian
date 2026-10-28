package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.DefaultValidationState
import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor() {

	operator fun invoke(username: String): UsernameValidationItem =
		UsernameValidationItem(
			data = username,
			validationState = when {
				UsernameValidator.isLengthInvalid(username) -> {
					DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH)
				}
				UsernameValidator.isCharactersInvalid(username) -> {
					DefaultValidationState.Invalid(UsernameInvalidState.INVALID_CHARACTERS)
				}
				else -> DefaultValidationState.Valid
			},
		)
}
