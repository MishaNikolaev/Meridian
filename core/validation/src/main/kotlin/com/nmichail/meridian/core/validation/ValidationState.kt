package com.nmichail.meridian.core.validation

interface ValidationState

interface InvalidState

sealed class DefaultValidationState : ValidationState {

	data object Unverified : DefaultValidationState()

	data object Valid : DefaultValidationState()

	data class Invalid(val invalidState: InvalidState) : DefaultValidationState()
}
