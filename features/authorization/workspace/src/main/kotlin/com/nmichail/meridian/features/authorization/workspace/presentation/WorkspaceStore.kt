package com.nmichail.meridian.features.authorization.workspace.presentation

import com.arkivanov.mvikotlin.core.store.Store

interface WorkspaceStore : Store<WorkspaceStore.Intent, WorkspaceStore.State, WorkspaceStore.Label> {

	sealed interface State {

		data object Initial : State

		data class Loading(
			val host: String,
			val email: String,
			val password: String,
		) : State

		data class Content(
			val host: String,
			val email: String,
			val password: String,
		) : State

		data class Error(
			val host: String,
			val email: String,
			val password: String,
			val message: String,
		) : State
	}

	sealed interface Intent {

		data class ChangeHost(val value: String) : Intent

		data class ChangeEmail(val value: String) : Intent

		data class ChangePassword(val value: String) : Intent

		data object Submit : Intent

		data object OpenExternalSignIn : Intent

		data object DismissNotice : Intent
	}

	sealed interface Label {

		data object OpenHome : Label

		data object OpenSignIn : Label
	}
}

fun WorkspaceStore.State.enteredHost(): String = when (this) {
	WorkspaceStore.State.Initial -> ""
	is WorkspaceStore.State.Loading -> host
	is WorkspaceStore.State.Content -> host
	is WorkspaceStore.State.Error -> host
}

fun WorkspaceStore.State.enteredEmail(): String = when (this) {
	WorkspaceStore.State.Initial -> ""
	is WorkspaceStore.State.Loading -> email
	is WorkspaceStore.State.Content -> email
	is WorkspaceStore.State.Error -> email
}

fun WorkspaceStore.State.enteredPassword(): String = when (this) {
	WorkspaceStore.State.Initial -> ""
	is WorkspaceStore.State.Loading -> password
	is WorkspaceStore.State.Content -> password
	is WorkspaceStore.State.Error -> password
}

fun WorkspaceStore.State.failureMessage(): String? = when (this) {
	is WorkspaceStore.State.Error -> message
	WorkspaceStore.State.Initial,
	is WorkspaceStore.State.Loading,
	is WorkspaceStore.State.Content -> null
}
