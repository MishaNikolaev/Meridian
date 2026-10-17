package com.nmichail.meridian.core.validation.username

import com.nmichail.meridian.core.validation.DefaultValidationState
import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor() {

	operator fun invoke(username: String): UsernameValidationItem =
		UsernameValidationItem(
			data = username,
			validationState = validationState(username),
		)

	private fun validationState(username: String): DefaultValidationState =
		when {
			UsernameValidator.isLengthInvalid(username) -> {
				DefaultValidationState.Invalid(UsernameInvalidState.INVALID_LENGTH)
			}
			UsernameValidator.isCharactersInvalid(username) -> {
				DefaultValidationState.Invalid(UsernameInvalidState.INVALID_CHARACTERS)
			}
			else -> DefaultValidationState.Valid
		}
}
