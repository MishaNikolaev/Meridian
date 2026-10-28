package com.nmichail.meridian.features.authorization.workspace.presentation

sealed interface WorkspaceEvent {

	data object NotFound : WorkspaceEvent

	data object PasswordLength : WorkspaceEvent

	data object WrongPassword : WorkspaceEvent

	data object NoConnection : WorkspaceEvent

	data object NotHandledError : WorkspaceEvent
}
