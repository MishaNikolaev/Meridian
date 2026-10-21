package com.nmichail.meridian.features.registration.presentation

sealed interface RegistrationIntent {

	data class ChangeUsername(
		val value: String,
	) : RegistrationIntent

	data class ChangeDisplayName(
		val value: String,
	) : RegistrationIntent

	data class ChangePassword(
		val value: String,
	) : RegistrationIntent

	data object Submit : RegistrationIntent

	data object OpenSignIn : RegistrationIntent

	data object OpenWorkspace : RegistrationIntent

	data object NavigateBack : RegistrationIntent
}
