package com.nmichail.meridian.core.validation.notEmpty

import com.nmichail.meridian.core.validation.ValidationState

sealed class NotEmptyValidationState : ValidationState {

	data object Unverified : NotEmptyValidationState()

	data object Valid : NotEmptyValidationState()

	data object Invalid : NotEmptyValidationState()
}
