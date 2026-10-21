package com.nmichail.meridian.features.authorization.workspace.presentation

sealed interface WorkspaceEvent {

	data object NotFound : WorkspaceEvent

	data object PasswordLength : WorkspaceEvent

	data class NotHandledError(
		val message: Int,
	) : WorkspaceEvent
}
