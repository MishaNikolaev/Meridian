package com.nmichail.meridian.features.authorization.workspace.presentation

sealed interface WorkspaceIntent {

	data class ChangeHost(
		val value: String,
	) : WorkspaceIntent

	data class ChangeEmail(
		val value: String,
	) : WorkspaceIntent

	data class ChangePassword(
		val value: String,
	) : WorkspaceIntent

	data object Submit : WorkspaceIntent

	data object OpenExternalSignIn : WorkspaceIntent

	data object NavigateBack : WorkspaceIntent
}
