package com.nmichail.meridian.features.registration.presentation

import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem

sealed interface RegistrationState {

	data object Initial : RegistrationState

	data class Loading(
		val username: UsernameValidationItem,
		val displayName: DisplayNameValidationItem,
		val password: PasswordValidationItem,
	) : RegistrationState

	data class Content(
		val username: UsernameValidationItem,
		val displayName: DisplayNameValidationItem,
		val password: PasswordValidationItem,
	) : RegistrationState

	data class Error(
		val username: UsernameValidationItem,
		val displayName: DisplayNameValidationItem,
		val password: PasswordValidationItem,
		val message: Int,
	) : RegistrationState
}
