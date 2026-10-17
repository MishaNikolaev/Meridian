package com.nmichail.meridian.core.validation.displayName

import com.nmichail.meridian.core.validation.DefaultValidationState
import javax.inject.Inject

class ValidateDisplayNameUseCase @Inject constructor() {

	operator fun invoke(displayName: String): DisplayNameValidationItem =
		DisplayNameValidationItem(
			data = displayName,
			validationState = validationState(displayName),
		)

	private fun validationState(displayName: String): DefaultValidationState =
		when {
			DisplayNameValidator.isEmpty(displayName) -> {
				DefaultValidationState.Invalid(DisplayNameInvalidState.EMPTY)
			}
			DisplayNameValidator.isTooLong(displayName) -> {
				DefaultValidationState.Invalid(DisplayNameInvalidState.TOO_LONG)
			}
			else -> DefaultValidationState.Valid
		}
}
