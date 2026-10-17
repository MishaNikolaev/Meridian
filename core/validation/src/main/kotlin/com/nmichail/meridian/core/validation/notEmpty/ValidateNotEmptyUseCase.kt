package com.nmichail.meridian.core.validation.notEmpty

import javax.inject.Inject

class ValidateNotEmptyUseCase @Inject constructor() {

	operator fun invoke(data: String): NotEmptyValidationItem =
		if (data.isNotBlank()) {
			NotEmptyValidationItem(
				data = data,
				validationState = NotEmptyValidationState.Valid,
			)
		} else {
			NotEmptyValidationItem(
				data = data,
				validationState = NotEmptyValidationState.Invalid,
			)
		}
}
