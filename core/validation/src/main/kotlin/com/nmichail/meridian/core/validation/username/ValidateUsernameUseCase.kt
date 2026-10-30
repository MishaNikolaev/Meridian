package com.nmichail.meridian.core.validation.username

import javax.inject.Inject

class ValidateUsernameUseCase @Inject constructor() {

	operator fun invoke(username: String): UsernameValidationItem =
		UsernameValidationItem(
			data = username,
			validationState = UsernameValidator.validate(username),
		)
}
