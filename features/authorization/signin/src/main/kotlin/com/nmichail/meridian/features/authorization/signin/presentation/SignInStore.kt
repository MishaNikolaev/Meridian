package com.nmichail.meridian.features.authorization.signin.presentation

import com.arkivanov.mvikotlin.core.store.Store
import com.nmichail.meridian.core.validation.DefaultValidationState
import com.nmichail.meridian.core.validation.password.PasswordValidationItem
import com.nmichail.meridian.core.validation.username.UsernameValidationItem

interface SignInStore : Store<SignInStore.Intent, SignInStore.State, SignInStore.Label> {

	sealed interface State {

		data object Initial : State

		data class Loading(
			val username: UsernameValidationItem,
			val password: PasswordValidationItem,
		) : State

		data class Content(
			val username: UsernameValidationItem,
			val password: PasswordValidationItem,
		) : State

		data class Error(
			val username: UsernameValidationItem,
			val password: PasswordValidationItem,
			val message: String,
		) : State
	}

	sealed interface Intent {

		data class ChangeUsername(val value: String) : Intent

		data class ChangePassword(val value: String) : Intent

		data object Submit : Intent

		data object OpenRegistration : Intent

		data object OpenWorkspace : Intent
	}

	sealed interface Label {

		data object OpenHome : Label

		data object OpenRegistration : Label

		data object OpenWorkspace : Label
	}
}

fun SignInStore.State.usernameItem(): UsernameValidationItem = when (this) {
	SignInStore.State.Initial -> emptyUsername()
	is SignInStore.State.Loading -> username
	is SignInStore.State.Content -> username
	is SignInStore.State.Error -> username
}

fun SignInStore.State.passwordItem(): PasswordValidationItem = when (this) {
	SignInStore.State.Initial -> emptyPassword()
	is SignInStore.State.Loading -> password
	is SignInStore.State.Content -> password
	is SignInStore.State.Error -> password
}

fun SignInStore.State.enteredPassword(): String = passwordItem().data

fun SignInStore.State.failureMessage(): String? = when (this) {
	is SignInStore.State.Error -> message
	SignInStore.State.Initial,
	is SignInStore.State.Loading,
	is SignInStore.State.Content -> null
}

private fun emptyUsername(): UsernameValidationItem = UsernameValidationItem(
	data = "",
	validationState = DefaultValidationState.Unverified,
)

private fun emptyPassword(): PasswordValidationItem = PasswordValidationItem(
	data = "",
	validationState = DefaultValidationState.Unverified,
)
