package com.nmichail.meridian.features.authorization.workspace.presentation

sealed interface WorkspaceEvent {

	data object NotFound : WorkspaceEvent

	data object PasswordLength : WorkspaceEvent

	data object WrongPassword : WorkspaceEvent

	data object NoConnection : WorkspaceEvent

	data object ServerNotFound : WorkspaceEvent

	data object NotOnServer : WorkspaceEvent

	data object DeviceTaken : WorkspaceEvent

	data object Forbidden : WorkspaceEvent

	data object TooManyRequests : WorkspaceEvent

	data object PasswordNotSet : WorkspaceEvent

	data object NotHandledError : WorkspaceEvent
}
