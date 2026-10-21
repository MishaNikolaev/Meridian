package com.nmichail.meridian.features.authorization.signin.presentation

import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem

sealed interface SignInState {

	data object Initial : SignInState

	data class Loading(
		val username: UsernameValidationItem,
		val password: PasswordValidationItem,
	) : SignInState

	data class Content(
		val username: UsernameValidationItem,
		val password: PasswordValidationItem,
	) : SignInState

	data class Error(
		val username: UsernameValidationItem,
		val password: PasswordValidationItem,
		val message: Int,
	) : SignInState
}
