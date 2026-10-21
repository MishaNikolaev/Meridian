package com.nmichail.meridian.features.authorization.signin.presentation

sealed interface SignInIntent {

	data class ChangeUsername(
		val value: String,
	) : SignInIntent

	data class ChangePassword(
		val value: String,
	) : SignInIntent

	data object Submit : SignInIntent

	data object OpenRegistration : SignInIntent

	data object OpenWorkspace : SignInIntent

	data object NavigateBack : SignInIntent
}
