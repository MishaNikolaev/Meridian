package com.nmichail.meridian.features.authorization.workspace.presentation

sealed interface WorkspaceState {

	data object Initial : WorkspaceState

	data class Loading(
		val host: String,
		val email: String,
		val password: String,
	) : WorkspaceState

	data class Content(
		val host: String,
		val email: String,
		val password: String,
	) : WorkspaceState

	data class Error(
		val host: String,
		val email: String,
		val password: String,
	) : WorkspaceState
}
