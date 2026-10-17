package com.nmichail.meridian.features.registration.presentation

import com.arkivanov.mvikotlin.core.store.Store
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.displayName.DisplayNameValidationItem
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem

interface RegistrationStore : Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> {

	sealed interface State {

		data object Initial : State

		data class Loading(
			val username: UsernameValidationItem,
			val displayName: DisplayNameValidationItem,
			val password: PasswordValidationItem,
		) : State

		data class Content(
			val username: UsernameValidationItem,
			val displayName: DisplayNameValidationItem,
			val password: PasswordValidationItem,
		) : State

		data class Error(
			val username: UsernameValidationItem,
			val displayName: DisplayNameValidationItem,
			val password: PasswordValidationItem,
			val message: String,
		) : State
	}

	sealed interface Intent {

		data class ChangeUsername(val value: String) : Intent

		data class ChangeDisplayName(val value: String) : Intent

		data class ChangePassword(val value: String) : Intent

		data object Submit : Intent

		data object OpenSignIn : Intent

		data object OpenWorkspace : Intent
	}

	sealed interface Label {

		data object OpenHome : Label

		data object OpenSignIn : Label

		data object OpenWorkspace : Label
	}
}

fun RegistrationStore.State.usernameItem(): UsernameValidationItem = when (this) {
	RegistrationStore.State.Initial -> emptyUsername()
	is RegistrationStore.State.Loading -> username
	is RegistrationStore.State.Content -> username
	is RegistrationStore.State.Error -> username
}

fun RegistrationStore.State.displayNameItem(): DisplayNameValidationItem = when (this) {
	RegistrationStore.State.Initial -> emptyDisplayName()
	is RegistrationStore.State.Loading -> displayName
	is RegistrationStore.State.Content -> displayName
	is RegistrationStore.State.Error -> displayName
}

fun RegistrationStore.State.passwordItem(): PasswordValidationItem = when (this) {
	RegistrationStore.State.Initial -> emptyPassword()
	is RegistrationStore.State.Loading -> password
	is RegistrationStore.State.Content -> password
	is RegistrationStore.State.Error -> password
}

fun RegistrationStore.State.enteredPassword(): String = passwordItem().data

fun RegistrationStore.State.failureMessage(): String? = when (this) {
	is RegistrationStore.State.Error -> message
	RegistrationStore.State.Initial,
	is RegistrationStore.State.Loading,
	is RegistrationStore.State.Content -> null
}

private fun emptyUsername(): UsernameValidationItem = UsernameValidationItem(
	data = "",
	validationState = DefaultValidationState.Unverified,
)

private fun emptyDisplayName(): DisplayNameValidationItem = DisplayNameValidationItem(
	data = "",
	validationState = DefaultValidationState.Unverified,
)

private fun emptyPassword(): PasswordValidationItem = PasswordValidationItem(
	data = "",
	validationState = DefaultValidationState.Unverified,
)
